package com.talool.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.thrift.TException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.StatelessSession;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.transform.Transformers;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernatespatial.GeometryUserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.googlecode.genericdao.dao.hibernate.DAODispatcher;
import com.googlecode.genericdao.search.Search;
import com.talool.core.AccountType;
import com.talool.core.AcquireStatus;
import com.talool.core.ActivationCode;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.DealType;
import com.talool.core.DevicePresence;
import com.talool.core.FavoriteMerchant;
import com.talool.core.IdentifiableUUID;
import com.talool.core.Location;
import com.talool.core.Merchant;
import com.talool.core.Relationship;
import com.talool.core.SearchOptions;
import com.talool.core.activity.Activity;
import com.talool.core.gift.EmailGift;
import com.talool.core.gift.FaceBookGift;
import com.talool.core.gift.Gift;
import com.talool.core.gift.GiftStatus;
import com.talool.core.purchase.UniqueCodeStrategy;
import com.talool.core.service.CustomerService;
import com.talool.core.service.InvalidInputException;
import com.talool.core.service.NotFoundException;
import com.talool.core.service.ProcessorException;
import com.talool.core.service.ServiceException;
import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.SocialNetwork;
import com.talool.domain.ActivationCodeImpl;
import com.talool.domain.CustomerCriteria;
import com.talool.domain.CustomerImpl;
import com.talool.domain.DealAcquireImpl;
import com.talool.domain.DealOfferPurchaseImpl;
import com.talool.domain.DevicePresenceImpl;
import com.talool.domain.FavoriteMerchantImpl;
import com.talool.domain.PropertyCriteria;
import com.talool.domain.PropertyCriteria.Filter;
import com.talool.domain.RelationshipImpl;
import com.talool.domain.activity.ActivityImpl;
import com.talool.domain.gift.EmailGiftImpl;
import com.talool.domain.gift.FacebookGiftImpl;
import com.talool.domain.gift.GiftImpl;
import com.talool.domain.social.CustomerSocialAccountImpl;
import com.talool.hibernate.MerchantAcquiresResultTransformer;
import com.talool.payment.PaymentDetail;
import com.talool.payment.TransactionResult;
import com.talool.payment.braintree.BraintreeUtil;
import com.talool.payment.braintree.BraintreeUtil.RefundVoidResult;
import com.talool.persistence.QueryHelper;
import com.talool.persistence.QueryHelper.QueryType;
import com.talool.service.mail.EmailRequestParams;
import com.talool.stats.CustomerSummary;
import com.talool.stats.PaginatedResult;
import com.talool.utils.GraphiteConstants.Action;
import com.talool.utils.GraphiteConstants.SubAction;
import com.talool.utils.KeyValue;
import com.talool.utils.TaloolStatsDClient;
import com.talool.utils.ValidatorUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * 
 * @author clintz
 * 
 */
@Transactional(readOnly = true)
@Service
@Repository
public class CustomerServiceImpl extends AbstractHibernateService implements CustomerService {
  private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);

  private static final String IGNORE_TEST_EMAIL_DOMAIN = "test.talool.com";

  private static final ThreadLocal<Map<String, String>> requestHeaders = new ThreadLocal<Map<String, String>>();

  private UniqueCodeStrategy redemptionCodeStrategy;

  private EventBus purchaseEventBus = new EventBus("PurchaseEventBus");

  public CustomerServiceImpl() {
    purchaseEventBus.register(new PurchaseListener());
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void createAccount(final Customer customer, final String password, final UUID whiteLabelPublisherMerchantId) throws ServiceException {

    // Talool app only supports emails for now.
    if (whiteLabelPublisherMerchantId == null) {
      if (!EmailValidator.getInstance().isValid(customer.getEmail())) {
        throw new ServiceException(ErrorCode.VALID_EMAIL_REQUIRED);
      }
    } else {
      // we are validating white label usernames and emails in the same field
      if (customer.getEmail().contains("@")) {
        if (!EmailValidator.getInstance().isValid(customer.getEmail())) {
          throw new ServiceException(ErrorCode.VALID_EMAIL_OPTIONAL);
        }
      } else if (!ValidatorUtils.isValidUsername(customer.getEmail())) {
        throw new ServiceException(ErrorCode.VALID_USERNAME_OPTIONAL);
      }
    }


    if (StringUtils.isEmpty(password)) {
      throw new ServiceException(ErrorCode.PASS_REQUIRED);
    }

    createAccount(AccountType.CUS, customer, password, whiteLabelPublisherMerchantId);

    // We are not sending registration emails in order to avoid 3rd-part email
    // costs

    // ServiceFactory.get().getEmailService().
    // sendCustomerRegistrationEmail(new EmailRequestParams<Customer>(customer,
    // true));

    final List<Gift> gifts = getGifts(customer.getId(), GiftStatus.values());
    final List<Activity> activities = new ArrayList<Activity>();
    Merchant whiteLabelMerchant = null;

    try {
      if (whiteLabelPublisherMerchantId != null) {
        whiteLabelMerchant = ServiceFactory.get().getTaloolService().getMerchantById(whiteLabelPublisherMerchantId);
      }
      // add welcome message!
      activities.add(ActivityFactory.createWelcome(customer.getId(), gifts.size(), whiteLabelMerchant));
    } catch (Exception ex) {
      LOG.error("Problem creating welcome activity: " + ex.getLocalizedMessage(), ex);
    }

    if (CollectionUtils.isNotEmpty(gifts)) {
      for (final Gift gift : gifts) {
        gift.setToCustomer(customer);
        daoDispatcher.save(gift);

        try {
          final Activity act = ActivityFactory.createRecvGift(gift);
          act.setGiftId(gift.getId());
          activities.add(act);
        } catch (Exception e) {
          LOG.error("Problem creating activity for new user " + customer.getEmail());
        }
      }
    }

    try {
      ServiceFactory.get().getActivityService().save(activities);
      if (LOG.isDebugEnabled()) {
        LOG.debug(String.format("Sending %d gift activities for new customer %s %s", gifts.size(), customer.getEmail(), customer.getId()));
      }
    } catch (Exception e) {
      LOG.error("Problem saving activities for new user " + customer.getEmail());
    }

    TaloolStatsDClient.get().count(Action.registration, null, null, requestHeaders.get());

  }

  private static class GiftOwnership {
    Gift gift;
    Customer receivingCustomer;
  }

  private void createAccount(final AccountType accountType, final IdentifiableUUID account, final String password,
      final UUID whiteLabelPublisherMerchantId) throws ServiceException {
    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Creating accountType:" + accountType + ": " + account.toString());
      }

      final CustomerImpl customerImpl = ((CustomerImpl) (account));
      // set encrypted password
      customerImpl.setPassword(password);
      customerImpl.setEmail(customerImpl.getEmail().toLowerCase().trim());

      if (whiteLabelPublisherMerchantId != null) {
        customerImpl.setWhiteLabelMerchantId(whiteLabelPublisherMerchantId);
      }

      save((CustomerImpl) account);
      daoDispatcher.flush(CustomerImpl.class);
      daoDispatcher.refresh((CustomerImpl) account);

      // create any neccsary activities

    } catch (Exception e) {
      final String err = "There was a problem registering  " + account;
      LOG.error(err, e);
      throw new ServiceException(err, e);
    }

  }

  @Override
  public void removeCustomer(UUID id) throws ServiceException {
    // TODO Auto-generated method stub

  }

  @Override
  public Customer authenticateCustomer(final String email, final String password) throws ServiceException {
    final Search search = new Search(CustomerImpl.class);

    if (requestHeaders.get() != null) {
      for (Entry<String, String> entry : requestHeaders.get().entrySet()) {
        LOG.info("Got header: " + entry.getKey() + " with value: " + entry.getValue());
      }

    }

    search.addFilterEqual("email", email.toLowerCase().trim());
    try {
      search.addFilterEqual("password", EncryptService.MD5(password));
    } catch (Exception ex) {
      throw new ServiceException("Problem authenticating", ex);
    }

    TaloolStatsDClient.get().count(Action.authenticate, SubAction.user, null, requestHeaders.get());

    return (Customer) daoDispatcher.searchUnique(search);

  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void save(final Customer customer) throws ServiceException {
    try {
      daoDispatcher.save(customer);
    } catch (Exception e) {
      final String err = "There was a problem saving customer " + customer;
      LOG.error(err, e);
      throw new ServiceException(err, e);
    }
  }

  @Override
  public Customer getCustomerById(final UUID id) throws ServiceException {
    Customer customer;
    try {
      customer = daoDispatcher.find(CustomerImpl.class, id);
    } catch (Exception ex) {
      throw new ServiceException("Problem getCustomerById  " + id, ex);
    }

    return customer;
  }

  @Override
  public Customer getCustomerByEmail(final String email) throws ServiceException, InvalidInputException {
    Customer customer = null;

    if (!EmailValidator.getInstance().isValid(email)) {
      throw new InvalidInputException(ErrorCode.VALID_EMAIL_REQUIRED, email);
    }

    try {
      Search search = new Search(CustomerImpl.class);
      search.addFilterEqual("email", email.toLowerCase());
      customer = (Customer) daoDispatcher.searchUnique(search);
    } catch (Exception ex) {
      throw new ServiceException("Problem getCustomerByEmail  " + email, ex);
    }

    return customer;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Customer> getCustomers() throws ServiceException {
    try {
      final Search search = new Search(CustomerImpl.class);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getCustomers"), ex);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final Relationship relationship) throws ServiceException {
    try {
      daoDispatcher.save(relationship);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem saving relationship fromCustomer '%s' toCustomer '%s' " + relationship.getFromCustomer(),
          relationship.getToCustomer(), ex));
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Customer> getFriends(final UUID id) throws ServiceException {
    try {
      final Query query =
          sessionFactory.getCurrentSession().createQuery(
              "from CustomerImpl c, RelationshipImpl r where c.id=r.customer.id and r.friend.id=:customerId");

      query.setParameter("customerId", id);
      return query.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getFriends %s", id), ex);
    }
  }

  /*
   * Redeems a deal. Uses a StatelessSession . Be careful with stateless sessions:
   * 
   * http://docs.jboss.org/hibernate/core/3.3/api/org/hibernate/StatelessSession. html
   * 
   * The reason a StatelessSession is used is so we dont break the transaction upon any DB error,
   * particularly a ConstraintViolation because we optimistically believe redemptionCode is unique.
   * If by chance it isn't, we regenerate a new one.
   */
  @Override
  @Transactional(propagation = Propagation.NESTED)
  public String redeemDeal(final UUID dealAcquireId, final UUID customerId, final Location location) throws ServiceException {
    String redemptionCode = null;
    Query query = null;
    final StatelessSession statelessSession = getSessionFactory().openStatelessSession();

    try {
      // we need to first check owning customer and acquireStatus
      query = statelessSession.getNamedQuery("getCustomerIdAcquireStatusOnDealAcquire");
      query.setParameter("dealAcquireId", dealAcquireId);
      final Object[] acquireFields = (Object[]) query.uniqueResult();
      final UUID owningCustomer = (UUID) acquireFields[0];
      final AcquireStatus acquireStatus = (AcquireStatus) acquireFields[1];

      if (!owningCustomer.equals(customerId)) {
        throw new ServiceException(ErrorCode.CUSTOMER_DOES_NOT_OWN_DEAL, "Customer does not own deal");
      }

      if (AcquireStatus.REDEEMED == acquireStatus) {
        throw new ServiceException("Cannot redeem already redeemed deal " + dealAcquireId);
      }

      if (!AcquireStatus.ACCEPTED_CUSTOMER_SHARE.equals(acquireStatus) && !AcquireStatus.REJECTED_CUSTOMER_SHARE.equals(acquireStatus) &&

      !AcquireStatus.ACCEPTED_MERCHANT_SHARE.equals(acquireStatus) && !AcquireStatus.PURCHASED.equals(acquireStatus)) {
        throw new ServiceException(String.format("AcquireStatus %s is not in proper state to redeem for dealAcquireId %s", acquireStatus,
            dealAcquireId) + dealAcquireId);
      }

    } catch (ServiceException se) {
      throw se;
    } catch (Exception ex) {
      LOG.error("Problem redeeming deal: " + ex.getLocalizedMessage(), ex);
      throw new ServiceException("Problem in redeemDeal with dealAcquireId " + dealAcquireId, ex);
    }

    try {
      query = statelessSession.getNamedQuery("redeemDealAcquire");
      redemptionCode = redemptionCodeStrategy.generateCode();
      query.setParameter("dealAcquireStatus", AcquireStatus.REDEEMED);
      query.setParameter("dealAcquireId", dealAcquireId);

      final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);

      final Point point =
          (location == null || location.getLatitude() == null || location.getLongitude() == null) ? null : factory.createPoint(new Coordinate(
              location.getLongitude(), location.getLatitude()));

      query.setParameter("redeemedAtGeometry", point, GeometryUserType.TYPE);
      query.setParameter("redemptionCode", redemptionCode);
      query.setParameter("redemptionDate", Calendar.getInstance().getTime());
      query.executeUpdate();
    } catch (ConstraintViolationException ce) {
      // try one more time with a new redemptionCode
      LOG.error("Constraint violation: " + ce.getSQLException().getMessage(), ce);
      redemptionCode = redemptionCodeStrategy.generateCode();
      LOG.warn("Regenerated redemptionCode: " + redemptionCode);
      query.setParameter("redemptionCode", redemptionCode);
      query.executeUpdate();
    } catch (Exception e) {
      LOG.error("Problem redeeming deal: " + e.getLocalizedMessage(), e);
      throw new ServiceException("Problem in redeemDeal with dealAcquireId " + dealAcquireId, e);
    }

    // get the deal id for tracking, cuz the deal acquire id is kinda
    // meaningless
    UUID dealId = null;
    try {
      dealId = getDealAcquire(dealAcquireId).getDeal().getId();
    } catch (Exception e) {
    }
    TaloolStatsDClient.get().count(Action.redemption, null, dealId, requestHeaders.get());

    return redemptionCode;

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Relationship> getRelationshipsFrom(final UUID customerId) throws ServiceException {
    try {
      final Search search = new Search(RelationshipImpl.class);
      search.addFilterEqual("fromCustomer.id", customerId);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getRelationshipsFrom %s", customerId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Relationship> getRelationshipsTo(final UUID customerId) throws ServiceException {
    try {
      final Search search = new Search(RelationshipImpl.class);
      search.addFilterEqual("toCustomer.id", customerId);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getRelationshipsTo %s", customerId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealAcquire> getDealAcquires(final UUID customerId, final UUID merchantId, final SearchOptions searchOpts) throws ServiceException {
    Calendar c = Calendar.getInstance();
    c.roll(Calendar.YEAR, -100);
    Date expiresAfter = c.getTime(); // 100 years ago
    return getDealAcquires(customerId, merchantId, searchOpts, expiresAfter);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealAcquire> getDealAcquires(final UUID customerId, final UUID merchantId, final SearchOptions searchOpts, final Date expiresAfter)
      throws ServiceException {
    try {
      final String newSql = QueryHelper.buildQuery(QueryType.DealAcquires, null, searchOpts, true);

      final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
      query.setParameter("customerId", customerId);
      query.setParameter("merchantId", merchantId);
      query.setParameter("expiresAfter", expiresAfter);
      QueryHelper.applyOffsetLimit(query, searchOpts);

      TaloolStatsDClient.get().count(Action.get_deal_acquires, null, null, requestHeaders.get());

      return query.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealAcquires customerId %s merchantId %s", customerId, merchantId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Merchant> getMerchantAcquires(final UUID customerId, final SearchOptions searchOpts) throws ServiceException {
    try {
      final String newSql = QueryHelper.buildQuery(QueryType.MerchantAcquires, null, searchOpts, true);

      final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
      query.setParameter("customerId", customerId);
      QueryHelper.applyOffsetLimit(query, searchOpts);

      TaloolStatsDClient.get().count(Action.get_merchant_acquires, null, null, requestHeaders.get());


      return query.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getMerchantAcquires customerId %s", customerId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Merchant> getMerchantAcquires(final UUID customerId, final SearchOptions searchOpts, final Location location) throws ServiceException {
    if (location == null) {
      return getMerchantAcquires(customerId, searchOpts);
    }

    List<Merchant> merchants = null;

    try {
      final org.postgis.Point point = new org.postgis.Point(location.getLongitude(), location.getLatitude());
      point.setSrid(4326);

      final ImmutableMap<String, Object> params = ImmutableMap.<String, Object>builder().put("point", point.toString()).build();

      final String newSql = QueryHelper.buildQuery(QueryType.MerchantAcquiresLocation, params, searchOpts, true);

      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);

      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("merchantName", StandardBasicTypes.STRING);
      query.addScalar("categoryId", StandardBasicTypes.INTEGER);

      query.addScalar("merchant_location_id", StandardBasicTypes.LONG);
      query.addScalar("merchant_location_name", StandardBasicTypes.STRING);
      query.addScalar("email", StandardBasicTypes.STRING);
      query.addScalar("website_url", StandardBasicTypes.STRING);
      query.addScalar("phone", StandardBasicTypes.STRING);
      query.addScalar("address1", StandardBasicTypes.STRING);
      query.addScalar("address2", StandardBasicTypes.STRING);
      query.addScalar("city", StandardBasicTypes.STRING);
      query.addScalar("state_province_county", StandardBasicTypes.STRING);
      query.addScalar("zip", StandardBasicTypes.STRING);
      query.addScalar("country", StandardBasicTypes.STRING);
      query.addScalar("geom", GeometryUserType.TYPE);

      query.addScalar("logo_url_id", StandardBasicTypes.STRING);
      query.addScalar("merchant_image_id", StandardBasicTypes.STRING);

      query.addScalar("distanceInMeters", StandardBasicTypes.DOUBLE);
      query.setResultTransformer(new MerchantAcquiresResultTransformer());

      query.setParameter("customerId", customerId, PostgresUUIDType.INSTANCE);

      QueryHelper.applyOffsetLimit(query, searchOpts);

      merchants = query.list();

    } catch (Exception ex) {
      String msg = "Problem getting merchants acquired in location ";
      LOG.error(msg, ex);
      throw new ServiceException(msg, ex);
    }

    TaloolStatsDClient.get().count(Action.get_merchant_acquires, null, null, requestHeaders.get());

    return merchants;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealAcquire> getDealAcquiresByCustomerId(final UUID customerId) throws ServiceException {
    try {
      final Search search = new Search(DealAcquireImpl.class);
      search.addFilterEqual("customer.id", customerId);

      TaloolStatsDClient.get().count(Action.get_deal_acquires, null, null, requestHeaders.get());

      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealAcquiresByCustomerId %s", customerId), ex);
    }
  }

  @Override
  public DealAcquire getDealAcquire(final UUID dealAcquireId) throws ServiceException {
    DealAcquire dac = null;

    try {
      dac = (DealAcquire) getCurrentSession().load(DealAcquireImpl.class, dealAcquireId);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealAcquire %s", dealAcquireId), ex);
    }

    return dac;

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Deal> getDealsByCustomerId(final UUID accountId) throws ServiceException {
    try {
      final Query query =
          sessionFactory.getCurrentSession().createQuery(
              "from MerchantDealImpl md, DealBookPurchaseImpl dbp where dbp.merchantId=md.id and dbp.customerId=:customerId");

      query.setParameter("customerId", accountId);
      return query.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealsByCustomerId %s", accountId), ex);
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealOfferPurchase> getDealOfferPurchasesByCustomerId(final UUID customerId) throws ServiceException {
    try {
      final Search search = new Search(DealOfferPurchaseImpl.class);
      search.addFilterEqual("customer.id", customerId);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealBookPurchasesByCustomerId %s", customerId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Merchant> getMerchantsByCustomerId(final Long customerId) throws ServiceException {
    try {
      final Query query =
          sessionFactory
              .getCurrentSession()
              .createQuery(
                  "select distinct m from DealBookPurchaseImpl dbp,  MerchantDealImpl md, MerchantImpl m, DealBookContentImpl dbc "
                      + "where dbp.customer.id=:customerId AND dbp.dealBook.id=dbc.dealBook.id AND dbc.merchantDeal.merchant.id=md.merchant.id AND dbc.merchantDeal.merchant.id=m.id");

      query.setParameter("customerId", customerId);

      TaloolStatsDClient.get().count(Action.get_merchants, null, null, requestHeaders.get());

      return query.list();

    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getMerchantsByCustomerId %s", customerId), ex);
    }
  }

  public DAODispatcher getDaoDispatcher() {
    return daoDispatcher;
  }

  public void setDaoDispatcher(DAODispatcher daoDispatcher) {
    this.daoDispatcher = daoDispatcher;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void addFavoriteMerchant(final UUID customerId, final UUID merchantId) throws ServiceException {
    final FavoriteMerchant favMerchant = new FavoriteMerchantImpl(customerId, merchantId);

    try {
      daoDispatcher.save(favMerchant);
      getCurrentSession().flush();
    } catch (ConstraintViolationException ce) {
      LOG.warn(String.format("favorite merch was out-of-sync customerId '%s' and merchant '%s'", customerId, merchantId));
    } catch (Exception e) {
      throw new ServiceException(String.format("There was a problem adding favorite merch: customerId %s merchantId %s", customerId, merchantId));
    }

    TaloolStatsDClient.get().count(Action.favorite, SubAction.add, merchantId, requestHeaders.get());

  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void removeFavoriteMerchant(final UUID customerId, final UUID merchantId) throws ServiceException {
    try {
      final Search search = new Search(FavoriteMerchantImpl.class).addFilterEqual("customerId", customerId).addFilterEqual("merchantId", merchantId);
      final FavoriteMerchant favMerchant = (FavoriteMerchant) daoDispatcher.searchUnique(search);
      if (favMerchant != null) {
        daoDispatcher.remove(favMerchant);
      } else {
        LOG.warn(String.format("Ignoring remove of favorite merchant (not found) customerId %s merchantId %s", customerId, merchantId));
      }

    } catch (Exception e) {
      throw new ServiceException(String.format("There was a problem removing favorite merchant: customerId %s merchantId %s", customerId, merchantId));
    }

    TaloolStatsDClient.get().count(Action.favorite, SubAction.remove, merchantId, requestHeaders.get());

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Merchant> getFavoriteMerchants(final UUID customerId, final SearchOptions searchOpts) throws ServiceException {
    try {
      final String newSql = QueryHelper.buildQuery(QueryType.FavoriteMerchants, null, searchOpts, true);

      final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
      query.setParameter("customerId", customerId);
      QueryHelper.applyOffsetLimit(query, searchOpts);

      TaloolStatsDClient.get().count(Action.get_favorites, null, null, requestHeaders.get());

      return query.list();

    } catch (Exception ex) {
      throw new ServiceException("Problem getting favorite merchant for customerId " + customerId, ex);
    }

  }

  @Transactional(propagation = Propagation.NESTED)
  public DealOfferPurchase createDealOfferPurchase(final Customer customer, final DealOffer dealOffer, final TransactionResult transactionResult,
      final Map<String, String> paymentProperties) throws ServiceException {
    try {
      final DealOfferPurchase purchase = new DealOfferPurchaseImpl(customer, dealOffer);
      purchase.setPaymentProcessor(transactionResult.getPaymentProcessor());
      purchase.setProcessorTransactionId(transactionResult.getTransactionId());

      // store payment receipt
      if (transactionResult.getPaymentReceipt() != null) {
        purchase.getProperties().createOrReplace(KeyValue.paymentReceipt, transactionResult.getPaymentReceipt().getDisplay());
      }

      // save any props
      if (MapUtils.isNotEmpty(paymentProperties)) {
        for (Entry<String, String> entry : paymentProperties.entrySet()) {
          purchase.getProperties().createOrReplace(entry.getKey(), entry.getValue());
        }
      }
      daoDispatcher.save(purchase);
      return purchase;
    } catch (Exception ex) {
      throw new ServiceException(
          String.format("Problem creating dealOfferPurchase customerId %s dealOfferId %s", customer.getId(), dealOffer.getId()), ex);
    }

  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void createDealOfferPurchase(final UUID customerId, final UUID dealOfferId) throws ServiceException {
    try {
      final SQLQuery query =
          getCurrentSession().createSQLQuery("insert into public.deal_offer_purchase (customer_id,deal_offer_id) values (:customerId,:dealOfferId)");

      query.setParameter("customerId", customerId, PostgresUUIDType.INSTANCE);
      query.setParameter("dealOfferId", dealOfferId, PostgresUUIDType.INSTANCE);

      query.executeUpdate();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem creating dealOfferPurchase customerId %s dealOfferId %s", customerId, dealOfferId), ex);
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Merchant> getMerchantAcquires(final UUID customerId, final Integer categoryId, final SearchOptions searchOpts) throws ServiceException {
    try {
      final String newSql = QueryHelper.buildQuery(QueryType.MerchantAcquiresByCatId, null, searchOpts, true);

      final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
      query.setParameter("customerId", customerId);
      query.setParameter("categoryId", categoryId);
      QueryHelper.applyOffsetLimit(query, searchOpts);

      TaloolStatsDClient.get().count(Action.get_merchant_acquires, null, null, requestHeaders.get());

      return query.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getMerchantAcquires customerId %s categoryId %s", customerId, categoryId, ex));
    }
  }

  @Transactional(propagation = Propagation.NESTED)
  public void createGift(final UUID owningCustomerId, final UUID dealAcquireId, final Gift gift, final Long jobId) throws ServiceException {
    final DealAcquire dac = getDealAcquire(dealAcquireId);
    Customer toCust = null;
    Activity sendActivity = null;
    Activity recvActivity = null;

    if (!dac.getCustomer().getId().equals(owningCustomerId)) {
      throw new ServiceException(ErrorCode.CUSTOMER_DOES_NOT_OWN_DEAL, "dealAcquireId: " + dac.getId() + ", badCustomerId: "
          + dac.getCustomer().getId());
    }

    final AcquireStatus currentAcquireStatus = dac.getAcquireStatus();

    if (currentAcquireStatus == AcquireStatus.REDEEMED) {
      throw new ServiceException(ErrorCode.DEAL_ALREADY_REDEEMED, "dealAcquireId: " + dac.getId());
    }

    // Can only gift a deal that is in a valid state!
    if (currentAcquireStatus != AcquireStatus.ACCEPTED_CUSTOMER_SHARE && currentAcquireStatus != AcquireStatus.ACCEPTED_MERCHANT_SHARE
        && currentAcquireStatus != AcquireStatus.REJECTED_CUSTOMER_SHARE && currentAcquireStatus != AcquireStatus.REJECTED_MERCHANT_SHARE
        && currentAcquireStatus != AcquireStatus.PURCHASED) {
      throw new ServiceException(ErrorCode.GIFTING_NOT_ALLOWED, String.format("acquireStatus %s for dealAcquireId %s", currentAcquireStatus,
          dac.getId()));
    }

    try {

      gift.setDealAcquire(dac);
      gift.setFromCustomer(dac.getCustomer());

      daoDispatcher.save(gift);

      // Send Gift activity if toCustomer exists
      if (gift instanceof FaceBookGift) {
        final String facebookId = ((FaceBookGift) (gift)).getToFacebookId();
        toCust = getCustomerBySocialLoginId(facebookId);
        gift.setToCustomer(toCust);
        sendActivity = ActivityFactory.createFacebookSendGift(gift);
        if (toCust != null) {
          recvActivity = ActivityFactory.createFacebookRecvGift(gift);
          recvActivity.setGiftId(gift.getId());
        }
      } else if (gift instanceof EmailGift) {
        toCust = getCustomerByEmail(((EmailGift) gift).getToEmail());
        sendActivity = ActivityFactory.createEmailSendGift(gift);
        gift.setToCustomer(toCust);
        if (toCust != null) {
          recvActivity = ActivityFactory.createEmailRecvGift(gift);
          recvActivity.setGiftId(gift.getId());
        }
      }

      if (jobId != null) {
        // relating the messaging job to this send
        recvActivity.getProperties().createOrReplace(KeyValue.jobId, jobId);
        sendActivity.getProperties().createOrReplace(KeyValue.jobId, jobId);
      }

      if (LOG.isDebugEnabled() && toCust != null) {
        LOG.debug("Sending immediate activity to Talool customer " + toCust.getEmail());
      }

      dac.setGift(gift);
      dac.setAcquireStatus(AcquireStatus.PENDING_ACCEPT_CUSTOMER_SHARE);
      daoDispatcher.save(dac);

      getCurrentSession().flush();

      if (sendActivity != null) {
        ServiceFactory.get().getActivityService().save(sendActivity);
      }
      if (recvActivity != null) {
        ServiceFactory.get().getActivityService().save(recvActivity);
      }

    } catch (Exception ex) {
      throw new ServiceException("Problem in createGift: " + ex.getLocalizedMessage(), ex);
    }

    SubAction subaction = (gift instanceof FaceBookGift) ? SubAction.facebook : SubAction.email;
    TaloolStatsDClient.get().count(Action.gift, subaction, gift.getDealAcquire().getDeal().getId(), requestHeaders.get());

  }

  @Transactional(propagation = Propagation.NESTED)
  public void createGift(final UUID owningCustomerId, final UUID dealAcquireId, final Gift gift) throws ServiceException {
    createGift(owningCustomerId, dealAcquireId, gift, null);
  }

  @Override
  public Gift getGift(final UUID giftRequestId) throws ServiceException {
    try {
      return daoDispatcher.find(GiftImpl.class, giftRequestId);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getGiftRequest %s", giftRequestId), ex);
    }
  }



  /**
   * If gift has not already been assigned to an existing customer and the gift has not been
   * accepted already, we are free to assign the gift to the receipientCustomerId
   */
  @Override
  @Transactional(propagation = Propagation.NESTED, rollbackFor = ServiceException.class)
  public DealAcquire acceptGift(final UUID giftId, final UUID receipientCustomerId) throws ServiceException {
    final Customer receivingCustomer = getCustomerById(receipientCustomerId);
    final Gift gift = daoDispatcher.find(GiftImpl.class, giftId);

    if (gift == null) {
      throw new ServiceException(String.format("giftRequestId %s does not exist", giftId));
    }

    if (gift.getGiftStatus() == GiftStatus.ACCEPTED) {
      throw new ServiceException(ErrorCode.GIFT_ALREADY_ACCEPTED, "accepted by " + gift.getToCustomer().getEmail());
    }

    // if no toCustomer then this gift hasn't been assigned to a customer yet (they don't exist)
    final Optional<Customer> toCustomer = Optional.fromNullable(gift.getToCustomer());
    if (toCustomer.isPresent()) {
      if (!toCustomer.get().getId().equals(receipientCustomerId)) {
        throw new ServiceException(ErrorCode.NOT_GIFT_RECIPIENT, String.format("The gift was sent to %s and cannot be claimed by %s", toCustomer
            .get().getEmail(), receivingCustomer.getEmail()));
      }
    }

    final DealAcquire dac = gift.getDealAcquire();
    dac.setAcquireStatus(AcquireStatus.ACCEPTED_CUSTOMER_SHARE);
    // dac.setSharedByCustomer(dac.getCustomer());
    dac.setCustomer(receivingCustomer);
    // update deal acquire
    daoDispatcher.save(dac);

    // update gift request
    gift.setToCustomer(receivingCustomer);
    gift.setGiftStatus(GiftStatus.ACCEPTED);
    daoDispatcher.save(gift);

    try {
      Activity activity = ActivityFactory.createFriendAccept(gift);
      ServiceFactory.get().getActivityService().save(activity);
    } catch (Exception e) {
      LOG.error("Problem creating createFriendAccept: " + e.getLocalizedMessage(), e);
    }

    try {
      setClosedState(gift, true);
    } catch (Exception e) {
      LOG.error(
          String.format("Problem finding/persisting closedState on activity. receipCustomerId %s giftId %s", receipientCustomerId.toString(), giftId),
          e);
    }

    TaloolStatsDClient.get().count(Action.gift, SubAction.accept, gift.getDealAcquire().getDeal().getId(), requestHeaders.get());

    return dac;

  }

  @Transactional(propagation = Propagation.NESTED, rollbackFor = ServiceException.class)
  private void setClosedState(final Gift gift, final boolean isClosed) throws TException {
    final Search search = new Search(ActivityImpl.class);
    search.addFilterEqual("giftId", gift.getId());
    search.addFilterEqual("customerId", gift.getToCustomer().getId());

    final Activity act = (Activity) daoDispatcher.searchUnique(search);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Closing state on gift activity - activityId: " + act.getId());
    }

    ActivityFactory.setActionTaken(act, isClosed);

    daoDispatcher.save(act);
    TaloolStatsDClient.get().count(Action.activity_action_taken, null, null, requestHeaders.get());
  }

  private GiftOwnership validateGiftOwnership(final UUID giftRequestId, final UUID customerId) throws ServiceException {
    final Gift giftRequest = daoDispatcher.find(GiftImpl.class, giftRequestId);
    if (giftRequest == null) {
      throw new ServiceException(String.format("giftRequestId %s does not exist", giftRequestId));
    }

    final GiftOwnership giftOwnership = new GiftOwnership();
    giftOwnership.gift = giftRequest;

    final Customer receivingCustomer = getCustomerById(customerId);

    if (receivingCustomer == null) {
      throw new ServiceException(String.format("customerId %s does not exist", customerId));
    }

    giftOwnership.receivingCustomer = receivingCustomer;

    if (giftRequest instanceof EmailGiftImpl) {
      if (!((EmailGiftImpl) giftRequest).getToEmail().equals(receivingCustomer.getEmail())) {
        throw new ServiceException(String.format("receivingCustomerId %s with email %s not the gift receiver for giftRequestId %s",
            receivingCustomer.getId(), receivingCustomer.getEmail(), receivingCustomer));
      }
    } else if (giftRequest instanceof FacebookGiftImpl) {
      final SocialNetwork facebook = ServiceFactory.get().getTaloolService().getSocialNetwork(SocialNetwork.NetworkName.Facebook);

      if (!((FacebookGiftImpl) giftRequest).getToFacebookId().equals(receivingCustomer.getSocialAccounts().get(facebook).getLoginId())) {
        throw new ServiceException(String.format("receivingCustomerId %s with facebookId %s is not the gift receiver for giftRequestId %s",
            receivingCustomer.getId(), receivingCustomer.getSocialAccounts().get(facebook).getLoginId(), receivingCustomer));
      }

    }

    return giftOwnership;
  }

  @Override
  @Transactional(propagation = Propagation.NESTED, rollbackFor = ServiceException.class)
  public void rejectGift(final UUID giftRequestId, final UUID receipientCustomerId) throws ServiceException {
    final GiftOwnership giftOwnership = validateGiftOwnership(giftRequestId, receipientCustomerId);

    final DealAcquire dac = giftOwnership.gift.getDealAcquire();

    dac.setAcquireStatus(AcquireStatus.REJECTED_CUSTOMER_SHARE);

    // update deal acquire
    daoDispatcher.save(dac);

    // update gift request
    giftOwnership.gift.setGiftStatus(GiftStatus.REJECTED);
    daoDispatcher.save(giftOwnership.gift);

    Activity activity;
    try {
      activity = ActivityFactory.createReject(giftOwnership.gift, receipientCustomerId);
      ServiceFactory.get().getActivityService().save(activity);
    } catch (Exception e) {
      LOG.error("Problem creating rejectGift: " + e.getLocalizedMessage(), e);
    }

    try {
      // create the bi-directional activity
      activity = ActivityFactory.createFriendRejectGift(giftOwnership.gift);
      ServiceFactory.get().getActivityService().save(activity);
    } catch (Exception e) {
      LOG.error("Problem creating createFriendRejectGift: " + e.getLocalizedMessage(), e);
    }

    try {
      setClosedState(giftOwnership.gift, true);
    } catch (Exception e) {
      LOG.error("Problem finding/persisting closedState on activity: " + e.getLocalizedMessage());
    }

    TaloolStatsDClient.get().count(Action.gift, SubAction.reject, giftOwnership.gift.getDealAcquire().getDeal().getId(), requestHeaders.get());
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void remove(final CustomerSocialAccount cas) throws ServiceException {
    try {
      daoDispatcher.remove(cas);
    } catch (Exception ex) {
      throw new ServiceException("Problem in removing CustomerSocialAccount", ex);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void removeSocialAccount(final UUID customerId, final SocialNetwork socialNetwork) throws ServiceException {
    try {
      final Query query = getCurrentSession().getNamedQuery("deleteCustomerSocialAccount");
      query.setParameter("customerId", customerId);
      query.setParameter("socialNetworkId", socialNetwork.getId());
      query.executeUpdate();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem in removing SocialAccount %s for customerId %s", socialNetwork.getName(),
          customerId.toString()), ex);
    }

  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final CustomerSocialAccount socialAccount) throws ServiceException {
    try {
      daoDispatcher.save(socialAccount);
    } catch (Exception ex) {
      throw new ServiceException("Problem saving CustomerSocialAccount", ex);
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Gift> getGifts(final UUID customerId, final GiftStatus[] giftStatus) throws ServiceException {
    List<Gift> gifts = null;

    try {
      final Query query = (Query) getCurrentSession().getNamedQuery("getGifts");
      query.setParameter("customerId", customerId, PostgresUUIDType.INSTANCE);
      query.setParameterList("giftStatus", giftStatus);
      gifts = query.list();
    } catch (Exception e) {
      throw new ServiceException("Problem getGiftedDealAcquires", e);
    }

    TaloolStatsDClient.get().count(Action.get_gifts, null, null, requestHeaders.get());

    return gifts;

  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public UUID giftToFacebook(final UUID owningCustomerId, final UUID dealAcquireId, final String facebookId, final String receipientName)
      throws ServiceException {
    final FaceBookGift gift = new FacebookGiftImpl();
    gift.setToFacebookId(facebookId);
    gift.setReceipientName(receipientName);
    createGift(owningCustomerId, dealAcquireId, gift);

    // see if to customer
    return gift.getId();
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public UUID giftToEmail(final UUID owningCustomerId, final UUID dealAcquireId, final String email, final String receipientName)
      throws ServiceException {
    final EmailGift gift = new EmailGiftImpl();
    gift.setToEmail(email.toLowerCase());
    gift.setReceipientName(receipientName);
    createGift(owningCustomerId, dealAcquireId, gift);

    if (!email.contains(IGNORE_TEST_EMAIL_DOMAIN)) {
      if (LOG.isDebugEnabled()) {
        LOG.info("Sending gift email to " + email);
      }

      ServiceFactory.get().getEmailService().sendGiftEmail(new EmailRequestParams<EmailGift>(gift));

    }

    return gift.getId();

  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public UUID giftToEmail(final Long jobId, final UUID owningCustomerId, final UUID dealAcquireId, final EmailGift gift, final String emailCategory)
      throws ServiceException {
    createGift(owningCustomerId, dealAcquireId, gift, jobId);

    if (!gift.getToEmail().contains(IGNORE_TEST_EMAIL_DOMAIN)) {
      if (LOG.isDebugEnabled()) {
        LOG.info("Sending gift email to " + gift.getToEmail());
      }

      ServiceFactory.get().getEmailService().sendGiftEmail(new EmailRequestParams<EmailGift>(gift), emailCategory);

    }

    return gift.getId();
  }

  public UniqueCodeStrategy getRedemptionCodeStrategy() {
    return redemptionCodeStrategy;
  }

  public void setRedemptionCodeStrategy(final UniqueCodeStrategy redemptionCodeStrategy) {
    this.redemptionCodeStrategy = redemptionCodeStrategy;
  }

  @Override
  public Gift getGiftOnDealAcquire(final UUID dealAcquireId) throws ServiceException {
    final Search search = new Search(DealAcquireImpl.class);

    try {
      search.addFilterEqual("id", dealAcquireId);
      search.addField("gift");
      return (Gift) daoDispatcher.searchUnique(search);
    } catch (Exception ex) {
      String msg = "Problem getGiftOnDealAcquire dealAcquireId: " + dealAcquireId;
      LOG.error(msg, ex);
      throw new ServiceException(msg, ex);
    }

  }

  @Override
  public Customer getCustomerBySocialLoginId(final String socialLoginId) throws ServiceException {
    final Search search = new Search(CustomerSocialAccountImpl.class);
    try {
      search.addFilterEqual("loginId", socialLoginId);
      search.addField("customer");
      return (Customer) daoDispatcher.searchUnique(search);
    } catch (Exception ex) {
      String msg = "Problem getCustomerBySocialLoginId socialLoginId: " + socialLoginId;
      LOG.error(msg, ex);
      throw new ServiceException(msg, ex);
    }
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void activateCode(final UUID customerId, final UUID dealOfferId, final String code) throws ServiceException {
    ActivationCode activationCode = null;
    Search search = null;

    try {
      final String uCode = code.toUpperCase();

      // we have no choice but to try the users original input
      search = new Search(ActivationCodeImpl.class);
      search.addFilterEqual("dealOfferId", dealOfferId);
      search.addFilterEqual("code", uCode);
      activationCode = (ActivationCodeImpl) daoDispatcher.searchUnique(search);

      if (activationCode == null) {
        // remove zeros and ohhs because too similar in printed book
        final String cleanCode = uCode.replaceAll("(O|0)", "(0|O)");
        if (cleanCode.equals(uCode)) {
          // there is nothing to replace,code doesnt exist
          throw new ServiceException(ErrorCode.ACTIVIATION_CODE_NOT_FOUND, code);
        }

        final SQLQuery sqlQuery =
            getCurrentSession().createSQLQuery("select activation_code_id from activation_code where code ~ '" + cleanCode + "'");

        sqlQuery.addScalar("activation_code_id", PostgresUUIDType.INSTANCE);

        @SuppressWarnings("unchecked")
        final List<UUID> codes = sqlQuery.list();

        if (CollectionUtils.isEmpty(codes)) {
          throw new ServiceException(ErrorCode.ACTIVIATION_CODE_NOT_FOUND, code);
        }

        if (codes.size() > 1) {
          // there are multiple ones and we don't know what to pick!
          throw new ServiceException(ErrorCode.ACTIVIATION_CODE_NOT_FOUND, code);
        } else {
          // guaranteed to be 1 code here
          search = new Search(ActivationCodeImpl.class);
          search.addFilterEqual("id", codes.get(0));
          activationCode = (ActivationCodeImpl) daoDispatcher.searchUnique(search);
        }
      }

      if (activationCode == null) {
        throw new ServiceException(ErrorCode.ACTIVIATION_CODE_NOT_FOUND, code);
      }

      if (activationCode.getCustomerId() != null) {
        throw new ServiceException(ErrorCode.ACTIVIATION_CODE_ALREADY_ACTIVATED, code);
      }

      activationCode.setCustomerId(customerId);
      activationCode.setActivatedDate(new Date(System.currentTimeMillis()));

      daoDispatcher.save(activationCode);

      final DealOffer dealOffer = ServiceFactory.get().getTaloolService().getDealOffer(dealOfferId);
      final Activity act = ActivityFactory.createActivatedByCode(dealOffer, customerId, code);

      createDealOfferPurchase(customerId, dealOfferId);

      ServiceFactory.get().getActivityService().save(act);

      TaloolStatsDClient.get().count(Action.purchase, SubAction.activate_code, dealOfferId, requestHeaders.get());
    } catch (ServiceException se) {
      throw se;
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem createActivationCodes dealOfferId %s", dealOfferId), ex);
    }
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void createPasswordReset(final Customer customer) throws ServiceException {
    try {
      customer.setResetPasswordCode(RandomStringUtils.randomAlphanumeric(16));
      customer.setResetPasswordExpires(DateUtils.addHours(Calendar.getInstance().getTime(), 2));

      daoDispatcher.save(customer);

      ServiceFactory.get().getEmailService().sendPasswordRecoveryEmail(new EmailRequestParams<Customer>(customer));

    } catch (Exception e) {
      throw new ServiceException("Problem creating password reset for email " + customer.getEmail(), e);
    }

    TaloolStatsDClient.get().count(Action.password, SubAction.create_reset, null, requestHeaders.get());
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  /**
   * TODO Test rollback on failures, VOID charge
   */
  public TransactionResult purchaseByCard(final UUID customerId, final UUID dealOfferId, final PaymentDetail paymentDetail,
      final Map<String, String> paymentProperties) throws ServiceException, NotFoundException {
    DealOffer dealOffer = null;
    Customer customer = null;
    TransactionResult transactionResult = null;
    Merchant fundraiser = null;
    Merchant publisher = null;
    DealOfferPurchase dop = null;

    try {
      // TODO Optimize the heavy call which pulls dealOffers - maybe ehcache
      // DealOffers
      dealOffer = ServiceFactory.get().getTaloolService().getDealOffer(dealOfferId);
      customer = ServiceFactory.get().getCustomerService().getCustomerById(customerId);

      if (paymentProperties.containsKey(KeyValue.merchantCode)) {
        String merchantCode = paymentProperties.get(KeyValue.merchantCode);
        fundraiser = ServiceFactory.get().getTaloolService().getFundraiserByTrackingCode(merchantCode);
        TaloolStatsDClient.get().count(Action.fundraiser_purchase, SubAction.credit_card, fundraiser.getId(), requestHeaders.get());
      }

    } catch (ServiceException se) {
      throw se;
    }

    if (dealOffer == null) {
      throw new NotFoundException("deal offer", dealOfferId == null ? null : dealOfferId.toString());
    }

    if (customer == null) {
      throw new NotFoundException("customer", customerId == null ? null : customerId.toString());
    }

    try {
      publisher = dealOffer.getMerchant();
      transactionResult = BraintreeUtil.get().processCard(customer, dealOffer, paymentDetail, publisher, fundraiser);
    } catch (ProcessorException e) {
      throw new ServiceException(ErrorCode.GENERAL_PROCESSOR_ERROR, e);
    }

    if (transactionResult.isSuccess()) {
      try {
        dop = createDealOfferPurchase(customer, dealOffer, transactionResult, paymentProperties);
        getCurrentSession().flush();
        TaloolStatsDClient.get().count(Action.purchase, SubAction.credit_card, dealOfferId, requestHeaders.get());
      } catch (ServiceException e) {
        try {
          rollbackPaymentTransaction(customerId, dealOfferId, transactionResult, e);
        } catch (ProcessorException pe) {
          LOG.error("Transaction not rolled back with processor! " + pe.getMessage(), pe);
        }

        throw e;
      }

      // send purchase events to anyone listing
      purchaseEventBus.post(new PurchaseEvent(dop, paymentProperties, fundraiser));
    } else {
      LOG.error(String.format("Transaction failed for customerId %s errorCode %s errorText %s", customerId, transactionResult.getErrorCode(),
          transactionResult.getErrorText()));
    }
    return transactionResult;

  }

  private void rollbackPaymentTransaction(final UUID customerId, final UUID dealOfferId, final TransactionResult transactionResult,
      final ServiceException causedException) throws ProcessorException {
    LOG.error(String.format("Creating dealOffer failed customerId '%s' dealOfferId '%s' rolling back transactionId '%s'", customerId, dealOfferId,
        transactionResult.getTransactionId()), causedException);

    final RefundVoidResult result = BraintreeUtil.get().refundOrVoid(transactionResult.getTransactionId());

    LOG.error(String.format("Payment transaction roll back of transactionId '%s' success %s ", transactionResult.getTransactionId(), result
        .getTransactionResult().isSuccess()));

  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public TransactionResult purchaseByCode(final UUID customerId, final UUID dealOfferId, final String paymentCode,
      final Map<String, String> paymentProperties) throws ServiceException, NotFoundException {
    DealOffer dealOffer = null;
    Customer customer = null;
    TransactionResult transactionResult = null;
    DealOfferPurchase dop = null;
    Merchant fundraiser = null;
    Merchant publisher = null;

    try {
      // TODO Optimize the heavy call which pulls dealOffers - maybe ehcache
      // DealOffers
      dealOffer = ServiceFactory.get().getTaloolService().getDealOffer(dealOfferId);
      customer = ServiceFactory.get().getCustomerService().getCustomerById(customerId);

      if (paymentProperties.containsKey(KeyValue.merchantCode)) {
        String merchantCode = paymentProperties.get(KeyValue.merchantCode);
        fundraiser = ServiceFactory.get().getTaloolService().getFundraiserByTrackingCode(merchantCode);
        TaloolStatsDClient.get().count(Action.fundraiser_purchase, SubAction.credit_card, fundraiser.getId(), requestHeaders.get());
      }
    } catch (ServiceException se) {
      throw se;
    }

    if (dealOffer == null) {
      throw new NotFoundException("deal offer", dealOfferId == null ? null : dealOfferId.toString());
    }

    if (customer == null) {
      throw new NotFoundException("customer", customerId == null ? null : customerId.toString());
    }

    try {
      publisher = dealOffer.getMerchant();
      transactionResult = BraintreeUtil.get().processPaymentCode(customer, dealOffer, paymentCode, publisher, fundraiser);
    } catch (ProcessorException e) {
      throw new ServiceException(ErrorCode.GENERAL_PROCESSOR_ERROR, e);
    }

    if (transactionResult.isSuccess()) {
      try {
        dop = createDealOfferPurchase(customer, dealOffer, transactionResult, paymentProperties);
        getCurrentSession().flush();
        if (LOG.isDebugEnabled()) {
          LOG.debug("processing braintree for " + customer.getEmail() + " " + transactionResult.getTransactionId());
        }
        TaloolStatsDClient.get().count(Action.purchase, SubAction.credit_card_code, dealOfferId, requestHeaders.get());
      } catch (ServiceException e) {
        try {
          if (LOG.isDebugEnabled()) {
            LOG.debug("rolling back braintree transaction " + customer.getEmail());
          }

          rollbackPaymentTransaction(customerId, dealOfferId, transactionResult, e);
        } catch (ProcessorException pe) {
          LOG.error("Transaction not rolled back with processor! " + pe.getMessage(), pe);
        }

        throw e;
      }

      // send purchase events to anyone listing
      purchaseEventBus.post(new PurchaseEvent(dop, paymentProperties, fundraiser));

    } else {
      LOG.error(String.format("Transaction failed for customerId %s errorCode %s errorText %s", customerId, transactionResult.getErrorCode(),
          transactionResult.getErrorText()));
    }
    return transactionResult;

  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<CustomerSummary> getCustomerSummary(final SearchOptions searchOpts, final boolean calculateRowSize) throws ServiceException {
    PaginatedResult<CustomerSummary> paginatedResult = null;
    List<CustomerSummary> summaries = null;
    Long totalResults = null;

    try {
      String newSql = QueryHelper.buildQuery(QueryType.CustomerSummary, null, searchOpts, true);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(CustomerSummary.class));
      query.addScalar("customerId", PostgresUUIDType.INSTANCE);
      query.addScalar("email", StandardBasicTypes.STRING);
      query.addScalar("firstName", StandardBasicTypes.STRING);
      query.addScalar("lastName", StandardBasicTypes.STRING);
      query.addScalar("redemptions", StandardBasicTypes.INTEGER);
      query.addScalar("registrationDate", StandardBasicTypes.DATE);
      query.addScalar("commaSeperatedDealOfferTitles", StandardBasicTypes.STRING);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<CustomerSummary>) query.list();

      if (calculateRowSize && summaries != null) {
        totalResults = (Long) getCustomerSummaryCount();
      }

      paginatedResult = new PaginatedResult<CustomerSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getCustomerSummary: " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @Override
  public long getCustomerSummaryCount() throws ServiceException {
    Long total = null;

    try {
      final String newSql = QueryHelper.buildQuery(QueryType.CustomerSummaryCnt, null, null, true);
      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getCustomerSummary: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<CustomerSummary> getPublisherCustomerSummary(final UUID publisherMerchantId, final SearchOptions searchOpts,
      final boolean calculateRowSize) throws ServiceException {
    PaginatedResult<CustomerSummary> paginatedResult = null;
    List<CustomerSummary> summaries = null;
    Long totalResults = null;

    try {
      String newSql = QueryHelper.buildQuery(QueryType.PublisherCustomerSummary, null, searchOpts, true);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(CustomerSummary.class));
      query.addScalar("customerId", PostgresUUIDType.INSTANCE);
      query.addScalar("email", StandardBasicTypes.STRING);
      query.addScalar("firstName", StandardBasicTypes.STRING);
      query.addScalar("lastName", StandardBasicTypes.STRING);
      query.addScalar("redemptions", StandardBasicTypes.INTEGER);
      query.addScalar("registrationDate", StandardBasicTypes.DATE);
      query.addScalar("commaSeperatedDealOfferTitles", StandardBasicTypes.STRING);

      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<CustomerSummary>) query.list();

      if (calculateRowSize && summaries != null) {
        totalResults = (Long) getPublisherCustomerSummaryCount(publisherMerchantId);
      }

      paginatedResult = new PaginatedResult<CustomerSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getCustomerSummary: " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @Override
  public long getPublisherCustomerSummaryCount(final UUID publisherMerchantId) throws ServiceException {
    Long total = null;

    try {
      final String newSql = QueryHelper.buildQuery(QueryType.PublisherCustomerSummaryCnt, null, null, true);
      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getCustomerSummary: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @Override
  public long getPublisherCustomerSummaryEmailCount(final UUID publisherMerchantId, final String email) throws ServiceException {
    Long total = null;

    try {
      final String newSql = QueryHelper.buildQuery(QueryType.PublisherCustomerEmailSummaryCnt, null, null, true);
      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
      query.setParameter("email", email.replaceAll("[*]", "%").toLowerCase());
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getCustomerSummary: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<CustomerSummary> getPublisherCustomerSummaryByEmail(final UUID publisherMerchantId, final SearchOptions searchOpts,
      final String email, final boolean calculateRowSize) throws ServiceException {
    PaginatedResult<CustomerSummary> paginatedResult = null;
    List<CustomerSummary> summaries = null;
    Long totalResults = null;
    String cleanEmail = null;

    try {
      String newSql = QueryHelper.buildQuery(QueryType.PublisherCustomerEmailSummary, null, searchOpts, true);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(CustomerSummary.class));
      query.addScalar("customerId", PostgresUUIDType.INSTANCE);
      query.addScalar("email", StandardBasicTypes.STRING);
      query.addScalar("firstName", StandardBasicTypes.STRING);
      query.addScalar("lastName", StandardBasicTypes.STRING);
      query.addScalar("redemptions", StandardBasicTypes.INTEGER);
      query.addScalar("registrationDate", StandardBasicTypes.DATE);
      query.addScalar("commaSeperatedDealOfferTitles", StandardBasicTypes.STRING);

      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
      cleanEmail = email.replaceAll("[*]", "%");
      query.setParameter("email", cleanEmail.toLowerCase());

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<CustomerSummary>) query.list();

      if (calculateRowSize && summaries != null) {
        totalResults = getPublisherCustomerSummaryEmailCount(publisherMerchantId, cleanEmail);
      }

      paginatedResult = new PaginatedResult<CustomerSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getCustomerSummary: " + ex.getMessage(), ex);
    }

    return paginatedResult;

  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<CustomerSummary> getCustomerSummary(final SearchOptions searchOpts, final String email, final boolean calculateRowSize)
      throws ServiceException {
    PaginatedResult<CustomerSummary> paginatedResult = null;
    List<CustomerSummary> summaries = null;
    Long totalResults = null;
    String cleanEmail = null;

    try {
      String newSql = QueryHelper.buildQuery(QueryType.AllCustomerEmailSummary, null, searchOpts, true);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(CustomerSummary.class));
      query.addScalar("customerId", PostgresUUIDType.INSTANCE);
      query.addScalar("email", StandardBasicTypes.STRING);
      query.addScalar("firstName", StandardBasicTypes.STRING);
      query.addScalar("lastName", StandardBasicTypes.STRING);
      query.addScalar("redemptions", StandardBasicTypes.INTEGER);
      query.addScalar("registrationDate", StandardBasicTypes.DATE);
      query.addScalar("commaSeperatedDealOfferTitles", StandardBasicTypes.STRING);

      cleanEmail = email.replaceAll("[*]", "%");
      query.setParameter("email", cleanEmail.toLowerCase());

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<CustomerSummary>) query.list();

      if (calculateRowSize && summaries != null) {
        totalResults = getCustomerSummaryCount(email);
      }

      paginatedResult = new PaginatedResult<CustomerSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getCustomerSummary with email %s: %s", email, ex.getMessage()), ex);
    }

    return paginatedResult;
  }

  @Override
  public long getCustomerSummaryCount(final String email) throws ServiceException {
    Long total = null;

    try {
      final String newSql = QueryHelper.buildQuery(QueryType.AllCustomerEmailSummaryCnt, null, null, true);
      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("email", email.replaceAll("[*]", "%").toLowerCase());
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getCustomerSummary %s : %s", email, ex.getMessage(), ex));
    }

    return total == null ? 0 : total;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void giveGiftBackToGiver(final UUID giftId, final String reason) throws ServiceException {
    Activity activity = null;
    final Gift gift = getGift(giftId);
    gift.setGiftStatus(GiftStatus.INVALIDATED);
    gift.getDealAcquire().setAcquireStatus(AcquireStatus.REJECTED_CUSTOMER_SHARE);
    daoDispatcher.save(gift);

    try {
      activity = ActivityFactory.createGiftReturnNotExistentEmail(gift);
      ServiceFactory.get().getActivityService().save(activity);
    } catch (TException e) {
      LOG.error("Problem creating activity :" + e.getLocalizedMessage(), e);
    }

  }

  @Override
  public boolean isActivationCodeValid(final String code, final UUID dealOfferid) throws ServiceException {
    ActivationCode activationCode = null;
    Search search = null;

    try {
      final String uCode = code.toUpperCase();
      search = new Search(ActivationCodeImpl.class);
      search.addFilterEqual("dealOfferId", dealOfferid);
      search.addFilterEqual("code", uCode);
      activationCode = (ActivationCodeImpl) daoDispatcher.searchUnique(search);

      TaloolStatsDClient.get().count(Action.validate_code, SubAction.activation_code, dealOfferid, requestHeaders.get());
    } catch (Exception ex) {
      throw new ServiceException(ErrorCode.MERCHANT_CODE_IS_NOT_VALID);
    }

    return activationCode == null ? false : true;

  }

  @Override
  public void setRequestHeaders(final Map<String, String> headers) {
    requestHeaders.set(headers);
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public TransactionResult purchaseByNonce(UUID customerId, UUID dealOfferId, String nonce, Map<String, String> paymentProperties)
      throws ServiceException, NotFoundException {

    DealOffer dealOffer = null;
    Customer customer = null;
    TransactionResult transactionResult = null;
    DealOfferPurchase dop = null;
    Merchant fundraiser = null;
    Merchant publisher = null;
    boolean freeBook = false;
    final String deviceId = requestHeaders.get().get(KeyValue.deviceId);


    try {
      // TODO Optimize the heavy call which pulls dealOffers - maybe ehcache
      // DealOffers
      dealOffer = ServiceFactory.get().getTaloolService().getDealOffer(dealOfferId);
      freeBook = dealOffer.getType() == DealType.FREE_BOOK || dealOffer.getPrice() == 0;
      if (LOG.isDebugEnabled()) {
        LOG.debug(dealOffer.getTitle() + " is a free book.");
      }

      // checking if dealOffer has limits - we may be limiting the number of purchases a customer
      if (dealOffer.getProperties().exists(KeyValue.limitOnePurchasePerCustomer)) {
        final PropertyCriteria pc = new PropertyCriteria();
        if (StringUtils.isNotEmpty(deviceId)) {
          pc.setFilters(Filter.equal(KeyValue.deviceId, deviceId));
        }
        final List<UUID> purchaseIds = ServiceFactory.get().getTaloolService().getDealOfferPurchaseIds(customerId, dealOfferId, pc);
        if (CollectionUtils.isNotEmpty(purchaseIds)) {
          if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("limitOnePurchasePerCustomer reached: customerId: %s dealOfferId: %s", customerId, dealOfferId));
          }
          throw new ServiceException(ErrorCode.LIMIT_ONE_PURCHASE_PER_CUSTOMER);
        }

      }
      customer = ServiceFactory.get().getCustomerService().getCustomerById(customerId);

      if (customer == null) {
        throw new NotFoundException("customer", customerId == null ? null : customerId.toString());
      }

      if (paymentProperties.containsKey(KeyValue.merchantCode)) {
        String merchantCode = paymentProperties.get(KeyValue.merchantCode);
        fundraiser = ServiceFactory.get().getTaloolService().getFundraiserByTrackingCode(merchantCode);
        TaloolStatsDClient.get().count(Action.fundraiser_purchase, SubAction.credit_card, fundraiser.getId(), requestHeaders.get());
      }
    } catch (ServiceException se) {
      throw se;
    }

    // Braintree transactions only if a Paid Book and price is not zero
    if (!freeBook) {
      try {
        publisher = dealOffer.getMerchant();
        transactionResult = BraintreeUtil.get().processPaymentNonce(customer, dealOffer, nonce, publisher, fundraiser);
      } catch (ProcessorException e) {
        throw new ServiceException(ErrorCode.GENERAL_PROCESSOR_ERROR, e);
      }
    } else {
      // create "free" transaction
      transactionResult = TransactionResult.successfulTransaction(null, null);
    }

    if (transactionResult.isSuccess()) {
      try {
        // store deviceId header
        if (StringUtils.isNotEmpty(deviceId)) {
          paymentProperties.put(KeyValue.deviceId, deviceId);
        }
        dop = createDealOfferPurchase(customer, dealOffer, transactionResult, paymentProperties);
        getCurrentSession().flush();
        if (LOG.isDebugEnabled()) {
          LOG.debug("processing braintree for " + customer.getEmail() + " " + transactionResult.getTransactionId());
        }
        TaloolStatsDClient.get().count(Action.purchase, SubAction.credit_card_nonce, dealOfferId, requestHeaders.get());
      } catch (ServiceException e) {
        try {
          if (transactionResult != null) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("rolling back braintree transaction " + customer.getEmail());
            }
            rollbackPaymentTransaction(customerId, dealOfferId, transactionResult, e);
          }
        } catch (ProcessorException pe) {
          LOG.error("Transaction not rolled back with processor! " + pe.getMessage(), pe);
        }
        throw e;
      }

      // send purchase events to anyone listing
      purchaseEventBus.post(new PurchaseEvent(dop, paymentProperties, fundraiser));

    } else {
      LOG.error(String.format("Transaction failed for customerId %s errorCode %s errorText %s", customerId, transactionResult.getErrorCode(),
          transactionResult.getErrorText()));
    }
    return transactionResult;
  }

  @Override
  public String generateBraintreeClientToken(UUID customerId) throws ServiceException, NotFoundException {
    return BraintreeUtil.get().generateClientToken(customerId);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Customer> getCustomers(CustomerCriteria criteria) throws ServiceException {
    try {
      final Query query = criteria.getQuery(sessionFactory.getCurrentSession());
      return query.list();
    } catch (Exception ex) {
      throw new ServiceException("Problem getCustomers with criteria", ex);
    }
  }

  @Override
  public long getCustomerCount(CustomerCriteria criteria) throws ServiceException {
    try {
      final Query query = criteria.getCountQuery(sessionFactory.getCurrentSession());
      return (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getCustomers with criteria", ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DevicePresence> getDevicePresenceForCustomer(UUID customerId) throws ServiceException {
    List<DevicePresence> devices;
    try {
      final Search search = new Search(DevicePresenceImpl.class).addFilterEqual("customerId", customerId);
      devices = (List<DevicePresence>) daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException("Problem getting devices for customerId " + customerId, ex);
    }
    return devices;
  }

}
