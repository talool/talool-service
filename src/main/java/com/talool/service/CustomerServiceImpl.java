package com.talool.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.thrift.TException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.StatelessSession;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernatespatial.GeometryUserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
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
import com.talool.domain.CustomerImpl;
import com.talool.domain.DealAcquireImpl;
import com.talool.domain.DealOfferPurchaseImpl;
import com.talool.domain.FavoriteMerchantImpl;
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
import com.talool.persistence.QueryHelper;
import com.talool.persistence.QueryHelper.QueryType;
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
public class CustomerServiceImpl extends AbstractHibernateService implements CustomerService
{
	private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);

	private static final String IGNORE_TEST_EMAIL_DOMAIN = "test.talool.com";

	private UniqueCodeStrategy redemptionCodeStrategy;

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void createAccount(final Customer customer, final String password) throws ServiceException
	{

		if (!EmailValidator.getInstance().isValid(customer.getEmail()))
		{
			throw new ServiceException(ErrorCode.VALID_EMAIL_REQUIRED);
		}

		if (StringUtils.isEmpty(password))
		{
			throw new ServiceException(ErrorCode.PASS_REQUIRED);
		}

		createAccount(AccountType.CUS, customer, password);

		ServiceFactory.get().getEmailService().sendCustomerRegistrationEmail(customer);

		LOG.info("Sent registration email to " + customer.getEmail());

		final List<Gift> gifts = getGifts(customer.getId(), GiftStatus.values());
		final List<Activity> activities = new ArrayList<Activity>();

		try
		{
			// add welcome message!
			activities.add(ActivityFactory.createWelcome(customer.getId(), gifts.size()));
		}
		catch (Exception ex)
		{
			LOG.error("Problem creating welcome activity: " + ex.getLocalizedMessage(), ex);
		}

		if (CollectionUtils.isNotEmpty(gifts))
		{
			for (final Gift gift : gifts)
			{
				gift.setToCustomer(customer);
				daoDispatcher.save(gift);

				try
				{
					final Activity act = ActivityFactory.createRecvGift(gift);
					act.setGiftId(gift.getId());
					activities.add(act);
				}
				catch (Exception e)
				{
					LOG.error("Problem creating activity for new user " + customer.getEmail());
				}
			}
		}

		try
		{
			ServiceFactory.get().getActivityService().save(activities);
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Sending %d gift activities for new customer %s %s", gifts.size(), customer.getEmail(),
						customer.getId()));
			}
		}
		catch (Exception e)
		{
			LOG.error("Problem saving activities for new user " + customer.getEmail());
		}

	}
	private static class GiftOwnership
	{
		Gift gift;
		Customer receivingCustomer;
	}

	private void createAccount(final AccountType accountType, final IdentifiableUUID account,
			final String password) throws ServiceException
	{
		try
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Creating accountType:" + accountType + ": " + account.toString());
			}

			// set encrypted password
			((CustomerImpl) (account)).setPassword(password);
			save((CustomerImpl) account);
			daoDispatcher.flush(CustomerImpl.class);
			daoDispatcher.refresh((CustomerImpl) account);

			// create any neccsary activities

		}
		catch (Exception e)
		{
			final String err = "There was a problem registering  " + account;
			LOG.error(err, e);
			throw new ServiceException(err, e);
		}

	}

	@Override
	public void removeCustomer(UUID id) throws ServiceException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Customer authenticateCustomer(final String email, final String password)
			throws ServiceException
	{
		final Search search = new Search(CustomerImpl.class);

		search.addFilterEqual("email", email);
		try
		{
			search.addFilterEqual("password", EncryptService.MD5(password));
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem authenticating", ex);
		}

		return (Customer) daoDispatcher.searchUnique(search);

	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void save(final Customer customer) throws ServiceException
	{
		try
		{
			daoDispatcher.save(customer);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving customer " + customer;
			LOG.error(err, e);
			throw new ServiceException(err, e);
		}
	}

	@Override
	public Customer getCustomerById(final UUID id) throws ServiceException
	{
		Customer customer;
		try
		{
			customer = daoDispatcher.find(CustomerImpl.class, id);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getCustomerById  " + id, ex);
		}

		return customer;
	}

	@Override
	public Customer getCustomerByEmail(final String email) throws ServiceException, InvalidInputException
	{
		Customer customer = null;

		if (!EmailValidator.getInstance().isValid(email))
		{
			throw new InvalidInputException(ErrorCode.VALID_EMAIL_REQUIRED, email);
		}

		try
		{
			Search search = new Search(CustomerImpl.class);
			search.addFilterEqual("email", email);
			customer = (Customer) daoDispatcher.searchUnique(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getCustomerByEmail  " + email, ex);
		}

		return customer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getCustomers() throws ServiceException
	{
		try
		{
			final Search search = new Search(CustomerImpl.class);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getCustomers"), ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final Relationship relationship) throws ServiceException
	{
		try
		{
			daoDispatcher.save(relationship);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format(
					"Problem saving relationship fromCustomer '%s' toCustomer '%s' "
							+ relationship.getFromCustomer(), relationship.getToCustomer(), ex));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getFriends(final UUID id) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory
					.getCurrentSession()
					.createQuery(
							"from CustomerImpl c, RelationshipImpl r where c.id=r.customer.id and r.friend.id=:customerId");

			query.setParameter("customerId", id);
			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getFriends %s", id), ex);
		}
	}

	/*
	 * Redeems a deal. Uses a StatelessSession . Be careful with stateless
	 * sessions:
	 * 
	 * http://docs.jboss.org/hibernate/core/3.3/api/org/hibernate/StatelessSession.
	 * html
	 * 
	 * The reason a StatelessSession is used is so we dont break the transaction
	 * upon any DB error, particularly a ConstraintViolation because we
	 * optimistically believe redemptionCode is unique. If by chance it isn't, we
	 * regenerate a new one.
	 */
	@Override
	@Transactional(propagation = Propagation.NESTED)
	public String redeemDeal(final UUID dealAcquireId, final UUID customerId, final Location location)
			throws ServiceException
	{
		String redemptionCode = null;
		Query query = null;
		final StatelessSession statelessSession = getSessionFactory().openStatelessSession();

		try
		{
			// we need to first check owning customer and acquireStatus
			query = statelessSession.getNamedQuery("getCustomerIdAcquireStatusOnDealAcquire");
			query.setParameter("dealAcquireId", dealAcquireId);
			final Object[] acquireFields = (Object[]) query.uniqueResult();
			final UUID owningCustomer = (UUID) acquireFields[0];
			final AcquireStatus acquireStatus = (AcquireStatus) acquireFields[1];

			if (!owningCustomer.equals(customerId))
			{
				throw new ServiceException(ErrorCode.CUSTOMER_DOES_NOT_OWN_DEAL,
						"Customer does not own deal");
			}

			if (AcquireStatus.REDEEMED == acquireStatus)
			{
				throw new ServiceException("Cannot redeem already redeemed deal " + dealAcquireId);
			}

			if (!AcquireStatus.ACCEPTED_CUSTOMER_SHARE.equals(acquireStatus) &&
					!AcquireStatus.REJECTED_CUSTOMER_SHARE.equals(acquireStatus) &&

					!AcquireStatus.ACCEPTED_MERCHANT_SHARE.equals(acquireStatus) && !AcquireStatus.PURCHASED.equals(acquireStatus))
			{
				throw new ServiceException(String.format("AcquireStatus %s is not in proper state to redeem for dealAcquireId %s",
						acquireStatus, dealAcquireId) + dealAcquireId);
			}

		}
		catch (ServiceException se)
		{
			throw se;
		}
		catch (Exception ex)
		{
			LOG.error("Problem redeeming deal: " + ex.getLocalizedMessage(), ex);
			throw new ServiceException("Problem in redeemDeal with dealAcquireId " + dealAcquireId, ex);
		}

		try
		{
			query = statelessSession.getNamedQuery("redeemDealAcquire");
			redemptionCode = redemptionCodeStrategy.generateCode();
			query.setParameter("dealAcquireStatus", AcquireStatus.REDEEMED);
			query.setParameter("dealAcquireId", dealAcquireId);

			final GeometryFactory factory = new GeometryFactory(
					new PrecisionModel(PrecisionModel.FLOATING), 4326);

			final Point point = (location == null || location.getLatitude() == null || location.getLongitude() == null) ?
					null : factory.createPoint(new Coordinate(location.getLongitude(), location.getLatitude()));

			query.setParameter("redeemedAtGeometry", point, GeometryUserType.TYPE);
			query.setParameter("redemptionCode", redemptionCode);
			query.setParameter("redemptionDate", Calendar.getInstance().getTime());
			query.executeUpdate();
		}
		catch (ConstraintViolationException ce)
		{
			// try one more time with a new redemptionCode
			LOG.error("Constraint violation: " + ce.getSQLException().getMessage(), ce);
			redemptionCode = redemptionCodeStrategy.generateCode();
			LOG.warn("Regenerated redemptionCode: " + redemptionCode);
			query.setParameter("redemptionCode", redemptionCode);
			query.executeUpdate();
		}
		catch (Exception e)
		{
			LOG.error("Problem redeeming deal: " + e.getLocalizedMessage(), e);
			throw new ServiceException("Problem in redeemDeal with dealAcquireId " + dealAcquireId, e);
		}

		return redemptionCode;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Relationship> getRelationshipsFrom(final UUID customerId) throws ServiceException
	{
		try
		{
			final Search search = new Search(RelationshipImpl.class);
			search.addFilterEqual("fromCustomer.id", customerId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getRelationshipsFrom %s", customerId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Relationship> getRelationshipsTo(final UUID customerId) throws ServiceException
	{
		try
		{
			final Search search = new Search(RelationshipImpl.class);
			search.addFilterEqual("toCustomer.id", customerId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getRelationshipsTo %s", customerId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealAcquire> getDealAcquires(final UUID customerId, final UUID merchantId,
			final SearchOptions searchOpts) throws ServiceException
	{
		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.DealAcquires, null, searchOpts,
					true);

			final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
			query.setParameter("customerId", customerId);
			query.setParameter("merchantId", merchantId);
			QueryHelper.applyOffsetLimit(query, searchOpts);

			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format(
					"Problem getDealAcquires customerId %s merchantId %s", customerId, merchantId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchantAcquires(final UUID customerId, final SearchOptions searchOpts)
			throws ServiceException
	{
		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.MerchantAcquires, null, searchOpts,
					true);

			final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
			query.setParameter("customerId", customerId);
			QueryHelper.applyOffsetLimit(query, searchOpts);
			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getMerchantAcquires customerId %s",
					customerId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchantAcquires(final UUID customerId, final SearchOptions searchOpts, final Location location)
			throws ServiceException
	{
		if (location == null)
		{
			return getMerchantAcquires(customerId, searchOpts);
		}

		List<Merchant> merchants = null;

		try
		{
			final org.postgis.Point point = new org.postgis.Point(location.getLongitude(), location.getLatitude());
			point.setSrid(4326);

			final ImmutableMap<String, Object> params = ImmutableMap.<String, Object> builder()
					.put("point", point.toString()).build();

			final String newSql = QueryHelper.buildQuery(QueryType.MerchantAcquiresLocation, params, searchOpts,
					true);

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

			query.addScalar("merchantLogo", StandardBasicTypes.STRING);
			query.addScalar("merchantImage", StandardBasicTypes.STRING);

			query.addScalar("distanceInMeters", StandardBasicTypes.DOUBLE);
			query.setResultTransformer(new MerchantAcquiresResultTransformer());

			query.setParameter("customerId", customerId, PostgresUUIDType.INSTANCE);

			QueryHelper.applyOffsetLimit(query, searchOpts);

			merchants = query.list();

		}
		catch (Exception ex)
		{
			String msg = "Problem getting merchants acquired in location ";
			LOG.error(msg, ex);
			throw new ServiceException(msg, ex);
		}

		return merchants;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealAcquire> getDealAcquiresByCustomerId(final UUID customerId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(DealAcquireImpl.class);
			search.addFilterEqual("customer.id", customerId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(
					String.format("Problem getDealAcquiresByCustomerId %s", customerId), ex);
		}
	}

	@Override
	public DealAcquire getDealAcquire(final UUID dealAcquireId) throws ServiceException
	{
		DealAcquire dac = null;

		try
		{
			dac = (DealAcquire) getCurrentSession().load(DealAcquireImpl.class, dealAcquireId);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealAcquire %s", dealAcquireId), ex);
		}

		return dac;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Deal> getDealsByCustomerId(final UUID accountId) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory
					.getCurrentSession()
					.createQuery(
							"from MerchantDealImpl md, DealBookPurchaseImpl dbp where dbp.merchantId=md.id and dbp.customerId=:customerId");

			query.setParameter("customerId", accountId);
			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealsByCustomerId %s", accountId), ex);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealOfferPurchase> getDealOfferPurchasesByCustomerId(final UUID customerId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(DealOfferPurchaseImpl.class);
			search.addFilterEqual("customer.id", customerId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealBookPurchasesByCustomerId %s",
					customerId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchantsByCustomerId(final Long customerId) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory
					.getCurrentSession()
					.createQuery(
							"select distinct m from DealBookPurchaseImpl dbp,  MerchantDealImpl md, MerchantImpl m, DealBookContentImpl dbc "
									+ "where dbp.customer.id=:customerId AND dbp.dealBook.id=dbc.dealBook.id AND dbc.merchantDeal.merchant.id=md.merchant.id AND dbc.merchantDeal.merchant.id=m.id");

			query.setParameter("customerId", customerId);
			return query.list();

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getMerchantsByCustomerId %s", customerId),
					ex);
		}
	}

	public DAODispatcher getDaoDispatcher()
	{
		return daoDispatcher;
	}

	public void setDaoDispatcher(DAODispatcher daoDispatcher)
	{
		this.daoDispatcher = daoDispatcher;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addFavoriteMerchant(final UUID customerId, final UUID merchantId) throws ServiceException
	{
		final FavoriteMerchant favMerchant = new FavoriteMerchantImpl(customerId, merchantId);

		try
		{
			daoDispatcher.save(favMerchant);
		}
		catch (Exception e)
		{
			throw new ServiceException(String.format("There was a problem adding favorite merchant: customerId %s merchantId %s",
					customerId, merchantId));
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeFavoriteMerchant(final UUID customerId, final UUID merchantId) throws ServiceException
	{
		try
		{
			final Search search = new Search(FavoriteMerchantImpl.class).addFilterEqual("customerId", customerId).addFilterEqual(
					"merchantId", merchantId);
			final FavoriteMerchant favMerchant = (FavoriteMerchant) daoDispatcher.searchUnique(search);
			if (favMerchant != null)
			{
				daoDispatcher.remove(favMerchant);
			}
			else
			{
				LOG.warn(String.format("Ignoring remove of favorite merchant (not found) customerId %s merchantId %s", customerId,
						merchantId));
			}

		}
		catch (Exception e)
		{
			throw new ServiceException(String.format("There was a problem removing favorite merchant: customerId %s merchantId %s",
					customerId, merchantId));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getFavoriteMerchants(final UUID customerId, final SearchOptions searchOpts) throws ServiceException
	{
		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.FavoriteMerchants, null, searchOpts,
					true);

			final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
			query.setParameter("customerId", customerId);
			QueryHelper.applyOffsetLimit(query, searchOpts);

			return query.list();

		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getting favorite merchant for customerId " + customerId, ex);
		}

	}

	@Transactional(propagation = Propagation.NESTED)
	public void createDealOfferPurchase(final Customer customer, final DealOffer dealOffer, final TransactionResult transactionResult)
			throws ServiceException
	{
		try
		{
			final DealOfferPurchase purchase = new DealOfferPurchaseImpl(customer, dealOffer);
			purchase.setPaymentProcessor(transactionResult.getPaymentProcessor());
			purchase.setProcessorTransactionId(transactionResult.getTransactionId());
			daoDispatcher.save(purchase);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem creating dealOfferPurchase customerId %s dealOfferId %s",
					customer.getId(), dealOffer.getId()), ex);
		}

	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void createDealOfferPurchase(final UUID customerId, final UUID dealOfferId) throws ServiceException
	{
		try
		{
			final SQLQuery query = getCurrentSession().createSQLQuery(
					"insert into public.deal_offer_purchase (customer_id,deal_offer_id) values (:customerId,:dealOfferId)");

			query.setParameter("customerId", customerId, PostgresUUIDType.INSTANCE);
			query.setParameter("dealOfferId", dealOfferId, PostgresUUIDType.INSTANCE);

			query.executeUpdate();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem creating dealOfferPurchase customerId %s dealOfferId %s",
					customerId, dealOfferId), ex);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchantAcquires(final UUID customerId, final Integer categoryId, final SearchOptions searchOpts)
			throws ServiceException
	{
		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.MerchantAcquiresByCatId, null, searchOpts,
					true);

			final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
			query.setParameter("customerId", customerId);
			query.setParameter("categoryId", categoryId);
			QueryHelper.applyOffsetLimit(query, searchOpts);
			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getMerchantAcquires customerId %s categoryId %s", customerId,
					categoryId, ex));
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	public void createGift(final UUID owningCustomerId, final UUID dealAcquireId, final Gift gift)
			throws ServiceException
	{
		final DealAcquire dac = getDealAcquire(dealAcquireId);
		Customer toCust = null;
		Activity sendActivity = null;
		Activity recvActivity = null;

		if (!dac.getCustomer().getId().equals(owningCustomerId))
		{
			throw new ServiceException(ErrorCode.CUSTOMER_DOES_NOT_OWN_DEAL, "dealAcquireId: " + dac.getId()
					+ ", badCustomerId: " + dac.getCustomer().getId());
		}

		final AcquireStatus currentAcquireStatus = dac.getAcquireStatus();

		if (currentAcquireStatus == AcquireStatus.REDEEMED)
		{
			throw new ServiceException(ErrorCode.DEAL_ALREADY_REDEEMED, "dealAcquireId: " + dac.getId());
		}

		// Can only gift a deal that is in a valid state!
		if (currentAcquireStatus != AcquireStatus.ACCEPTED_CUSTOMER_SHARE &&
				currentAcquireStatus != AcquireStatus.ACCEPTED_MERCHANT_SHARE &&
				currentAcquireStatus != AcquireStatus.REJECTED_CUSTOMER_SHARE &&
				currentAcquireStatus != AcquireStatus.REJECTED_MERCHANT_SHARE &&
				currentAcquireStatus != AcquireStatus.PURCHASED)
		{
			throw new ServiceException(ErrorCode.GIFTING_NOT_ALLOWED,
					String.format("acquireStatus %s for dealAcquireId %s", currentAcquireStatus, dac.getId()));
		}

		try
		{

			gift.setDealAcquire(dac);
			gift.setFromCustomer(dac.getCustomer());

			daoDispatcher.save(gift);

			// Send Gift activity if toCustomer exists
			if (gift instanceof FaceBookGift)
			{
				final String facebookId = ((FaceBookGift) (gift)).getToFacebookId();
				toCust = getCustomerBySocialLoginId(facebookId);
				gift.setToCustomer(toCust);
				sendActivity = ActivityFactory.createFacebookSendGift(gift);
				if (toCust != null)
				{
					recvActivity = ActivityFactory.createFacebookRecvGift(gift);
					recvActivity.setGiftId(gift.getId());
				}
			}
			else if (gift instanceof EmailGift)
			{
				toCust = getCustomerByEmail(((EmailGift) gift).getToEmail());
				sendActivity = ActivityFactory.createEmailSendGift(gift);
				gift.setToCustomer(toCust);
				if (toCust != null)
				{
					recvActivity = ActivityFactory.createEmailRecvGift(gift);
					recvActivity.setGiftId(gift.getId());
				}
			}

			if (LOG.isDebugEnabled() && toCust != null)
			{
				LOG.debug("Sending immediate activity to Talool customer " + toCust.getEmail());
			}

			dac.setGift(gift);
			dac.setAcquireStatus(AcquireStatus.PENDING_ACCEPT_CUSTOMER_SHARE);
			daoDispatcher.save(dac);

			getCurrentSession().flush();

			if (sendActivity != null)
			{
				ServiceFactory.get().getActivityService().save(sendActivity);
			}
			if (recvActivity != null)
			{
				ServiceFactory.get().getActivityService().save(recvActivity);
			}

		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem in createGift: " + ex.getLocalizedMessage(), ex);
		}

	}

	@Override
	public Gift getGift(final UUID giftRequestId) throws ServiceException
	{
		try
		{
			return daoDispatcher.find(GiftImpl.class, giftRequestId);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getGiftRequest %s", giftRequestId), ex);
		}
	}

	private GiftOwnership validateGiftOwnership(final UUID giftRequestId, final UUID customerId) throws ServiceException
	{
		final Gift giftRequest = daoDispatcher.find(GiftImpl.class, giftRequestId);
		if (giftRequest == null)
		{
			throw new ServiceException(String.format("giftRequestId %s does not exist", giftRequestId));
		}

		final GiftOwnership giftOwnership = new GiftOwnership();
		giftOwnership.gift = giftRequest;

		final Customer receivingCustomer = getCustomerById(customerId);

		if (receivingCustomer == null)
		{
			throw new ServiceException(String.format("customerId %s does not exist", customerId));
		}

		giftOwnership.receivingCustomer = receivingCustomer;

		if (giftRequest instanceof EmailGiftImpl)
		{
			if (!((EmailGiftImpl) giftRequest).getToEmail().equals(receivingCustomer.getEmail()))
			{
				throw new ServiceException(String.format(
						"receivingCustomerId %s with email %s not the gift receiver for giftRequestId %s",
						receivingCustomer.getId(), receivingCustomer.getEmail(), receivingCustomer));
			}
		}
		else if (giftRequest instanceof FacebookGiftImpl)
		{
			final SocialNetwork facebook = ServiceFactory.get().getTaloolService().
					getSocialNetwork(SocialNetwork.NetworkName.Facebook);

			if (!((FacebookGiftImpl) giftRequest).getToFacebookId().
					equals(receivingCustomer.getSocialAccounts().get(facebook).getLoginId()))
			{
				throw new ServiceException(String.format(
						"receivingCustomerId %s with facebookId %s is not the gift receiver for giftRequestId %s",
						receivingCustomer.getId(), receivingCustomer.getSocialAccounts().get(facebook).getLoginId(), receivingCustomer));
			}

		}

		return giftOwnership;
	}

	@Override
	@Transactional(propagation = Propagation.NESTED, rollbackFor = ServiceException.class)
	public DealAcquire acceptGift(final UUID giftId, final UUID receipientCustomerId) throws ServiceException
	{
		final GiftOwnership giftOwnership = validateGiftOwnership(giftId, receipientCustomerId);

		final DealAcquire dac = giftOwnership.gift.getDealAcquire();

		dac.setAcquireStatus(AcquireStatus.ACCEPTED_CUSTOMER_SHARE);
		// dac.setSharedByCustomer(dac.getCustomer());
		dac.setCustomer(giftOwnership.receivingCustomer);

		// update deal acquire
		daoDispatcher.save(dac);

		// update gift request
		giftOwnership.gift.setGiftStatus(GiftStatus.ACCEPTED);
		daoDispatcher.save(giftOwnership.gift);

		try
		{
			Activity activity = ActivityFactory.createFriendAccept(giftOwnership.gift);
			ServiceFactory.get().getActivityService().save(activity);
		}
		catch (Exception e)
		{
			LOG.error("Problem creating createFriendAccept: " + e.getLocalizedMessage(), e);
		}

		try
		{
			setClosedState(giftOwnership.gift, true);
		}
		catch (Exception e)
		{
			LOG.error("Problem finding/persisting closedState on activity: " + e.getLocalizedMessage());
		}

		return dac;

	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = ServiceException.class)
	private void setClosedState(final Gift gift, final boolean isClosed) throws TException
	{
		final Search search = new Search(ActivityImpl.class);
		search.addFilterEqual("giftId", gift.getId());
		search.addFilterEqual("customerId", gift.getToCustomer().getId());

		final Activity act = (Activity) daoDispatcher.searchUnique(search);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Closing state on gift activity - activityId: " + act.getId());
		}

		ActivityFactory.setActionTaken(act, isClosed);

		daoDispatcher.save(act);

	}

	@Override
	@Transactional(propagation = Propagation.NESTED, rollbackFor = ServiceException.class)
	public void rejectGift(final UUID giftRequestId, final UUID receipientCustomerId) throws ServiceException
	{
		final GiftOwnership giftOwnership = validateGiftOwnership(giftRequestId, receipientCustomerId);

		final DealAcquire dac = giftOwnership.gift.getDealAcquire();

		dac.setAcquireStatus(AcquireStatus.REJECTED_CUSTOMER_SHARE);

		// update deal acquire
		daoDispatcher.save(dac);

		// update gift request
		giftOwnership.gift.setGiftStatus(GiftStatus.REJECTED);
		daoDispatcher.save(giftOwnership.gift);

		Activity activity;
		try
		{
			activity = ActivityFactory.createReject(giftOwnership.gift, receipientCustomerId);
			ServiceFactory.get().getActivityService().save(activity);
		}
		catch (Exception e)
		{
			LOG.error("Problem creating rejectGift: " + e.getLocalizedMessage(), e);
		}

		try
		{
			// create the bi-directional activity
			activity = ActivityFactory.createFriendRejectGift(giftOwnership.gift);
			ServiceFactory.get().getActivityService().save(activity);
		}
		catch (Exception e)
		{
			LOG.error("Problem creating createFriendRejectGift: " + e.getLocalizedMessage(), e);
		}

		try
		{
			setClosedState(giftOwnership.gift, true);
		}
		catch (Exception e)
		{
			LOG.error("Problem finding/persisting closedState on activity: " + e.getLocalizedMessage());
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void remove(final CustomerSocialAccount cas) throws ServiceException
	{
		try
		{
			daoDispatcher.remove(cas);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem in removing CustomerSocialAccount", ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeSocialAccount(final UUID customerId, final SocialNetwork socialNetwork) throws ServiceException
	{
		try
		{
			final Query query = getCurrentSession().getNamedQuery("deleteCustomerSocialAccount");
			query.setParameter("customerId", customerId);
			query.setParameter("socialNetworkId", socialNetwork.getId());
			query.executeUpdate();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem in removing SocialAccount %s for customerId %s", socialNetwork.getName(),
					customerId.toString()), ex);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final CustomerSocialAccount socialAccount) throws ServiceException
	{
		try
		{
			daoDispatcher.save(socialAccount);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem saving CustomerSocialAccount", ex);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Gift> getGifts(final UUID customerId, final GiftStatus[] giftStatus)
			throws ServiceException
	{
		List<Gift> gifts = null;

		try
		{
			final Query query = (Query) getCurrentSession().getNamedQuery("getGifts");
			query.setParameter("customerId", customerId, PostgresUUIDType.INSTANCE);
			query.setParameterList("giftStatus", giftStatus);

			gifts = query.list();

			// query = (Query) getCurrentSession().getNamedQuery("giftsByEmail");
			// query.setParameter("customerId", customerId);
			// query.setParameterList("requestStatus", requestStatus);

			// dealAcquires.addAll(query.list());

		}
		catch (Exception e)
		{
			throw new ServiceException("Problem getGiftedDealAcquires", e);
		}

		return gifts;

	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public UUID giftToFacebook(final UUID owningCustomerId, final UUID dealAcquireId, final String facebookId,
			final String receipientName) throws ServiceException
	{
		final FaceBookGift gift = new FacebookGiftImpl();
		gift.setToFacebookId(facebookId);
		gift.setReceipientName(receipientName);
		createGift(owningCustomerId, dealAcquireId, gift);

		// see if to customer
		return gift.getId();
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public UUID giftToEmail(final UUID owningCustomerId, final UUID dealAcquireId, final String email, final String receipientName)
			throws ServiceException
	{
		final EmailGift gift = new EmailGiftImpl();
		gift.setToEmail(email);
		gift.setReceipientName(receipientName);
		createGift(owningCustomerId, dealAcquireId, gift);

		if (!email.contains(IGNORE_TEST_EMAIL_DOMAIN))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.info("Sending gift email to " + email);
			}
			ServiceFactory.get().getEmailService().sendGiftEmail(gift);
		}

		return gift.getId();

	}

	public UniqueCodeStrategy getRedemptionCodeStrategy()
	{
		return redemptionCodeStrategy;
	}

	public void setRedemptionCodeStrategy(final UniqueCodeStrategy redemptionCodeStrategy)
	{
		this.redemptionCodeStrategy = redemptionCodeStrategy;
	}

	@Override
	public Gift getGiftOnDealAcquire(final UUID dealAcquireId) throws ServiceException
	{
		final Search search = new Search(DealAcquireImpl.class);

		try
		{
			search.addFilterEqual("id", dealAcquireId);
			search.addField("gift");
			return (Gift) daoDispatcher.searchUnique(search);
		}
		catch (Exception ex)
		{
			String msg = "Problem getGiftOnDealAcquire dealAcquireId: " + dealAcquireId;
			LOG.error(msg, ex);
			throw new ServiceException(msg, ex);
		}

	}

	@Override
	public Customer getCustomerBySocialLoginId(final String socialLoginId) throws ServiceException
	{
		final Search search = new Search(CustomerSocialAccountImpl.class);
		try
		{
			search.addFilterEqual("loginId", socialLoginId);
			search.addField("customer");
			return (Customer) daoDispatcher.searchUnique(search);
		}
		catch (Exception ex)
		{
			String msg = "Problem getCustomerBySocialLoginId socialLoginId: " + socialLoginId;
			LOG.error(msg, ex);
			throw new ServiceException(msg, ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void activateCode(final UUID customerId, final UUID dealOfferId, final String code) throws ServiceException
	{
		ActivationCode activationCode = null;

		try
		{
			final Search search = new Search(ActivationCodeImpl.class);
			search.addFilterEqual("dealOfferId", dealOfferId);
			search.addFilterEqual("code", code.toUpperCase());

			activationCode = (ActivationCodeImpl) daoDispatcher.searchUnique(search);

			if (activationCode == null)
			{
				throw new ServiceException(ErrorCode.ACTIVIATION_CODE_NOT_FOUND, code);
			}

			if (activationCode.getCustomerId() != null)
			{
				throw new ServiceException(ErrorCode.ACTIVIATION_CODE_ALREADY_ACTIVATED, code);
			}

			activationCode.setCustomerId(customerId);
			activationCode.setActivatedDate(new Date(System.currentTimeMillis()));

			daoDispatcher.save(activationCode);

			final DealOffer dealOffer = ServiceFactory.get().getTaloolService().getDealOffer(dealOfferId);
			final Activity act = ActivityFactory.createActivatedByCode(dealOffer, customerId, code);

			createDealOfferPurchase(customerId, dealOfferId);

			ServiceFactory.get().getActivityService().save(act);
		}
		catch (ServiceException se)
		{
			throw se;
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem createActivationCodes dealOfferId %s", dealOfferId), ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void createPasswordReset(final Customer customer) throws ServiceException
	{
		try
		{
			customer.setResetPasswordCode(RandomStringUtils.randomAlphanumeric(16));
			customer.setResetPasswordExpires(DateUtils.addHours(Calendar.getInstance().getTime(), 2));

			daoDispatcher.save(customer);

			ServiceFactory.get().getEmailService().sendPasswordRecoveryEmail(customer);
		}
		catch (Exception e)
		{
			throw new ServiceException("Problem creating password reset for email " + customer.getEmail(), e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	/**
	 * TODO Test rollback on failures, VOID charge
	 */
	public TransactionResult purchaseByCard(final UUID customerId, final UUID dealOfferId, final PaymentDetail paymentDetail)
			throws ServiceException, NotFoundException
	{
		DealOffer dealOffer = null;
		Customer customer = null;
		TransactionResult transactionResult = null;

		try
		{
			// TODO Optimize the heavy call which pulls dealOffers - maybe ehcache
			// DealOffers
			dealOffer = ServiceFactory.get().getTaloolService().getDealOffer(dealOfferId);
			customer = ServiceFactory.get().getCustomerService().getCustomerById(customerId);
		}
		catch (ServiceException se)
		{
			throw se;
		}

		if (dealOffer == null)
		{
			throw new NotFoundException("deal offer", dealOfferId == null ? null : dealOfferId.toString());
		}

		if (customer == null)
		{
			throw new NotFoundException("customer", customerId == null ? null : customerId.toString());
		}

		try
		{
			transactionResult = BraintreeUtil.processCard(customer, dealOffer, paymentDetail);
		}
		catch (ProcessorException e)
		{
			throw new ServiceException(ErrorCode.GENERAL_PROCESSOR_ERROR, e);
		}

		if (transactionResult.isSuccess())
		{
			try
			{
				createDealOfferPurchase(customer, dealOffer, transactionResult);
				getCurrentSession().flush();
			}
			catch (ServiceException e)
			{
				try
				{
					rollbackPaymentTransaction(customerId, dealOfferId, transactionResult, e);
				}
				catch (ProcessorException pe)
				{
					LOG.error("Transaction not rolled back with processor! " + pe.getMessage(), pe);
				}

				throw e;
			}

			try
			{
				// create a purchase activity. if it fails, we will not rollback the
				// entire transaction
				final Activity activity = ActivityFactory.createPurchase(dealOffer, customer.getId());
				ServiceFactory.get().getActivityService().save(activity);
			}
			catch (TException e)
			{
				LOG.error(String.format("Activity not created for purchase customerId '%s' dealOfferId '%s'", customerId, dealOfferId), e);
			}

		}
		else
		{
			LOG.warn(String.format("Transaction failed for customerId '%s' with message '%s'", customerId, transactionResult.getMessage()));
		}
		return transactionResult;

	}

	private void rollbackPaymentTransaction(final UUID customerId, final UUID dealOfferId, final TransactionResult transactionResult,
			final ServiceException causedException) throws ProcessorException
	{
		LOG.error(String.format("Creating dealOffer failed customerId '%s' dealOfferId '%s' rolling back transactionId '%s'",
				customerId, dealOfferId, transactionResult.getTransactionId()), causedException);

		TransactionResult voidedTrans = null;

		voidedTrans = BraintreeUtil.voidTransaction(transactionResult.getTransactionId());

		LOG.error(String.format("Payment transaction roll back of transactionId '%s' success %s ", transactionResult.getTransactionId(),
				voidedTrans.isSuccess()));

	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public TransactionResult purchaseByCode(final UUID customerId, final UUID dealOfferId, final String paymentCode) throws ServiceException,
			NotFoundException
	{
		DealOffer dealOffer = null;
		Customer customer = null;
		Activity activity = null;
		TransactionResult transactionResult = null;

		try
		{
			// TODO Optimize the heavy call which pulls dealOffers - maybe ehcache
			// DealOffers
			dealOffer = ServiceFactory.get().getTaloolService().getDealOffer(dealOfferId);
			customer = ServiceFactory.get().getCustomerService().getCustomerById(customerId);
		}
		catch (ServiceException se)
		{
			throw se;
		}

		if (dealOffer == null)
		{
			throw new NotFoundException("deal offer", dealOfferId == null ? null : dealOfferId.toString());
		}

		if (customer == null)
		{
			throw new NotFoundException("customer", customerId == null ? null : customerId.toString());
		}

		try
		{
			transactionResult = BraintreeUtil.processPaymentCode(customer, dealOffer, paymentCode);
		}
		catch (ProcessorException e)
		{
			throw new ServiceException(ErrorCode.GENERAL_PROCESSOR_ERROR, e);
		}

		if (transactionResult.isSuccess())
		{
			try
			{
				createDealOfferPurchase(customer, dealOffer, transactionResult);
				getCurrentSession().flush();
			}
			catch (ServiceException e)
			{
				try
				{
					rollbackPaymentTransaction(customerId, dealOfferId, transactionResult, e);
				}
				catch (ProcessorException pe)
				{
					LOG.error("Transaction not rolled back with processor! " + pe.getMessage(), pe);
				}

				throw e;
			}

			try
			{
				// if it fails, we will not rollback the entire transaction
				activity = ActivityFactory.createPurchase(dealOffer, customer.getId());
				ServiceFactory.get().getActivityService().save(activity);
			}
			catch (TException e)
			{
				LOG.error(String.format("Activity not created for purchase customerId '%s' dealOfferId '%s'", customerId, dealOfferId), e);
			}
			catch (ServiceException e)
			{
				LOG.error(String.format("Activity not created for purchase customerId '%s' dealOfferId '%s'", customerId, dealOfferId), e);
			}

		}
		else
		{
			LOG.warn(String.format("Transaction failed for customerId '%s' with message '%s'", customerId, transactionResult.getMessage()));
		}
		return transactionResult;

	}
}
