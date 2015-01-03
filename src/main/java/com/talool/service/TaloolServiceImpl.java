package com.talool.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.postgis.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.braintreegateway.MerchantAccount.Status;
import com.braintreegateway.ValidationError;
import com.braintreegateway.ValidationErrors;
import com.braintreegateway.WebhookNotification;
import com.google.common.collect.ImmutableMap;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;
import com.talool.cache.DealOfferMetadataCache;
import com.talool.core.AccountType;
import com.talool.core.ActivationCode;
import com.talool.core.ActivationSummary;
import com.talool.core.Category;
import com.talool.core.CategoryTag;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealAcquireHistory;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferGeoSummariesResult;
import com.talool.core.DealOfferGeoSummary;
import com.talool.core.DealOfferPurchase;
import com.talool.core.DealType;
import com.talool.core.FactoryManager;
import com.talool.core.Location;
import com.talool.core.MediaType;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantCodeGroup;
import com.talool.core.MerchantIdentity;
import com.talool.core.MerchantLocation;
import com.talool.core.MerchantMedia;
import com.talool.core.PropertyEntity;
import com.talool.core.RefundResult;
import com.talool.core.SearchOptions;
import com.talool.core.Sex;
import com.talool.core.Tag;
import com.talool.core.purchase.UniqueCodeStrategy;
import com.talool.core.service.ProcessorException;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.core.social.SocialNetwork;
import com.talool.domain.ActivationCodeImpl;
import com.talool.domain.CategoryImpl;
import com.talool.domain.CategoryTagImpl;
import com.talool.domain.CustomerImpl;
import com.talool.domain.DealAcquireHistoryImpl;
import com.talool.domain.DealAcquireImpl;
import com.talool.domain.DealImpl;
import com.talool.domain.DealOfferImpl;
import com.talool.domain.DealOfferPurchaseImpl;
import com.talool.domain.MerchantAccountImpl;
import com.talool.domain.MerchantCodeGroupImpl;
import com.talool.domain.MerchantCodeImpl;
import com.talool.domain.MerchantIdentityImpl;
import com.talool.domain.MerchantImpl;
import com.talool.domain.MerchantLocationImpl;
import com.talool.domain.MerchantMediaImpl;
import com.talool.domain.Properties;
import com.talool.domain.PropertyCriteria;
import com.talool.domain.TagImpl;
import com.talool.domain.gift.GiftImpl;
import com.talool.domain.job.MessagingJobImpl;
import com.talool.domain.social.SocialNetworkImpl;
import com.talool.payment.braintree.BraintreeUtil;
import com.talool.payment.braintree.BraintreeUtil.RefundType;
import com.talool.payment.braintree.BraintreeUtil.RefundVoidResult;
import com.talool.persistence.HstoreUserType;
import com.talool.persistence.QueryHelper;
import com.talool.persistence.QueryHelper.QueryType;
import com.talool.purchase.DealUniqueConfirmationCodeStrategyImpl;
import com.talool.stats.DealOfferMetadata;
import com.talool.stats.DealOfferMetrics;
import com.talool.stats.DealOfferMetrics.MetricType;
import com.talool.stats.DealOfferSummary;
import com.talool.stats.DealSummary;
import com.talool.stats.FundraiserSummary;
import com.talool.stats.MerchantCodeSummary;
import com.talool.stats.MerchantSummary;
import com.talool.stats.PaginatedResult;
import com.talool.utils.GraphiteConstants.Action;
import com.talool.utils.GraphiteConstants.SubAction;
import com.talool.utils.KeyValue;
import com.talool.utils.SpatialUtils;
import com.talool.utils.TaloolStatsDClient;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * Implementation of the TaloolService
 * 
 * @author clintz
 */
@Transactional(readOnly = true)
@Service
@Repository
public class TaloolServiceImpl extends AbstractHibernateService implements TaloolService {
  private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceImpl.class);

  public static final float MILES_TO_METERS = 1609.34f;

  private static final ThreadLocal<Map<String, String>> requestHeaders = new ThreadLocal<Map<String, String>>();

  private UniqueCodeStrategy activiationCodeStrategy = new DealUniqueConfirmationCodeStrategyImpl(7, '0', 'O');

  public TaloolServiceImpl() {}

  @Override
  public SocialNetwork getSocialNetwork(final SocialNetwork.NetworkName name) throws ServiceException {
    SocialNetwork snet;
    try {

      final Search search = new Search(SocialNetworkImpl.class);
      search.addFilterEqual("name", name.toString());
      snet = (SocialNetwork) daoDispatcher.searchUnique(search);

    } catch (Exception ex) {
      throw new ServiceException("Problem getSocialNetwork  " + name.toString(), ex);
    }

    return snet;
  }

  @Override
  public boolean emailExists(final AccountType accountType, final String email) throws ServiceException {
    try {
      final Search search = accountType == AccountType.CUS ? new Search(CustomerImpl.class) : new Search(MerchantImpl.class);

      search.addField("id");
      search.addFilterEqual("email", email.toLowerCase());
      final UUID id = (UUID) daoDispatcher.searchUnique(search);
      return id == null ? false : true;
    } catch (Exception ex) {
      throw new ServiceException("Problem emailExists  " + email, ex);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteMerchant(final String id) throws ServiceException {
    removeElement(id, MerchantImpl.class);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final Merchant merchant) throws ServiceException {
    try {
      daoDispatcher.save(merchant);
    } catch (Exception e) {
      final String err = "There was a problem saving merchant " + merchant;
      LOG.error(err, e);
      throw new ServiceException(err, e);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Merchant getMerchantById(final UUID id) throws ServiceException {
    Merchant merchant;
    try {
      merchant = daoDispatcher.find(MerchantImpl.class, id);
    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantById  " + id, ex);
    }

    return merchant;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Merchant> getMerchantByName(final String name) throws ServiceException {
    List<Merchant> merchants = null;

    try {
      final Search search = new Search(MerchantImpl.class);
      search.addFilterEqual("name", name);
      merchants = daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantByName  " + name, ex);
    }

    return merchants;
  }

  private void removeElement(final String id, Class<MerchantImpl> clazz) throws ServiceException {
    boolean deleted = false;
    try {
      deleted = daoDispatcher.removeById(clazz, id);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem removing ID '%d' for domain %s", id, clazz.getSimpleName()), ex);
    }

    if (!deleted) {
      throw new ServiceException((String.format("Element ID '%d' not found for domain %s", id, clazz.getSimpleName())));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Deal> getMerchantDeals(final UUID merchantId, final Boolean isActive) throws ServiceException {
    List<Deal> merchantDeals = null;

    try {
      final Search search = new Search(DealImpl.class);
      search.addFilterEqual("merchant.id", merchantId);
      if (isActive != null) {
        search.addFilterEqual("isActive", isActive);
      }
      merchantDeals = daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantDeals for merchantId " + merchantId, ex);
    }

    return merchantDeals;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final Deal merchantDeal) throws ServiceException {
    try {
      daoDispatcher.save((DealImpl) merchantDeal);
    } catch (Exception e) {
      final String err = "There was a problem saving merchantDeal " + merchantDeal;
      throw new ServiceException(err, e);
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Deal> getDealsByMerchantId(final UUID merchantId) throws ServiceException {
    try {
      final Search search = new Search(DealImpl.class);
      search.addFilterEqual("merchant.id", merchantId);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealsByMerchantId %s", merchantId), ex);
    }
  }

  @Override
  public Tag getTag(final String tagName) throws ServiceException {
    try {
      final Search search = new Search(TagImpl.class);
      search.addFilterEqual("name", tagName);
      return (Tag) daoDispatcher.searchUnique(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getTag %s", tagName), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Tag> getTags() throws ServiceException {
    try {
      final Search search = new Search(TagImpl.class);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException("Problem getTags", ex);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void addTags(final List<Tag> tags) throws ServiceException {
    try {
      for (Tag tag : tags) {
        daoDispatcher.save((TagImpl) tag);
      }

    } catch (Exception e) {
      throw new ServiceException("Problem adding tags", e);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final MerchantAccount merchantAccount) throws ServiceException {
    try {
      daoDispatcher.save((MerchantAccountImpl) merchantAccount);
    } catch (Exception e) {
      final String err = "There was a problem saving merchantAccount " + merchantAccount;
      throw new ServiceException(err, e);
    }
  }

  @Override
  public MerchantAccount authenticateMerchantAccount(final UUID merchantId, final String email, final String password) throws ServiceException {
    try {
      final String md5pass = EncryptService.MD5(password);
      final Query query =
          sessionFactory.getCurrentSession()
              .createQuery("from MerchantAccountImpl where merchant.id=:merchantId and email=:email and password=:pass");

      query.setParameter("merchantId", merchantId);
      query.setParameter("email", email);
      query.setParameter("pass", md5pass);

      TaloolStatsDClient.get().count(Action.authenticate, SubAction.merchant, null, requestHeaders.get());

      return (MerchantAccount) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem authenticateMerchantAccount %s %s", merchantId, email), ex);
    }

  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final DealOffer dealOffer) throws ServiceException {
    try {
      daoDispatcher.save(dealOffer);
    } catch (Exception e) {
      throw new ServiceException("There was a problem saving dealOffer ", e);
    }

  }

  @Override
  public DealOffer getDealOffer(final UUID dealOfferId) throws ServiceException {
    try {
      final Search search = new Search(DealOfferImpl.class);
      search.addFilterEqual("id", dealOfferId);
      return (DealOffer) daoDispatcher.searchUnique(search);
    } catch (Exception e) {
      throw new ServiceException("There was a problem in getDealOfferById", e);
    }
  }

  @Override
  public Deal getDeal(final UUID dealId) throws ServiceException {
    try {
      return daoDispatcher.find(DealImpl.class, dealId);
    } catch (Exception e) {
      throw new ServiceException("There was a problem in getDeal " + dealId, e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Deal> getDealsByDealOfferId(final UUID dealOfferId, final SearchOptions searchOpts, boolean activeDealsOnly) throws ServiceException {
    List<Deal> deals = null;

    try {
      final String newSql =
          activeDealsOnly ? QueryHelper.buildQuery(QueryType.ActiveDealsByDealOfferId, null, searchOpts) : QueryHelper.buildQuery(
              QueryType.DealsByDealOfferId, null, searchOpts);

      final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
      query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
      query.setParameter("dealOfferId", dealOfferId, PostgresUUIDType.INSTANCE);

      if (activeDealsOnly) {
        query.setParameter("expiresDate", new Date(), StandardBasicTypes.DATE);
      }

      QueryHelper.applyOffsetLimit(query, searchOpts);

      deals = query.list();

    } catch (Exception ex) {
      throw new ServiceException("Problem getDealsByDealOfferId for dealOfferId " + dealOfferId + " " + ex.getLocalizedMessage(), ex);
    }

    return deals;

  }

  private static void setSearchOptions(final Search search, final SearchOptions searchOpts, final String[] eagerlyLoadedProps) {
    if (searchOpts != null) {
      if (searchOpts.getMaxResults() != null) {
        search.setMaxResults(searchOpts.getMaxResults());
      }
      if (searchOpts.getPage() != null) {
        search.setPage(searchOpts.getPage());
      }
      if (searchOpts.getSortProperty() != null) {
        search.addSort(searchOpts.getSortProperty(), !searchOpts.isAscending());
      }
    }

    search.addFetches(eagerlyLoadedProps);

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealOfferPurchase> getDealOfferPurchasesByDealOfferId(final UUID dealOfferId) throws ServiceException {
    try {
      final Search search = new Search(DealOfferPurchaseImpl.class);
      search.addFilterEqual("dealOffer.id", dealOfferId);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealBookPurchasesByDealOfferId %s", dealOfferId), ex);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final DealOfferPurchase dealOfferPurchase) throws ServiceException {
    try {
      daoDispatcher.save(dealOfferPurchase);
    } catch (Exception e) {
      throw new ServiceException("There was a problem saving dealOfferPurchase ", e);
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Merchant> getMerchants() throws ServiceException {
    try {
      Criteria crit = getSessionFactory().getCurrentSession().createCriteria(MerchantImpl.class);
      crit.setCacheMode(CacheMode.GET);
      crit.setCacheable(true);
      return crit.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getMerchants"), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Merchant> getAllMerchants() throws ServiceException {
    List<Merchant> merchants = null;

    try {
      final Criteria criteria = getCurrentSession().createCriteria(MerchantImpl.class);
      criteria.addOrder(Order.asc("name"));

      merchants = criteria.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealOffers"), ex);
    }

    return merchants;
  }

  @Override
  public Long sizeOfCollection(Object collection) throws ServiceException {
    return ((Long) getSessionFactory().getCurrentSession().createFilter(collection, "select count(*)").list().get(0)).longValue();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Set<Tag> getDealOfferTags(final UUID dealOfferId) throws ServiceException {
    Set<Tag> tags = null;

    try {
      final Query query =
          getSessionFactory()
              .getCurrentSession()
              .createSQLQuery(
                  "select distinct t.* from tag as t, deal_tag as dt,deal_offer as dof, deal as d where t.tag_id=dt.tag_id and dof.deal_offer_id=d.deal_offer_id and d.deal_offer_id=:dealOfferId and d.deal_id=dt.deal_id")
              .addEntity(TagImpl.class);

      query.setParameter("dealOfferId", dealOfferId, PostgresUUIDType.INSTANCE);

      final List<Tag> tagList = (query.list());

      if (CollectionUtils.isNotEmpty(tagList)) {
        tags = new HashSet<Tag>();
        tags.addAll(tagList);
      }

      return tags;

    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealOfferTags %s", dealOfferId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealOffer> getDealOffers() throws ServiceException {
    try {
      final Criteria criteria = getCurrentSession().createCriteria(DealOfferImpl.class);
      return criteria.list();

    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealOffers"), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealOffer> getDealOffersByMerchantId(final UUID merchantId) throws ServiceException {
    try {
      final Search search = new Search(DealOfferImpl.class);
      search.addSort(Sort.desc("createdUpdated.updated"));
      search.addFilterEqual("merchant.id", merchantId);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealOffersByMerchantId %s", merchantId), ex);
    }
  }

  private String cleanTagName(final String tagName) {
    return tagName.trim().toLowerCase();
  }

  /**
   * TODO read tags from a TagCache or use 2nd level caching!
   */
  @Override
  public Set<Tag> getOrCreateTags(final String... tags) throws ServiceException {
    Set<Tag> tagList = new HashSet<Tag>();

    for (final String tagName : tags) {

      Tag _tag = getTag(cleanTagName(tagName));
      if (_tag != null) {
        tagList.add(_tag);
      } else {
        _tag = FactoryManager.get().getDomainFactory().newTag(tagName);
        _tag.setName(tagName);
        tagList.add(_tag);
      }
    }

    return tagList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<MerchantAccount> getAccountsForMerchant(final UUID merchantId) throws ServiceException {
    try {
      final Search search = new Search(MerchantAccountImpl.class);
      search.addFilterEqual("merchant.id", merchantId);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getAccountsForMerchant %s", merchantId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<MerchantLocation> getLocationsForMerchant(UUID merchantId) throws ServiceException {
    try {
      final Search search = new Search(MerchantLocationImpl.class);
      search.addFilterEqual("merchant.id", merchantId);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getLocationsForMerchant %s", merchantId), ex);
    }
  }

  @Override
  public MerchantLocation getMerchantLocationById(Long merchantLocationId) throws ServiceException {
    MerchantLocation merchantLocation;
    try {
      merchantLocation = daoDispatcher.find(MerchantLocationImpl.class, merchantLocationId);
    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantLocationById  " + merchantLocationId, ex);
    }

    return merchantLocation;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final MerchantLocation merchantLocation) throws ServiceException {
    try {
      daoDispatcher.save((MerchantLocationImpl) merchantLocation);
    } catch (Exception e) {
      final String err = "There was a problem saving MerchantLocation " + merchantLocation;
      throw new ServiceException(err, e);
    }

  }

  @Override
  public MerchantAccount getMerchantAccountById(Long merchantAccountId) throws ServiceException {
    MerchantAccount merchantAccount;
    try {
      merchantAccount = daoDispatcher.find(MerchantAccountImpl.class, merchantAccountId);
    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantLocationById  " + merchantAccountId, ex);
    }

    return merchantAccount;
  }

  @SuppressWarnings("unchecked")
  @Override
  public MerchantAccount authenticateMerchantAccount(final String email, final String password) throws ServiceException {
    List<MerchantAccount> accounts = null;

    try {
      final Search search = new Search(MerchantAccountImpl.class);
      search.addFilterEqual("email", email);
      accounts = daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem authenticateMerchantAccount %s", email), ex);
    }

    if (CollectionUtils.isNotEmpty(accounts)) {
      if (accounts.size() > 1) {
        throw new ServiceException(String.format("%s is associated with mutliple merchant accounts. Cannot authenticate via a simple email/pass",
            email));
      }

      try {
        if (accounts.get(0).getPassword().equals(EncryptService.MD5(password))) {
          TaloolStatsDClient.get().count(Action.authenticate, SubAction.merchant, null, requestHeaders.get());
          return accounts.get(0);
        }
      } catch (Exception ex) {
        throw new ServiceException("Problem authentication merchant account for email " + email, ex);
      }
    }

    return null;

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<MerchantIdentity> getAuthorizedMerchantIdentities(final Long merchantAccountId) throws ServiceException {
    List<MerchantIdentity> identies;

    try {
      final Query query =
          getSessionFactory().getCurrentSession()
              .createSQLQuery("select m.merchant_id as id, m.merchant_name as name from merchant as m order by m.merchant_name asc")
              .addScalar("id", StandardBasicTypes.UUID_CHAR).addScalar("name", StandardBasicTypes.STRING)
              .setResultTransformer(Transformers.aliasToBean(MerchantIdentityImpl.class));

      identies = query.list();
    } catch (Exception ex) {
      throw new ServiceException("Problem getAuthorizedMerchants " + merchantAccountId, ex);
    }

    return identies;

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Deal> getAllRelatedDealsForMerchantId(final UUID merchantId) throws ServiceException {
    try {
      final Query query = sessionFactory.getCurrentSession().getNamedQuery("allRelatedDeals");

      query.setParameter("merchantId", merchantId);

      return query.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getAllRelatedDealsForMerchantId %s", merchantId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealOffer> getAllRelatedDealsOffersForMerchantId(final UUID merchantId) throws ServiceException {
    try {
      final Search search = new Search(DealOfferImpl.class);
      // TODO read permissions or other to get only deal offers visible to
      // merchantId
      search.addFilterEqual("merchant.id", merchantId);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getAllRelatedDealsOffersForMerchantId %s", merchantId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealAcquireHistory> getDealAcquireHistory(final UUID dealAcquireId, boolean chronological) throws ServiceException {
    try {
      final Search search = new Search(DealAcquireHistoryImpl.class);
      search.addFilter(Filter.equal("primaryKey.dealAcquire.id", dealAcquireId));

      if (chronological) {
        search.addSort(Sort.asc("primaryKey.updated"));
      } else {
        search.addSort(Sort.desc("primaryKey.updated"));
      }

      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealAcquireHistory %s", dealAcquireId), ex);
    }
  }

  @Override
  public List<Merchant> getMerchantsWithin(final Location location, final int maxMiles, SearchOptions searchOpts) throws ServiceException {
    final List<Merchant> merchants = new ArrayList<Merchant>();

    final Point point = new Point(location.getLongitude(), location.getLatitude());
    point.setSrid(4326);

    final ImmutableMap<String, Object> params =
        ImmutableMap.<String, Object>builder().put("point", point.toString()).put("isDiscoverable", true)
            .put("distanceInMeters", SpatialUtils.milesToMeters(maxMiles)).build();

    final String newSql = QueryHelper.buildQuery(QueryType.MerchantsWithinMeters, params, searchOpts);

    try {
      final SQLQuery query = getSessionFactory().getCurrentSession().createSQLQuery(newSql);
      query.addScalar("distanceInMeters", StandardBasicTypes.DOUBLE);
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("name", StandardBasicTypes.STRING);
      query.addEntity("merchant_location", MerchantLocationImpl.class);
      query.addEntity("category", CategoryImpl.class);

      final Map<UUID, MerchantImpl> merchantMap = new HashMap<UUID, MerchantImpl>();

      query.setResultTransformer(new ResultTransformer() {

        private static final long serialVersionUID = 1L;

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
          final UUID uuid = (UUID) tuple[1];
          final String name = (String) tuple[2];
          final MerchantLocationImpl location = (MerchantLocationImpl) tuple[3];
          final Category category = (Category) tuple[4];
          location.setDistanceInMeters((Double) tuple[0]);
          MerchantImpl merchant = merchantMap.get(uuid);

          if (merchant == null) {
            merchant = new MerchantImpl();
            merchant.setId(uuid);
            merchant.setName(name);
            merchant.setCategory(category);

            merchant.getLocations().add(location);
            merchantMap.put(uuid, merchant);
            merchants.add(merchant);
          } else {
            merchant.getLocations().add(location);
          }

          return null;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public List transformList(List collection) {
          return merchants;
        }
      });

      query.list();

    } catch (Exception ex) {
      String msg = String.format("Problem getting merchants within lng/lat %s and maxMiles %d", location, maxMiles);
      LOG.error(msg, ex);
      throw new ServiceException(msg, ex);
    }

    return merchants;

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Merchant> getMerchantsWithin2(final Location location, final int maxMiles, SearchOptions searchOpts) throws ServiceException {
    List<Merchant> merchants = null;

    final Coordinate coord = new Coordinate(location.getLongitude(), location.getLatitude());

    @SuppressWarnings("deprecation")
    final com.vividsolutions.jts.geom.Geometry point =
        new com.vividsolutions.jts.geom.Point(coord, new PrecisionModel(PrecisionModel.FLOATING), 4326);

    try {
      final Criteria criteria = getCurrentSession().createCriteria(MerchantImpl.class, "merchant");
      criteria.createAlias("locations", "loc");

      criteria.add(SpatialRestrictions.distanceWithin("loc.geometry", point, SpatialUtils.milesToMeters(maxMiles)));

      merchants = criteria.list();

      for (final Merchant merch : merchants) {
        for (MerchantLocation loc : merch.getLocations()) {
          System.out.println("Distance away : " + SpatialUtils.metersToMiles(loc.getGeometry().distance(point)));
        }

      }

    } catch (Exception ex) {
      String msg = String.format("Problem getting merchants within lng/lat %s and maxMiles %d", location, maxMiles);
      LOG.error(msg, ex);
      throw new ServiceException(msg, ex);
    }

    return merchants;

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Category> getAllCategories() throws ServiceException {
    try {
      final Search search = new Search(CategoryImpl.class);
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getAllCategories"), ex);
    }
  }

  @Transactional(propagation = Propagation.NESTED)
  protected void persistObject(Object obj) throws ServiceException {
    try {
      daoDispatcher.save(obj);
    } catch (Exception e) {
      throw new ServiceException("There was a problem saving object" + obj, e);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final Tag tag) throws ServiceException {
    persistObject(tag);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final Category category) throws ServiceException {
    persistObject(category);
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public CategoryTag createCategoryTag(final String categoryName, final String tagName) throws ServiceException {
    CategoryTag categoryTag;

    try {
      Category category = getCategory(categoryName);
      Tag tag = getTag(tagName);
      if (category == null) {
        category = new CategoryImpl();
        category.setName(categoryName);
      }
      if (tag == null) {
        tag = new TagImpl();
        tag.setName(tagName);
      }

      categoryTag = new CategoryTagImpl(category, tag);

      daoDispatcher.save(category);
      daoDispatcher.save(tag);
      daoDispatcher.save(categoryTag);

    } catch (Exception e) {
      throw new ServiceException(String.format("There was a problem saving category %s tageName %s", categoryName, tagName), e);
    }

    return categoryTag;
  }

  @Override
  public Category getCategory(final String categoryName) throws ServiceException {
    try {
      final Search search = new Search(CategoryImpl.class);
      search.addFilterEqual("name", categoryName);
      return (Category) daoDispatcher.searchUnique(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getCategory %s", categoryName), ex);
    }
  }

  @Override
  public Category getCategory(final Integer categoryId) throws ServiceException {
    try {
      return (Category) getCurrentSession().get(CategoryImpl.class, categoryId);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getCategory %s", categoryId), ex);
    }
  }

  @Override
  public Map<Category, List<Tag>> getCategoryTags() throws ServiceException {
    final Map<Category, List<Tag>> catTagMap = new HashMap<Category, List<Tag>>();

    try {
      final Query query = getSessionFactory().getCurrentSession().getNamedQuery("allCategoryTags").setResultTransformer(new ResultTransformer() {
        private static final long serialVersionUID = 1L;

        @Override
        public Object transformTuple(final Object[] tuple, final String[] aliases) {
          final Category cat = (Category) tuple[0];
          final Tag tag = (Tag) tuple[1];

          List<Tag> tagList = catTagMap.get(cat);
          if (tagList == null) {
            tagList = new ArrayList<Tag>();
          }
          tagList.add(tag);

          catTagMap.put(cat, tagList);

          return null;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public List transformList(List collection) {
          // nothing to return, we built our map
          return collection;
        }
      });

      query.list();

    } catch (Exception ex) {
      throw new ServiceException("Problem getCategoryTags", ex);
    }

    return catTagMap;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<MerchantMedia> getMerchantMedias(final UUID merchantId, final MediaType[] mediaTypes, final SearchOptions searchOpts)
      throws ServiceException {
    try {
      final String newSql = QueryHelper.buildQuery(QueryType.GetMerchantMedias, null, searchOpts, true);

      final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
      query.setParameter("merchantId", merchantId);
      query.setParameterList("mediaTypes", mediaTypes);
      QueryHelper.applyOffsetLimit(query, searchOpts);
      return query.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getMerchantMedias merchantId %s", merchantId, merchantId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<MerchantMedia> getStockMedias(final UUID merchantId, final Set<Tag> tags, final SearchOptions searchOptions) throws ServiceException {
    MediaType[] mediaTypes = new MediaType[] {MediaType.DEAL_IMAGE, MediaType.MERCHANT_IMAGE};
    List<String> tagNames = new ArrayList<String>();
    for (Tag t : tags) {
      tagNames.add(t.getName());
    }
    try {

      if (tags.isEmpty()) {
        final String newSql = QueryHelper.buildQuery(QueryType.GetStockMediaWithoutTags, null, searchOptions, true);

        final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
        query.setParameter("merchantId", merchantId);
        query.setParameterList("mediaTypes", mediaTypes);
        QueryHelper.applyOffsetLimit(query, searchOptions);
        return query.list();
      } else {
        final String newSql = QueryHelper.buildQuery(QueryType.GetStockMediaByTags, null, searchOptions, true);

        final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
        query.setParameter("merchantId", merchantId);
        query.setParameterList("mediaTypes", mediaTypes);
        query.setParameterList("tags", tagNames);
        QueryHelper.applyOffsetLimit(query, searchOptions);
        return query.list();
      }

    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getStockMedias merchantId %s", merchantId, merchantId), ex);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void saveMerchantMedia(final MerchantMedia merchantMedia) throws ServiceException {
    try {
      daoDispatcher.save(merchantMedia);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem saving merchantMedia for merchantId '%s' mediaUrl '%s' ", merchantMedia.getMerchantId(),
          merchantMedia.getMediaUrl(), ex));
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealAcquire> getRedeemedDealAcquires(final UUID merchantId, final String redemptionCode) throws ServiceException {
    try {

      final Search search = new Search(DealAcquireImpl.class);
      search.addFilterEqual("redemptionCode", redemptionCode);
      search.addFilterEqual("deal.merchant.id", merchantId);
      return (List<DealAcquire>) daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getRedeemedDealAcquires redemptionCode %s", redemptionCode), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealAcquireHistory> getDealAcquireHistoryByGiftId(final UUID giftId, final boolean chronological) throws ServiceException {
    try {
      final Query query =
          getCurrentSession()
              .createQuery(
                  "select d from DealAcquireHistoryImpl as d, GiftImpl as g where d.primaryKey.dealAcquire.id=g.dealAcquire.id and g.id=:giftId order by d.primaryKey.updated desc");
      query.setParameter("giftId", giftId, PostgresUUIDType.INSTANCE);

      return query.list();

    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealAcquireHistoryByGiftId %s", giftId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  /**
   * Special note: this method is intended to be temporary as we dont want to have activation codes a core part of our platform.
   * If activiation codes become core, the algorithm and functionality of this method should be revisited
   */
  public void createActivationCodes(final UUID dealOfferId, final int totalCodes) throws ServiceException {
    final Set<ActivationCode> currentCodes = new HashSet<ActivationCode>();
    final List<ActivationCode> newCodes = new ArrayList<ActivationCode>();

    try {
      final Search search = new Search(ActivationCodeImpl.class);
      search.addFilterEqual("dealOfferId", dealOfferId);
      search.addField("code");
      currentCodes.addAll(daoDispatcher.search(search));

      int i = 0;
      while (i < totalCodes) {
        final String code = activiationCodeStrategy.generateCode();

        if (!currentCodes.contains(code)) {
          final ActivationCode actCode = new ActivationCodeImpl();
          actCode.setCode(code);
          actCode.setDealOfferId(dealOfferId);
          newCodes.add(actCode);
          i++;
        } else {
          LOG.warn(String.format("duplicated activation code %s for dealOfferId %s", code, dealOfferId));
        }
      }

      daoDispatcher.save(newCodes.toArray());

    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem createActivationCodes dealOfferId %s", dealOfferId), ex);
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<String> getActivationCodes(final UUID dealOfferId) throws ServiceException {
    try {
      final Search search = new Search(ActivationCodeImpl.class);
      search.addFilterEqual("dealOfferId", dealOfferId);
      search.addField("code");
      // we want ascending so the first created code is first in the list
      search.addSort(new Sort("created", false));
      return daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem createActivationCodes dealOfferId %s", dealOfferId), ex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ActivationSummary> getActivationSummaries(final UUID merchantId) throws ServiceException {
    List<ActivationSummary> summaries = null;

    try {

      final Query query =
          getCurrentSession().createSQLQuery(QueryHelper.QueryType.ActivationSummary.getQuery()).addScalar("totalCodes", StandardBasicTypes.INTEGER)
              .addScalar("totalActivations", StandardBasicTypes.INTEGER).addScalar("title", StandardBasicTypes.STRING)
              .addScalar("dealOfferId", PostgresUUIDType.INSTANCE);

      query.setParameter("merchantId", merchantId, PostgresUUIDType.INSTANCE);

      query.setResultTransformer(Transformers.aliasToBean(ActivationSummary.class));

      summaries = query.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getActivationSummaries for merchantId %s", merchantId), ex);
    }

    return summaries;

  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteCustomer(final UUID customerId) throws ServiceException {
    try {
      int deleted = 0;
      Query query = getCurrentSession().createQuery("delete from CustomerImpl where id=:customerId").setParameter("customerId", customerId);

      deleted = query.executeUpdate();

      query = getCurrentSession().createQuery("delete from ActivityImpl where customerId=:customerId").setParameter("customerId", customerId);

      deleted = query.executeUpdate();

      query =
          getCurrentSession().createSQLQuery("delete from gift where to_email=(select email from customer where customer_id=:customerId)")
              .setParameter("customerId", customerId, PostgresUUIDType.INSTANCE);

      deleted = query.executeUpdate();
    } catch (Exception e) {
      throw new ServiceException("Problem deleteing customerId " + customerId, e);
    }

  }

  @Override
  public DealOfferGeoSummariesResult getDealOfferGeoSummariesWithin(final Location location, final int maxMiles, final SearchOptions searchOpts,
      final SearchOptions fallbackSearchOpts, final boolean supportsFreeBooks) throws ServiceException {
    final List<DealOfferGeoSummary> summaries = new ArrayList<DealOfferGeoSummary>();
    boolean usingFallback = !isLocationAvailable(location);
    String newSql = null;
    SQLQuery query = null;

    final ResultTransformer resultTransformer = new ResultTransformer() {
      private static final long serialVersionUID = 1L;

      @Override
      public Object transformTuple(Object[] tuple, String[] aliases) {
        final UUID dealOfferId = (UUID) tuple[0];
        final Double distanceInMeters = tuple.length == 2 ? (Double) tuple[1] : null;
        final DealOfferMetadata metadata = DealOfferMetadataCache.get().getDealOfferMetrics(dealOfferId);

        if (metadata == null) {
          LOG.warn(String.format("Metadata cache does not yet contain dealOfferId %s . Skipping", dealOfferId));
          return null;
        }

        final DealOfferGeoSummary geoSummary =
            new DealOfferGeoSummary(metadata.getDealOffer(), distanceInMeters, null, metadata.getDealOfferMetrics().getLongMetrics(), metadata
                .getDealOfferMetrics().getDoubleMetrics());
        summaries.add(geoSummary);
        return null;
      }

      @SuppressWarnings("rawtypes")
      @Override
      public List transformList(List collection) {
        return summaries;
      }
    };

    try {
      if (!usingFallback) {
        final Point point = new Point(location.getLongitude(), location.getLatitude());
        point.setSrid(4326);
        final ImmutableMap<String, Object> params =
            ImmutableMap.<String, Object>builder().put("point", point.toString()).put("distanceInMeters", SpatialUtils.milesToMeters(maxMiles))
                .build();

        newSql =
            QueryHelper.buildQuery(supportsFreeBooks ? QueryType.ActivePaidAndFreeDealOfferIDsWithinMeters : QueryType.ActivePaidDealOfferIDsWithinMeters,
                params, searchOpts);

        query = getSessionFactory().getCurrentSession().createSQLQuery(newSql);
        query.addScalar("dealOfferId", PostgresUUIDType.INSTANCE);
        query.addScalar("distanceInMeters", StandardBasicTypes.DOUBLE);

        query.setResultTransformer(resultTransformer);
        query.list();
      }

      // if no results are found with the spacial query, or we are using the
      // fall back because location is unavailable, then fall back to query
      if (CollectionUtils.isEmpty(summaries) && fallbackSearchOpts != null) {
        usingFallback = true;
        newSql =
            QueryHelper.buildQuery(supportsFreeBooks ? QueryType.ActivePaidAndFreeDealOfferIDs : QueryType.ActivePaidDealOfferIDs, null, fallbackSearchOpts);
        query = getSessionFactory().getCurrentSession().createSQLQuery(newSql);
        query.addScalar("dealOfferId", PostgresUUIDType.INSTANCE);
        query.setResultTransformer(resultTransformer);
        query.list();
      }

    } catch (Exception ex) {
      String msg = String.format("Problem getting merchants within lng/lat %s and maxMiles %d", location, maxMiles);
      LOG.error(msg, ex);
      throw new ServiceException(msg, ex);
    }

    return new DealOfferGeoSummariesResult(summaries, usingFallback);
  }

  protected static boolean isLocationAvailable(final Location location) {
    return (location != null && location.getLongitude() != null && location.getLatitude() != null && (location.getLongitude() != 0.0 && location
        .getLatitude() != 0.0));
  }

  @Override
  public Map<UUID, DealOfferMetrics> getDealOfferMetrics() throws ServiceException {
    final Map<UUID, DealOfferMetrics> dealOfferMetrics = new HashMap<UUID, DealOfferMetrics>();

    final String newSql = QueryHelper.buildQuery(QueryType.DealOfferBasicStats, null, null);

    try {
      final SQLQuery query = getSessionFactory().getCurrentSession().createSQLQuery(newSql);

      query.addScalar("dealOfferId", PostgresUUIDType.INSTANCE);
      query.addScalar("totalMerchants", StandardBasicTypes.LONG);
      query.addScalar("totalDeals", StandardBasicTypes.LONG);

      query.setResultTransformer(new ResultTransformer() {

        private static final long serialVersionUID = 1L;

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
          final UUID dealOfferId = (UUID) tuple[0];
          final DealOfferMetrics metric = new DealOfferMetrics(dealOfferId);
          metric.addLongMetric(DealOfferMetrics.MetricType.TotalMerchants.toString(), (Long) tuple[1]);
          metric.addLongMetric(DealOfferMetrics.MetricType.TotalDeals.toString(), (Long) tuple[2]);
          dealOfferMetrics.put(dealOfferId, metric);
          return null;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public List transformList(List collection) {
          return collection;
        }

      });

      query.list();

    } catch (Exception ex) {
      throw new ServiceException("Problem executing dealOfferMetrics", ex);
    }

    return dealOfferMetrics;

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Merchant> getMerchantsByDealOfferId(final UUID dealOfferId, final SearchOptions searchOpts) throws ServiceException {
    try {
      final String newHql = QueryHelper.buildQuery(QueryType.MerchantsByDealOfferId, null, searchOpts, true);

      final Query query = sessionFactory.getCurrentSession().createQuery(newHql);
      query.setParameter("dealOfferId", dealOfferId);

      QueryHelper.applyOffsetLimit(query, searchOpts);

      return query.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getMerchantsByDealOfferId %s ", dealOfferId), ex);
    }
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public DealOffer deepCopyDealOffer(final UUID dealOfferId) throws ServiceException {
    try {
      final DealOffer dealOffer = getDealOffer(dealOfferId);
      final DealOffer newDealOffer = ((DealOfferImpl) dealOffer).copy();
      newDealOffer.setActive(false);
      newDealOffer.setTitle("copy of '" + dealOffer.getTitle() + "'");

      daoDispatcher.save(newDealOffer);

      final List<Deal> deals = getDealsByDealOfferId(dealOffer.getId(), null, false);
      final Deal[] newDeals = new Deal[deals.size()];
      int idx = 0;

      for (Deal deal : deals) {
        final Deal newDeal = ((DealImpl) deal).copy();
        deal = null;
        newDeal.setDealOffer(newDealOffer);
        newDeals[idx++] = newDeal;
      }

      daoDispatcher.save((Object[]) newDeals);

      return newDealOffer;
    } catch (ServiceException se) {
      throw se;
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Merchant> getMerchantsCreatedByMerchant(final UUID merchantId) throws ServiceException {
    List<Merchant> merchants;

    try {
      final Query query = sessionFactory.getCurrentSession().getNamedQuery("getMerchantsCreatedByMerchant");
      query.setParameter("merchantId", merchantId, PostgresUUIDType.INSTANCE);
      merchants = query.list();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getMerchantsCreatedByMerchant merchantId %s", merchantId), ex);
    }

    return merchants;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<DealOfferSummary> getDealOfferSummary(SearchOptions searchOpts, boolean calculateTotalResults) throws ServiceException {

    PaginatedResult<DealOfferSummary> paginatedResult = null;
    List<DealOfferSummary> summaries = null;
    Long totalResults = null;

    try {

      String newSql = QueryHelper.buildQuery(QueryType.DealOfferSummary, null, searchOpts, true);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(DealOfferSummary.class));
      query.addScalar("offerId", PostgresUUIDType.INSTANCE);
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("title", StandardBasicTypes.STRING);
      query.addScalar("summary", StandardBasicTypes.STRING);
      query.addScalar("properties", StandardBasicTypes.STRING);
      query.addScalar("locationName", StandardBasicTypes.STRING);
      query.addScalar("address1", StandardBasicTypes.STRING);
      query.addScalar("address2", StandardBasicTypes.STRING);
      query.addScalar("city", StandardBasicTypes.STRING);
      query.addScalar("state", StandardBasicTypes.STRING);
      query.addScalar("offerType", StandardBasicTypes.STRING);
      query.addScalar("price", StandardBasicTypes.DOUBLE);
      query.addScalar("isActive", StandardBasicTypes.BOOLEAN);
      query.addScalar("backgroundUrl", StandardBasicTypes.STRING);
      query.addScalar("iconUrl", StandardBasicTypes.STRING);
      query.addScalar("logoUrl", StandardBasicTypes.STRING);
      query.addScalar("merchantName", StandardBasicTypes.STRING);
      query.addScalar("createdByMerchantName", StandardBasicTypes.STRING);
      query.addScalar("scheduledStartDate", StandardBasicTypes.DATE);
      query.addScalar("scheduledEndDate", StandardBasicTypes.DATE);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<DealOfferSummary>) query.list();

      if (calculateTotalResults && summaries != null) {
        totalResults = (Long) getDealOfferSummaryCount();
      }

      // get the metrics out of the cache and put them on the summaries
      DealOfferMetadataCache cache = DealOfferMetadataCache.get();
      for (DealOfferSummary dos : summaries) {
        DealOfferMetadata meta = cache.getDealOfferMetrics(dos.getOfferId());
        if (meta != null) {
          DealOfferMetrics metrics = meta.getDealOfferMetrics();
          if (metrics != null) {
            Map<String, Long> map = metrics.getLongMetrics();
            dos.setMerchantCount(map.get(MetricType.TotalMerchants.toString()));
            dos.setDealCount(map.get(MetricType.TotalDeals.toString()));
            dos.setAcquiresCount(map.get(MetricType.TotalAcquires.toString()));
            dos.setRedemptionCount(map.get(MetricType.TotalRedemptions.toString()));
          }
        }
      }

      paginatedResult = new PaginatedResult<DealOfferSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getDealOfferSummary: " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<DealOfferSummary> getDealOfferSummary(SearchOptions searchOpts, String title, boolean calculateTotalResults)
      throws ServiceException {

    PaginatedResult<DealOfferSummary> paginatedResult = null;
    List<DealOfferSummary> summaries = null;
    Long totalResults = null;
    String cleanTitle = null;

    try {
      String newSql = QueryHelper.buildQuery(QueryType.DealOfferTitleSummary, null, searchOpts, true);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(DealOfferSummary.class));
      query.addScalar("offerId", PostgresUUIDType.INSTANCE);
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("title", StandardBasicTypes.STRING);
      query.addScalar("summary", StandardBasicTypes.STRING);
      query.addScalar("properties", StandardBasicTypes.STRING);
      query.addScalar("locationName", StandardBasicTypes.STRING);
      query.addScalar("address1", StandardBasicTypes.STRING);
      query.addScalar("address2", StandardBasicTypes.STRING);
      query.addScalar("city", StandardBasicTypes.STRING);
      query.addScalar("state", StandardBasicTypes.STRING);
      query.addScalar("offerType", StandardBasicTypes.STRING);
      query.addScalar("price", StandardBasicTypes.DOUBLE);
      query.addScalar("expires", StandardBasicTypes.DATE);
      query.addScalar("isActive", StandardBasicTypes.BOOLEAN);
      query.addScalar("backgroundUrl", StandardBasicTypes.STRING);
      query.addScalar("iconUrl", StandardBasicTypes.STRING);
      query.addScalar("logoUrl", StandardBasicTypes.STRING);
      query.addScalar("merchantName", StandardBasicTypes.STRING);
      query.addScalar("createdByMerchantName", StandardBasicTypes.STRING);
      query.addScalar("scheduledStartDate", StandardBasicTypes.DATE);
      query.addScalar("scheduledEndDate", StandardBasicTypes.DATE);

      cleanTitle = title.replaceAll("[*]", "%");
      query.setParameter("title", cleanTitle);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<DealOfferSummary>) query.list();

      if (calculateTotalResults && summaries != null) {
        totalResults = getDealOfferSummaryCount(title);
      }

      // get the metrics out of the cache and put them on the summaries
      DealOfferMetadataCache cache = DealOfferMetadataCache.get();
      for (DealOfferSummary dos : summaries) {
        DealOfferMetadata meta = cache.getDealOfferMetrics(dos.getOfferId());
        if (meta != null) {
          DealOfferMetrics metrics = meta.getDealOfferMetrics();
          if (metrics != null) {
            Map<String, Long> map = metrics.getLongMetrics();
            dos.setMerchantCount(map.get(MetricType.TotalMerchants.toString()));
            dos.setDealCount(map.get(MetricType.TotalDeals.toString()));
            dos.setAcquiresCount(map.get(MetricType.TotalAcquires.toString()));
            dos.setRedemptionCount(map.get(MetricType.TotalRedemptions.toString()));
          }
        }

      }

      paginatedResult = new PaginatedResult<DealOfferSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealOfferSummary with title %s: %s", title, ex.getMessage()), ex);
    }

    return paginatedResult;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<DealOfferSummary> getPublisherDealOfferSummary(UUID publisherMerchantId, SearchOptions searchOpts, boolean calculateRowSize)
      throws ServiceException {

    PaginatedResult<DealOfferSummary> paginatedResult = null;
    List<DealOfferSummary> summaries = null;
    Long totalResults = null;

    try {
      String newSql = QueryHelper.buildQuery(QueryType.PublisherDealOfferSummary, null, searchOpts, true);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(DealOfferSummary.class));
      query.addScalar("offerId", PostgresUUIDType.INSTANCE);
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("title", StandardBasicTypes.STRING);
      query.addScalar("summary", StandardBasicTypes.STRING);
      query.addScalar("properties", StandardBasicTypes.STRING);
      query.addScalar("locationName", StandardBasicTypes.STRING);
      query.addScalar("address1", StandardBasicTypes.STRING);
      query.addScalar("address2", StandardBasicTypes.STRING);
      query.addScalar("city", StandardBasicTypes.STRING);
      query.addScalar("state", StandardBasicTypes.STRING);
      query.addScalar("offerType", StandardBasicTypes.STRING);
      query.addScalar("price", StandardBasicTypes.DOUBLE);
      query.addScalar("isActive", StandardBasicTypes.BOOLEAN);
      query.addScalar("backgroundUrl", StandardBasicTypes.STRING);
      query.addScalar("iconUrl", StandardBasicTypes.STRING);
      query.addScalar("logoUrl", StandardBasicTypes.STRING);
      query.addScalar("merchantName", StandardBasicTypes.STRING);
      query.addScalar("createdByMerchantName", StandardBasicTypes.STRING);

      query.addScalar("scheduledStartDate", StandardBasicTypes.DATE);
      query.addScalar("scheduledEndDate", StandardBasicTypes.DATE);

      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<DealOfferSummary>) query.list();

      if (calculateRowSize && summaries != null) {
        totalResults = (Long) getPublisherDealOfferSummaryCount(publisherMerchantId);
      }

      // get the metrics out of the cache and put them on the summaries
      DealOfferMetadataCache cache = DealOfferMetadataCache.get();
      for (DealOfferSummary dos : summaries) {
        DealOfferMetadata meta = cache.getDealOfferMetrics(dos.getOfferId());
        if (meta != null) {
          DealOfferMetrics metrics = meta.getDealOfferMetrics();
          if (metrics != null) {
            Map<String, Long> map = metrics.getLongMetrics();
            dos.setMerchantCount(map.get(MetricType.TotalMerchants.toString()));
            dos.setDealCount(map.get(MetricType.TotalDeals.toString()));
            dos.setAcquiresCount(map.get(MetricType.TotalAcquires.toString()));
            dos.setRedemptionCount(map.get(MetricType.TotalRedemptions.toString()));
          }
        }
      }

      paginatedResult = new PaginatedResult<DealOfferSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getPublisherDealOfferSummary: " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<DealOfferSummary> getPublisherDealOfferSummaryByTitle(UUID publisherMerchantId, SearchOptions searchOpts, String title,
      boolean calculateRowSize) throws ServiceException {

    PaginatedResult<DealOfferSummary> paginatedResult = null;
    List<DealOfferSummary> summaries = null;
    Long totalResults = null;
    String cleanTitle = null;

    try {
      String newSql = QueryHelper.buildQuery(QueryType.PublisherDealOfferTitleSummary, null, searchOpts, true);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(DealOfferSummary.class));
      query.addScalar("offerId", PostgresUUIDType.INSTANCE);
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("title", StandardBasicTypes.STRING);
      query.addScalar("summary", StandardBasicTypes.STRING);
      query.addScalar("properties", StandardBasicTypes.STRING);
      query.addScalar("locationName", StandardBasicTypes.STRING);
      query.addScalar("address1", StandardBasicTypes.STRING);
      query.addScalar("address2", StandardBasicTypes.STRING);
      query.addScalar("city", StandardBasicTypes.STRING);
      query.addScalar("state", StandardBasicTypes.STRING);
      query.addScalar("offerType", StandardBasicTypes.STRING);
      query.addScalar("price", StandardBasicTypes.DOUBLE);
      query.addScalar("expires", StandardBasicTypes.DATE);
      query.addScalar("isActive", StandardBasicTypes.BOOLEAN);
      query.addScalar("backgroundUrl", StandardBasicTypes.STRING);
      query.addScalar("iconUrl", StandardBasicTypes.STRING);
      query.addScalar("logoUrl", StandardBasicTypes.STRING);
      query.addScalar("merchantName", StandardBasicTypes.STRING);
      query.addScalar("createdByMerchantName", StandardBasicTypes.STRING);

      query.addScalar("scheduledStartDate", StandardBasicTypes.DATE);
      query.addScalar("scheduledEndDate", StandardBasicTypes.DATE);

      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
      cleanTitle = title.replaceAll("[*]", "%");
      query.setParameter("title", cleanTitle);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<DealOfferSummary>) query.list();

      if (calculateRowSize && summaries != null) {
        totalResults = getPublisherDealOfferSummaryTitleCount(publisherMerchantId, cleanTitle);
      }

      // get the metrics out of the cache and put them on the summaries
      DealOfferMetadataCache cache = DealOfferMetadataCache.get();
      for (DealOfferSummary dos : summaries) {
        DealOfferMetadata meta = cache.getDealOfferMetrics(dos.getOfferId());
        if (meta != null) {
          DealOfferMetrics metrics = meta.getDealOfferMetrics();
          if (metrics != null) {
            Map<String, Long> map = metrics.getLongMetrics();
            dos.setMerchantCount(map.get(MetricType.TotalMerchants.toString()));
            dos.setDealCount(map.get(MetricType.TotalDeals.toString()));
            dos.setAcquiresCount(map.get(MetricType.TotalAcquires.toString()));
            dos.setRedemptionCount(map.get(MetricType.TotalRedemptions.toString()));
          }
        }

      }

      paginatedResult = new PaginatedResult<DealOfferSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getPublisherDealOfferSummaryByTitle: " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @Override
  public long getDealOfferSummaryCount(final String title) throws ServiceException {
    Long total = null;

    try {
      final String newSql = QueryHelper.buildQuery(QueryType.DealOfferTitleSummaryCnt, null, null, true);
      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("title", title.replaceAll("[*]", "%"));
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDealOfferSummaryCount %s : %s", title, ex.getMessage(), ex));
    }

    return total == null ? 0 : total;
  }

  @Override
  public long getPublisherDealOfferSummaryTitleCount(final UUID publisherMerchantId, final String title) throws ServiceException {
    Long total = null;

    try {
      final String newSql = QueryHelper.buildQuery(QueryType.PublisherDealOfferTitleSummaryCnt, null, null, true);
      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
      query.setParameter("title", title.replaceAll("[*]", "%"));
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getPublisherDealOfferSummaryTitleCount: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @Override
  public long getDealOfferSummaryCount() throws ServiceException {
    Long total = null;

    try {
      final String newSql = QueryHelper.buildQuery(QueryType.DealOfferSummaryCnt, null, null, true);
      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getDealOfferSummaryCount: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @Override
  public long getPublisherDealOfferSummaryCount(UUID publisherMerchantId) throws ServiceException {
    Long total = null;

    try {
      final String newSql = QueryHelper.buildQuery(QueryType.PublisherDealOfferSummaryCnt, null, null, true);
      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getPublisherDealOfferSummaryCount: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<MerchantSummary> getMerchantSummary(SearchOptions searchOpts, final PropertyCriteria propertyCriteria,
      boolean calculateTotalResults) throws ServiceException {

    PaginatedResult<MerchantSummary> paginatedResult = null;
    List<MerchantSummary> summaries = null;
    Long totalResults = null;

    try {

      String newSql = QueryHelper.buildPropertyQuery(QueryType.MerchantSummary, propertyCriteria, searchOpts);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(MerchantSummary.class));
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("name", StandardBasicTypes.STRING);
      query.addScalar("address1", StandardBasicTypes.STRING);
      query.addScalar("address2", StandardBasicTypes.STRING);
      query.addScalar("city", StandardBasicTypes.STRING);
      query.addScalar("state", StandardBasicTypes.STRING);
      query.addScalar("zip", StandardBasicTypes.STRING);
      query.addScalar("phone", StandardBasicTypes.STRING);
      query.addScalar("website", StandardBasicTypes.STRING);
      query.addScalar("category", StandardBasicTypes.STRING);
      query.addScalar("logoUrl", StandardBasicTypes.STRING);
      query.addScalar("imageUrl", StandardBasicTypes.STRING);
      query.addScalar("locationCount", StandardBasicTypes.INTEGER);
      query.addScalar("dealCount", StandardBasicTypes.INTEGER);
      query.addScalar("merchantAccountCount", StandardBasicTypes.INTEGER);
      query.addScalar("properties", StandardBasicTypes.STRING);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<MerchantSummary>) query.list();

      if (calculateTotalResults && summaries != null) {
        totalResults = (Long) getMerchantSummaryCount(propertyCriteria);
      }

      paginatedResult = new PaginatedResult<MerchantSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantSummary: " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<MerchantSummary> getMerchantSummary(SearchOptions searchOpts, String name, final PropertyCriteria propertyCriteria,
      boolean calculateTotalResults) throws ServiceException {

    PaginatedResult<MerchantSummary> paginatedResult = null;
    List<MerchantSummary> summaries = null;
    Long totalResults = null;
    String cleanName = null;

    try {

      String newSql = QueryHelper.buildPropertyQuery(QueryType.MerchantNameSummary, propertyCriteria, searchOpts);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(MerchantSummary.class));
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("name", StandardBasicTypes.STRING);
      query.addScalar("address1", StandardBasicTypes.STRING);
      query.addScalar("address2", StandardBasicTypes.STRING);
      query.addScalar("city", StandardBasicTypes.STRING);
      query.addScalar("state", StandardBasicTypes.STRING);
      query.addScalar("zip", StandardBasicTypes.STRING);
      query.addScalar("phone", StandardBasicTypes.STRING);
      query.addScalar("website", StandardBasicTypes.STRING);
      query.addScalar("category", StandardBasicTypes.STRING);
      query.addScalar("logoUrl", StandardBasicTypes.STRING);
      query.addScalar("imageUrl", StandardBasicTypes.STRING);
      query.addScalar("locationCount", StandardBasicTypes.INTEGER);
      query.addScalar("dealCount", StandardBasicTypes.INTEGER);
      query.addScalar("merchantAccountCount", StandardBasicTypes.INTEGER);
      query.addScalar("properties", StandardBasicTypes.STRING);

      cleanName = name.replaceAll("[*]", "%");
      query.setParameter("name", cleanName);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<MerchantSummary>) query.list();

      if (calculateTotalResults && summaries != null) {
        totalResults = (Long) getMerchantSummaryCount(cleanName, propertyCriteria);
      }

      paginatedResult = new PaginatedResult<MerchantSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantSummary: " + name + ": " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<MerchantSummary> getPublisherMerchantSummary(UUID publisherMerchantId, SearchOptions searchOpts,
      final PropertyCriteria propertyCriteria, boolean calculateTotalResults) throws ServiceException {

    PaginatedResult<MerchantSummary> paginatedResult = null;
    List<MerchantSummary> summaries = null;
    Long totalResults = null;

    try {

      String newSql = QueryHelper.buildPropertyQuery(QueryType.PublisherMerchantSummary, propertyCriteria, searchOpts);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(MerchantSummary.class));
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("name", StandardBasicTypes.STRING);
      query.addScalar("address1", StandardBasicTypes.STRING);
      query.addScalar("address2", StandardBasicTypes.STRING);
      query.addScalar("city", StandardBasicTypes.STRING);
      query.addScalar("state", StandardBasicTypes.STRING);
      query.addScalar("zip", StandardBasicTypes.STRING);
      query.addScalar("phone", StandardBasicTypes.STRING);
      query.addScalar("website", StandardBasicTypes.STRING);
      query.addScalar("category", StandardBasicTypes.STRING);
      query.addScalar("logoUrl", StandardBasicTypes.STRING);
      query.addScalar("imageUrl", StandardBasicTypes.STRING);
      query.addScalar("locationCount", StandardBasicTypes.INTEGER);
      query.addScalar("dealCount", StandardBasicTypes.INTEGER);
      query.addScalar("merchantAccountCount", StandardBasicTypes.INTEGER);
      query.addScalar("properties", StandardBasicTypes.STRING);

      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<MerchantSummary>) query.list();

      if (calculateTotalResults && summaries != null) {
        totalResults = (Long) getPublisherMerchantSummaryCount(publisherMerchantId, propertyCriteria);
      }

      paginatedResult = new PaginatedResult<MerchantSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getPublisherMerchantSummary: " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<MerchantSummary> getPublisherMerchantSummaryByName(UUID publisherMerchantId, SearchOptions searchOpts, String name,
      final PropertyCriteria propertyCriteria, boolean calculateRowSize) throws ServiceException {

    PaginatedResult<MerchantSummary> paginatedResult = null;
    List<MerchantSummary> summaries = null;
    Long totalResults = null;
    String cleanName = null;

    try {
      String newSql = QueryHelper.buildPropertyQuery(QueryType.PublisherMerchantNameSummary, propertyCriteria, searchOpts);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(MerchantSummary.class));
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("name", StandardBasicTypes.STRING);
      query.addScalar("address1", StandardBasicTypes.STRING);
      query.addScalar("address2", StandardBasicTypes.STRING);
      query.addScalar("city", StandardBasicTypes.STRING);
      query.addScalar("state", StandardBasicTypes.STRING);
      query.addScalar("zip", StandardBasicTypes.STRING);
      query.addScalar("phone", StandardBasicTypes.STRING);
      query.addScalar("website", StandardBasicTypes.STRING);
      query.addScalar("category", StandardBasicTypes.STRING);
      query.addScalar("logoUrl", StandardBasicTypes.STRING);
      query.addScalar("imageUrl", StandardBasicTypes.STRING);
      query.addScalar("locationCount", StandardBasicTypes.INTEGER);
      query.addScalar("dealCount", StandardBasicTypes.INTEGER);
      query.addScalar("merchantAccountCount", StandardBasicTypes.INTEGER);
      query.addScalar("properties", StandardBasicTypes.STRING);

      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
      cleanName = name.replaceAll("[*]", "%");
      query.setParameter("name", cleanName);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<MerchantSummary>) query.list();

      if (calculateRowSize && summaries != null) {
        totalResults = getPublisherMerchantSummaryNameCount(publisherMerchantId, cleanName, propertyCriteria);
      }

      paginatedResult = new PaginatedResult<MerchantSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getPublisherMerchantSummaryByName: " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @Override
  public long getMerchantSummaryCount(final PropertyCriteria propertyCriteria) throws ServiceException {
    Long total = null;

    try {
      String newSql = QueryHelper.buildPropertyQuery(QueryType.MerchantSummaryCnt, propertyCriteria, null);

      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantSummaryCount: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @Override
  public long getMerchantSummaryCount(String name, final PropertyCriteria propertyCriteria) throws ServiceException {
    Long total = null;

    try {
      String newSql = QueryHelper.buildPropertyQuery(QueryType.MerchantNameSummaryCnt, propertyCriteria, null);
      if (propertyCriteria != null) {
        newSql += propertyCriteria.buildRawFilterClause("m.properties");
      }

      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("name", name.replaceAll("[*]", "%"));
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getMerchantSummaryCount %s : %s", name, ex.getMessage(), ex));
    }

    return total == null ? 0 : total;
  }

  @Override
  public long getPublisherMerchantSummaryCount(UUID publisherMerchantId, final PropertyCriteria propertyCriteria) throws ServiceException {

    Long total = null;

    try {
      String newSql = QueryHelper.buildPropertyQuery(QueryType.PublisherMerchantSummaryCnt, propertyCriteria, null);

      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getPublisherMerchantSummaryCount: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;

  }

  @Override
  public long getPublisherMerchantSummaryNameCount(UUID publisherMerchantId, String name, final PropertyCriteria propertyCriteria)
      throws ServiceException {
    Long total = null;

    try {
      String newSql = QueryHelper.buildPropertyQuery(QueryType.PublisherMerchantNameSummaryCnt, propertyCriteria, null);

      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
      query.setParameter("name", name.replaceAll("[*]", "%"));
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getPublisherMerchantSummaryNameCount: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<DealSummary> getDealSummary(UUID dealOfferId, SearchOptions searchOpts, boolean calculateTotalResults)
      throws ServiceException {
    PaginatedResult<DealSummary> paginatedResult = null;
    List<DealSummary> summaries = null;
    Long totalResults = null;

    try {

      String newSql = QueryHelper.buildQuery(QueryType.DealSummary, null, searchOpts, true);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(DealSummary.class));
      query.addScalar("dealId", PostgresUUIDType.INSTANCE);
      query.addScalar("offerId", PostgresUUIDType.INSTANCE);
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("createdByMerchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("title", StandardBasicTypes.STRING);
      query.addScalar("summary", StandardBasicTypes.STRING);
      query.addScalar("details", StandardBasicTypes.STRING);
      query.addScalar("tags", StandardBasicTypes.STRING);
      query.addScalar("imageUrl", StandardBasicTypes.STRING);
      query.addScalar("merchantName", StandardBasicTypes.STRING);
      query.addScalar("merchantCity", StandardBasicTypes.STRING);
      query.addScalar("merchantState", StandardBasicTypes.STRING);
      query.addScalar("createdByMerchantName", StandardBasicTypes.STRING);
      query.addScalar("offerTitle", StandardBasicTypes.STRING);
      query.addScalar("expires", StandardBasicTypes.DATE);
      query.addScalar("isActive", StandardBasicTypes.BOOLEAN);
      query.addScalar("acquireCount", StandardBasicTypes.INTEGER);
      query.addScalar("redemptionCount", StandardBasicTypes.INTEGER);
      query.addScalar("giftCount", StandardBasicTypes.INTEGER);

      query.setParameter("offerId", dealOfferId, PostgresUUIDType.INSTANCE);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<DealSummary>) query.list();

      if (calculateTotalResults && summaries != null) {
        totalResults = (Long) getDealSummaryCount(dealOfferId);
      }

      paginatedResult = new PaginatedResult<DealSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getDealSummary: " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @Override
  public long getDealSummaryCount(UUID dealOfferId) throws ServiceException {
    Long total = null;

    try {
      final String newSql = QueryHelper.buildQuery(QueryType.DealSummaryCnt, null, null, true);
      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("offerId", dealOfferId, PostgresUUIDType.INSTANCE);
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getDealSummaryCount: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @Override
  public void moveDeals(final List<UUID> dealIds, final UUID dealOfferId, final long merchantAccountId) throws ServiceException {

    try {
      final String queryString = QueryHelper.buildQuery(QueryType.MoveDeals, null, null, true);
      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString);
      query.setParameterList("dealIds", dealIds, PostgresUUIDType.INSTANCE);
      query.setParameter("dealOfferId", dealOfferId, PostgresUUIDType.INSTANCE);
      query.setParameter("merchantAccountId", merchantAccountId, StandardBasicTypes.LONG);
      int updates = query.executeUpdate();

      if (LOG.isDebugEnabled()) {
        LOG.debug("moved " + updates + " deals");
      }

    } catch (Exception ex) {
      throw new ServiceException("Problem moveDeals: " + ex.getMessage(), ex);
    }

  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void setIsCustomerEmailValid(final String email, final boolean isValid) throws ServiceException {
    try {
      final Query query = sessionFactory.getCurrentSession().getNamedQuery("updateCustomerEmailValid");
      query.setParameter("isValid", isValid);
      query.setParameter("email", email);
      query.executeUpdate();
    } catch (Exception ex) {
      throw new ServiceException("Problem updating customer isValidEmail: " + ex.getMessage(), ex);
    }

  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void setIsMerchantEmailValid(final String email, final boolean isValid) throws ServiceException {
    try {
      final Query query = sessionFactory.getCurrentSession().getNamedQuery("updateMerchantEmailValid");
      query.setParameter("isValid", isValid);
      query.setParameter("email", email);
      query.executeUpdate();
    } catch (Exception ex) {
      throw new ServiceException("Problem updating merchantLocation isValidEmail: " + ex.getMessage(), ex);
    }
  }

  @Override
  public <T extends PropertyEntity> List<? extends T> getEntityByProperty(Class<T> type, String propKey, String propVal) throws ServiceException {
    final Map<String, String> props = new HashMap<String, String>();
    props.put(propKey, propVal);
    PropertyCriteria criteria = new PropertyCriteria();
    criteria.setFilters(com.talool.domain.PropertyCriteria.Filter.equal(propKey, propVal));
    return getEntityByProperties(type, criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends PropertyEntity> List<? extends T> getEntityByProperties(final Class<T> type, final PropertyCriteria propertyCriteria)
      throws ServiceException {
    List<T> entityList = null;
    Query query = null;
    String queryStr = null;

    try {
      if (type.equals(DealOffer.class)) {
        queryStr = "from DealOfferImpl as d left join fetch d.merchant as merchant left join fetch merchant.locations where ";
        queryStr += propertyCriteria.buildFilterClause("d.props");
      } else if (type.equals(Merchant.class)) {
        queryStr = "from MerchantImpl as m where ";
        queryStr += propertyCriteria.buildFilterClause("m.props");
      } else if (type.equals(MerchantAccount.class)) {
        queryStr = "from MerchantAccountImpl as ma where ";
        queryStr += propertyCriteria.buildFilterClause("ma.props");
      } else if (type.equals(MerchantLocation.class)) {
        queryStr = "from MerchantLocationImpl as ml where ";
        queryStr += propertyCriteria.buildFilterClause("ml.props");
      } else if (type.equals(DealOfferPurchase.class)) {
        queryStr = "from DealOfferPurchaseImpl as p where ";
        queryStr += propertyCriteria.buildFilterClause("p.props");
      } else if (type.equals(GiftImpl.class)) {
        queryStr = "from GiftImpl as g where ";
        queryStr += propertyCriteria.buildFilterClause("g.props");
      } else if (type.equals(MessagingJobImpl.class)) {
        queryStr = "from MessagingJobImpl as m where ";
        queryStr += propertyCriteria.buildFilterClause("m.props");
      } else {
        throw new ServiceException("Unsupported entity class " + type.getClass().getSimpleName());
      }

      query = getCurrentSession().createQuery(queryStr);
      query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
      entityList = query.list();
    } catch (ServiceException se) {
      throw se;
    } catch (Exception ex) {
      throw new ServiceException(ex.getLocalizedMessage(), ex);
    }

    return entityList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealOffer> getPublisherDealOffers(final UUID publisherId, final PropertyCriteria propertyCriteria) throws ServiceException {
    List<DealOffer> entityList = null;
    Query query = null;
    String queryStr = null;

    try {
      queryStr =
          "from DealOfferImpl as d left join fetch d.merchant as merchant left join fetch merchant.locations where merchant.id=:merchantId and ";
      queryStr += propertyCriteria.buildFilterClause("d.props");

      query = getCurrentSession().createQuery(queryStr);
      query.setParameter("merchantId", publisherId, PostgresUUIDType.INSTANCE);
      query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
      entityList = query.list();
    } catch (Exception ex) {
      throw new ServiceException(ex.getLocalizedMessage(), ex);
    }

    return entityList;

  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public MerchantCodeGroup createMerchantCodeGroup(final Merchant merchant, final Long createdByMerchantAccountId, final UUID publisherId,
      final String codeGroupTitle, final String codeGroupNotes, final short totalCodes) throws ServiceException {
    MerchantCodeGroupImpl mcg = null;

    try {
      mcg = new MerchantCodeGroupImpl(merchant);
      mcg.setCreatedBymerchantAccountId(createdByMerchantAccountId);
      mcg.setPublisherId(publisherId);
      mcg.setCodeGroupNotes(codeGroupNotes);
      mcg.setCodeGroupTitle(codeGroupTitle);
      mcg.setTotalCodes(totalCodes);
      mcg.setCreated(Calendar.getInstance().getTime());

      MerchantCodeImpl mcode = null;

      for (int i = 0; i < totalCodes; i++) {
        mcode = new MerchantCodeImpl();
        mcode.setCode(activiationCodeStrategy.generateCode());
        mcode.setMerchantCodeGroup(mcg);
        mcg.getCodes().add(mcode);
      }

      daoDispatcher.save(mcg);
      getCurrentSession().flush();

      // no need to refresh objects, we have what we want.

    } catch (Exception ex) {
      throw new ServiceException(ex.getLocalizedMessage(), ex);
    }

    return mcg;

  }

  @Override
  public boolean isMerchantCodeValid(final String code, final UUID dealOfferId) throws ServiceException {
    boolean isValid = false;

    try {
      SQLQuery query =
          getCurrentSession().createSQLQuery(
              "select mc.merchant_code_id from deal_offer as dof,merchant_code_group as mcg,merchant_code as mc where dof.deal_offer_id=:dealOfferId and "
                  + "dof.merchant_id=mcg.publisher_id and mc.merchant_code_group_id=mcg.merchant_code_group_id and mc.code=:code");

      query.setParameter("dealOfferId", dealOfferId, PostgresUUIDType.INSTANCE);
      query.setParameter("code", code.toUpperCase());

      Object codeId = query.uniqueResult();
      isValid = codeId != null ? true : false;

      TaloolStatsDClient.get().count(Action.validate_code, SubAction.merchant_code, dealOfferId, requestHeaders.get());

    } catch (Exception ex) {
      throw new ServiceException(ex.getLocalizedMessage(), ex);
    }

    return isValid;

  }

  @Override
  public Merchant getFundraiserByTrackingCode(final String code) throws ServiceException {
    if (code == null) {
      return null;
    }

    Merchant fundraiser = null;

    try {

      Query query =
          getCurrentSession()
              .createQuery(
                  "select mcg.merchant from MerchantCodeGroupImpl as mcg,MerchantCodeImpl as mc "
                      + "WHERE mc.merchantCodeGroup=mcg.id AND mc.code=:code");

      query.setParameter("code", code);
      query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
      fundraiser = (Merchant) query.uniqueResult();

    } catch (Exception ex) {
      throw new ServiceException(ex.getLocalizedMessage(), ex);
    }
    return fundraiser;
  }

  @Override
  public MerchantMedia getMerchantMediaById(UUID mediaId) throws ServiceException {
    try {
      return daoDispatcher.find(MerchantMediaImpl.class, mediaId);
    } catch (Exception e) {
      throw new ServiceException("There was a problem in getMerchantMediaById " + mediaId, e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<MerchantLocation> getMerchantLocationsUsingMedia(UUID mediaId, MediaType mediaType) throws ServiceException {
    List<MerchantLocation> locations = null;

    try {
      final Search search = new Search(MerchantLocationImpl.class);
      if (mediaType.equals(MediaType.MERCHANT_IMAGE)) {
        search.addFilterEqual("merchantImage.id", mediaId);
      } else if (mediaType.equals(MediaType.MERCHANT_LOGO)) {
        search.addFilterEqual("logo.id", mediaId);
      } else {
        return new ArrayList<MerchantLocation>();
      }

      locations = daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantLocationsUsingMedia", ex);
    }

    return locations;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealOffer> getDealOffersUsingMedia(UUID mediaId, MediaType mediaType) throws ServiceException {
    List<DealOffer> offers = null;

    try {
      final Search search = new Search(DealOfferImpl.class);
      if (mediaType.equals(MediaType.DEAL_OFFER_LOGO)) {
        search.addFilterEqual("dealOfferLogo.id", mediaId);
      } else if (mediaType.equals(MediaType.DEAL_OFFER_BACKGROUND_IMAGE)) {
        search.addFilterEqual("dealOfferBackground.id", mediaId);
      } else if (mediaType.equals(MediaType.DEAL_OFFER_MERCHANT_LOGO)) {
        search.addFilterEqual("dealOfferIcon.id", mediaId);
      } else {
        return new ArrayList<DealOffer>();
      }
      offers = daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException("Problem getDealOffersUsingMedia", ex);
    }

    return offers;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Deal> getDealsUsingMedia(UUID mediaId) throws ServiceException {
    List<Deal> deals = null;

    try {
      final Search search = new Search(DealImpl.class);
      search.addFilterEqual("image.id", mediaId);
      deals = daoDispatcher.search(search);
    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantLocationsUsingMedia", ex);
    }

    return deals;
  }

  @Override
  public void deleteMerchantLocation(final Long id) throws ServiceException {
    try {
      final Query query = getCurrentSession().createQuery("delete from MerchantLocationImpl where id=:locId").setParameter("locId", id);

      query.executeUpdate();
    } catch (Exception e) {
      throw new ServiceException("Problem deleteing locationId " + id, e);
    }
  }

  @Override
  public void deleteMerchantMedia(UUID mediaId) throws ServiceException {
    // Leave media on the server, but delete the record
    try {
      final Query query = getCurrentSession().createQuery("delete from MerchantMediaImpl where id=:mediaId").setParameter("mediaId", mediaId);

      query.executeUpdate();
    } catch (Exception e) {
      throw new ServiceException("Problem deleteing mediaId " + mediaId, e);
    }
  }

  @Override
  public void replaceMerchantMedia(UUID mediaId, UUID replacementMediaId, MediaType mediaType) throws ServiceException {
    List<SQLQuery> updates = new ArrayList<SQLQuery>();
    if (mediaType.equals(MediaType.DEAL_IMAGE) || mediaType.equals(MediaType.MERCHANT_IMAGE)) {
      updates.add(getCurrentSession().createSQLQuery("UPDATE merchant_location SET merchant_image_id=:replaceId WHERE merchant_image_id=:mediaId"));
      updates.add(getCurrentSession().createSQLQuery("UPDATE deal SET image_id=:replaceId WHERE image_id=:mediaId"));
    } else if (mediaType.equals(MediaType.DEAL_OFFER_LOGO) || mediaType.equals(MediaType.MERCHANT_LOGO)) {
      updates.add(getCurrentSession().createSQLQuery("UPDATE merchant_location SET logo_url_id=:replaceId WHERE logo_url_id=:mediaId"));
      updates.add(getCurrentSession().createSQLQuery("UPDATE deal_offer SET deal_offer_logo_id=:replaceId WHERE deal_offer_logo_id=:mediaId"));
    } else if (mediaType.equals(MediaType.DEAL_OFFER_BACKGROUND_IMAGE)) {
      updates.add(getCurrentSession().createSQLQuery(
          "UPDATE deal_offer SET deal_offer_background_id =:replaceId WHERE deal_offer_background_id =:mediaId"));
    } else if (mediaType.equals(MediaType.DEAL_OFFER_MERCHANT_LOGO)) {
      updates.add(getCurrentSession().createSQLQuery("UPDATE deal_offer SET deal_offer_icon_id =:replaceId WHERE deal_offer_icon_id =:mediaId"));
    }

    try {
      for (SQLQuery update : updates) {
        update.setParameter("mediaId", mediaId, PostgresUUIDType.INSTANCE);
        update.setParameter("replaceId", replacementMediaId, PostgresUUIDType.INSTANCE);
        update.executeUpdate();
      }
    } catch (Exception e) {
      throw new ServiceException("Problem replacing mediaId " + mediaId, e);
    }

  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public <T extends PropertyEntity> void saveProperties(final T entity, final Properties properties) throws ServiceException {
    Query query = null;

    try {
      if (entity instanceof DealOffer) {
        query = sessionFactory.getCurrentSession().createQuery("update DealOfferImpl set props=:properties where id=:id");
        query.setParameter("id", ((DealOffer) entity).getId(), PostgresUUIDType.INSTANCE);
      } else if (entity instanceof Merchant) {
        query = sessionFactory.getCurrentSession()
        // JUST PLAYING WITH VERSIONING - version is not ready yet
            .createQuery("update MerchantImpl set props=:properties where id=:id");
        query.setParameter("id", ((Merchant) entity).getId(), PostgresUUIDType.INSTANCE);
        // query.setParameter("version", ((MerchantImpl) entity).getVersion());

      } else if (entity instanceof MerchantLocation) {
        query = sessionFactory.getCurrentSession().createQuery("update MerchantLocationImpl set props=:properties where id=:id");
        query.setParameter("id", ((MerchantLocation) entity).getId());
      } else if (entity instanceof MerchantAccount) {
        query = sessionFactory.getCurrentSession().createQuery("update MerchantAccountImpl set props=:properties where id=:id");
        query.setParameter("id", ((MerchantAccount) entity).getId());
      } else if (entity instanceof DealOfferPurchase) {
        query = sessionFactory.getCurrentSession().createQuery("update DealOfferPurchaseImpl set props=:properties where id=:id");
        query.setParameter("id", ((MerchantAccount) entity).getId());
      }

      query.setParameter("properties", properties.getAllProperties(), HstoreUserType.TYPE);

      query.executeUpdate();
    } catch (Exception ex) {
      throw new ServiceException("Problem getUniqueProperyKeys for " + entity, ex);
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<String> getUniqueProperyKeys(Class<? extends PropertyEntity> entity) throws ServiceException {
    SQLQuery query = null;

    try {
      if (entity == DealOffer.class) {
        query = sessionFactory.getCurrentSession().createSQLQuery("select distinct skeys( properties ) as keys from deal_offer");
      } else if (entity == Merchant.class) {
        query = sessionFactory.getCurrentSession().createSQLQuery("select distinct skeys( properties ) as keys from merchant");
      } else if (entity == MerchantAccount.class) {
        query = sessionFactory.getCurrentSession().createSQLQuery("select distinct skeys( properties ) as keys from merchant_account");
      } else if (entity == MerchantLocation.class) {
        query = sessionFactory.getCurrentSession().createSQLQuery("select distinct skeys( properties ) as keys from merchant_location");
      } else if (entity == DealOfferPurchase.class) {
        query = sessionFactory.getCurrentSession().createSQLQuery("select distinct skeys( properties ) as keys from deal_offer_purchase");
      }

      return query.list();
    } catch (Exception ex) {
      throw new ServiceException("Problem getUniqueProperyKeys for " + entity, ex);
    }
  }

  @Override
  public void setRequestHeaders(final Map<String, String> headers) {
    requestHeaders.set(headers);

  }

  @Override
  public DealOfferPurchase getDealOfferPurchase(UUID dealOfferPurchaseId) throws ServiceException {
    DealOfferPurchase dop;
    try {
      dop = daoDispatcher.find(DealOfferPurchaseImpl.class, dealOfferPurchaseId);
    } catch (Exception ex) {
      throw new ServiceException("Problem getDealOfferPurchase  " + dealOfferPurchaseId, ex);
    }

    return dop;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void processBraintreeNotification(final String btSignatureParam, final String btPayloadParam) throws ServiceException {
    if (StringUtils.isEmpty(btSignatureParam) || StringUtils.isEmpty(btPayloadParam)) {
      throw new ServiceException(ErrorCode.BRAINTREE_INVALID_WEBHOOK_PARAMS);
    }

    try {
      final WebhookNotification webhookNotification = BraintreeUtil.get().parseWebhookNotification(btSignatureParam, btPayloadParam);

      final Status status = webhookNotification.getMerchantAccount().getStatus();
      List<? extends Merchant> merchants = null;

      // we need to sleep a bit of the merchant is not found - rare case of
      // Braintree faster than our own updates (seen in development environment)
      for (int i = 0; i < 3; i++) {
        merchants = getEntityByProperty(Merchant.class, KeyValue.braintreeSubmerchantId, webhookNotification.getMerchantAccount().getId());

        if (CollectionUtils.isNotEmpty(merchants)) {
          break;
        } else {
          try {
            Thread.sleep(1000);
          } catch (Exception ex) {
          }

        }
      }

      if (CollectionUtils.isEmpty(merchants)) {
        throw new ServiceException(ErrorCode.BRAINTREE_SUBMERCHANT_ID_NOT_FOUND, webhookNotification.getMerchantAccount().getId());
      }

      final Merchant merchant = merchants.get(0);

      merchant.getProperties().createOrReplace(KeyValue.braintreeSubmerchantStatus, status.toString());
      merchant.getProperties().createOrReplace(KeyValue.braintreeSubmerchantStatusTimestamp, webhookNotification.getTimestamp().getTime().getTime());

      switch (webhookNotification.getKind()) {
        case SUB_MERCHANT_ACCOUNT_APPROVED:
          if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Braintree submerchant '%s' approved with status '%s'", merchant.getName(), status.toString()));
          }
          save(merchant);
          break;

        case SUB_MERCHANT_ACCOUNT_DECLINED:
          if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Braintree submerchant '%s' declined with status '%s'", merchant.getName(), status.toString()));
          }

          final StringBuilder sb = new StringBuilder();
          final ValidationErrors errors = webhookNotification.getErrors();
          for (ValidationError error : errors.getAllDeepValidationErrors()) {
            sb.append(error.getMessage()).append(";");
          }
          merchant.getProperties().createOrReplace(KeyValue.braintreeSubmerchantStatusMessage, sb.toString());
          save(merchant);
          break;

        default:

          LOG.warn("Unsupported Braintree Webhook: " + webhookNotification.getKind());
      }

    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new ServiceException(e);
    }

  }

  @Override
  public long getDailyTrackingCodeCountByPublisher(UUID publisherId) throws ServiceException {
    Long total = null;

    try {
      final String newSql = QueryHelper.buildQuery(QueryType.PublisherDailyCodeCnt, null, null, true);
      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("publisherId", publisherId, PostgresUUIDType.INSTANCE);
      query.addScalar("codeCount", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException(String.format("Problem getDailyTrackingCodeCountByPublisher %s : %s", publisherId.toString(), ex.getMessage(), ex));
    }

    return total == null ? 0 : total;
  }

  @Override
  public List<DealOfferPurchase> getDealOfferPurchasesByMerchantId(UUID fundraiserId) throws ServiceException {

    List<DealOfferPurchase> purchases = null;

    // TODO
    // fundraiser's codes: merchant_code_groups where merchant_id = fundraiserId
    // fundraiser's purchases: purchase where one of those codes in the merchant code property

    return purchases;

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DealOfferPurchase> getDealOfferPurchasesByTrackingCode(String code) throws ServiceException {

    List<DealOfferPurchase> purchases = null;

    try {
      purchases = (List<DealOfferPurchase>) getEntityByProperty(DealOfferPurchase.class, KeyValue.merchantCode, code);
    } catch (Exception ex) {
      throw new ServiceException("Problem getDealOfferPurchasesByTrackingCode  " + code, ex);
    }

    return purchases;
  }

  @Override
  public MerchantCodeGroup getMerchantCodeGroupForCode(String code) throws ServiceException {

    if (code == null) {
      return null;
    }

    MerchantCodeGroup group = null;

    try {

      Query query =
          getCurrentSession().createQuery(
              "select mcg from MerchantCodeGroupImpl as mcg,MerchantCodeImpl as mc " + "WHERE mc.merchantCodeGroup=mcg.id AND mc.code=:code");

      query.setParameter("code", code);
      query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
      group = (MerchantCodeGroup) query.uniqueResult();

    } catch (Exception ex) {
      throw new ServiceException(ex.getLocalizedMessage(), ex);
    }
    return group;

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.talool.core.service.MerchantService#getCustomerForMerchant(com.talool.core.Merchant)
   * 
   * Gets (or creates) a "dummy" customer for a merchant for using in sending gifts to customers in
   * the app. The dummy customer id is stored in a property on the merchant after it has been
   * created.
   */
  @Override
  public Customer getCustomerForMerchant(Merchant merchant) throws ServiceException {

    Customer dummy = null;

    String id = merchant.getProperties().getAsString(KeyValue.merchantCustomerId);
    if (StringUtils.isEmpty(id)) {
      try {
        // create a customer
        dummy = new CustomerImpl();
        dummy.setFirstName(merchant.getName());
        dummy.setLastName("");

        StringBuilder sb = new StringBuilder();
        sb.append("dummy").append((new Date()).getTime());
        dummy.setPassword(sb.toString());
        sb.append("@talool.com");
        dummy.setEmail(sb.toString());

        dummy.setSex(Sex.Unknown);

        daoDispatcher.save(dummy);

        id = dummy.getId().toString();

        merchant.getProperties().createOrReplace(KeyValue.merchantCustomerId, id);
        merge(merchant);
        // daoDispatcher.save(merchant);
      } catch (Exception e) {
        throw new ServiceException("Failed to create customer for merchant with merchant: " + merchant.getName(), e);
      }
    } else {
      // look up the customer
      try {
        UUID customerId = UUID.fromString(id);
        dummy = daoDispatcher.find(CustomerImpl.class, customerId);
      } catch (Exception e) {
        throw new ServiceException("Failed to find customer for merchant with customer id: " + id, e);
      }
    }
    return dummy;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<MerchantCodeSummary> getMerchantCodeSummariesForFundraiser(final UUID merchantId, final SearchOptions searchOpts,
      final boolean calculateTotalResults) throws ServiceException {

    PaginatedResult<MerchantCodeSummary> paginatedResult = null;
    List<MerchantCodeSummary> codes = null;
    Long totalResults = null;

    try {
      String newSql = QueryHelper.buildQuery(QueryType.MerchantCodeSummary, null, searchOpts, true);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(MerchantCodeSummary.class));
      query.addScalar("code", StandardBasicTypes.STRING);
      query.addScalar("name", StandardBasicTypes.STRING);
      query.addScalar("email", StandardBasicTypes.STRING);
      query.addScalar("purchaseCount", StandardBasicTypes.INTEGER);

      query.setParameter("merchantId", merchantId, PostgresUUIDType.INSTANCE);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      codes = (List<MerchantCodeSummary>) query.list();

      if (calculateTotalResults && codes != null) {
        totalResults = (Long) getMerchantCodeSummaryCount(merchantId);
      }

      paginatedResult = new PaginatedResult<MerchantCodeSummary>(searchOpts, totalResults, codes);

    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantCodeGroupsForFundraiser  " + merchantId.toString(), ex);
    }
    return paginatedResult;
  }

  @Override
  public long getMerchantCodeSummaryCount(final UUID merchantId) throws ServiceException {
    Long total = null;

    try {
      String newSql = QueryHelper.buildQuery(QueryType.MerchantCodeSummaryCnt, null, null);

      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("merchantId", merchantId, PostgresUUIDType.INSTANCE);
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getMerchantCodeSummaryCount: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void save(final List<DealAcquire> dealAcquires) throws ServiceException {
    try {
      daoDispatcher.save(dealAcquires);
    } catch (Exception e) {
      throw new ServiceException("There was a problem saving dealAcquires", e);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void save(final DealAcquire dealAcquire) throws ServiceException {
    try {
      daoDispatcher.save(dealAcquire);
    } catch (Exception e) {
      final String err = "There was a problem saving DealAcquire";
      throw new ServiceException(err, e);
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<FundraiserSummary> getPublisherFundraiserSummaries(UUID publisherMerchantId, SearchOptions searchOpts,
      boolean calculateRowSize) throws ServiceException {
    PaginatedResult<FundraiserSummary> paginatedResult = null;
    List<FundraiserSummary> summaries = null;
    Long totalResults = null;

    PropertyCriteria criteria = new PropertyCriteria();
    criteria.setFilters(com.talool.domain.PropertyCriteria.Filter.equal(KeyValue.fundraiser, true));

    try {

      String newSql = QueryHelper.buildPropertyQuery(QueryType.PublisherFundraiserSummary, criteria, searchOpts);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(FundraiserSummary.class));
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("name", StandardBasicTypes.STRING);
      query.addScalar("dealOffersSoldCount", StandardBasicTypes.INTEGER);
      query.addScalar("merchantCodeCount", StandardBasicTypes.INTEGER);
      query.addScalar("properties", StandardBasicTypes.STRING);

      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<FundraiserSummary>) query.list();

      if (summaries != null) {
        totalResults = (Long) getPublisherFundraiserSummaryCount(publisherMerchantId);
      }

      paginatedResult = new PaginatedResult<FundraiserSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getPublisherFundraiserSummary: " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @Override
  public long getPublisherFundraiserSummaryCount(UUID publisherMerchantId) throws ServiceException {
    Long total = null;

    PropertyCriteria criteria = new PropertyCriteria();
    criteria.setFilters(com.talool.domain.PropertyCriteria.Filter.equal(KeyValue.fundraiser, true));

    try {
      String newSql = QueryHelper.buildPropertyQuery(QueryType.PublisherMerchantSummaryCnt, criteria, null);

      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getPublisherFundraiserSummaryCount: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PaginatedResult<FundraiserSummary> getFundraiserSummaries(SearchOptions searchOpts, boolean calculateRowSize) throws ServiceException {
    PaginatedResult<FundraiserSummary> paginatedResult = null;
    List<FundraiserSummary> summaries = null;
    Long totalResults = null;

    PropertyCriteria criteria = new PropertyCriteria();
    criteria.setFilters(com.talool.domain.PropertyCriteria.Filter.equal(KeyValue.fundraiser, true));

    try {

      String newSql = QueryHelper.buildPropertyQuery(QueryType.FundraiserSummary, criteria, searchOpts);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(FundraiserSummary.class));
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("name", StandardBasicTypes.STRING);
      query.addScalar("dealOffersSoldCount", StandardBasicTypes.INTEGER);
      query.addScalar("merchantCodeCount", StandardBasicTypes.INTEGER);
      query.addScalar("properties", StandardBasicTypes.STRING);

      QueryHelper.applyOffsetLimit(query, searchOpts);
      summaries = (List<FundraiserSummary>) query.list();

      if (summaries != null) {
        totalResults = (Long) getFundraiserSummaryCount();
      }

      paginatedResult = new PaginatedResult<FundraiserSummary>(searchOpts, totalResults, summaries);
    } catch (Exception ex) {
      throw new ServiceException("Problem getPublisherFundraiserSummary: " + ex.getMessage(), ex);
    }

    return paginatedResult;
  }

  @Override
  public long getFundraiserSummaryCount() throws ServiceException {
    Long total = null;

    PropertyCriteria criteria = new PropertyCriteria();
    criteria.setFilters(com.talool.domain.PropertyCriteria.Filter.equal(KeyValue.fundraiser, true));

    try {
      String newSql = QueryHelper.buildPropertyQuery(QueryType.MerchantSummaryCnt, criteria, null);

      final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.addScalar("totalResults", StandardBasicTypes.LONG);
      total = (Long) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getFundraiserSummaryCount: " + ex.getMessage(), ex);
    }

    return total == null ? 0 : total;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public RefundResult refundOrVoid(final DealOfferPurchase dealOfferPurchase, boolean removeDealAcquires) throws ServiceException {
    final String transactionId = dealOfferPurchase.getProcessorTransactionId();
    RefundType refundType = null;

    try {
      final RefundVoidResult result = BraintreeUtil.get().refundOrVoid(transactionId);
      final Long now = Calendar.getInstance().getTime().getTime();
      if (result.getTransactionResult().isSuccess()) {
        refundType = result.getRefundType();
        switch (refundType) {
          case Refund:
            dealOfferPurchase.getProperties().createOrReplace(KeyValue.processorRefundDate, now);
            break;
          case Void:
            dealOfferPurchase.getProperties().createOrReplace(KeyValue.processorVoidDate, now);
            break;
        }

        save(dealOfferPurchase);

        // TODO - Be careful! This deletes all purchases of a book rather than a single book (if the
        // customer purcahsed
        // multiple books)
        int totalDeleted = 0;
        if (removeDealAcquires) {
          final SQLQuery query =
              getCurrentSession()
                  .createSQLQuery(
                      "delete from deal_acquire where customer_id=:customerId and deal_id in (select deal_id from deal where deal_offer_id=:dealOfferId)");

          query.setParameter("customerId", dealOfferPurchase.getCustomer().getId(), PostgresUUIDType.INSTANCE);
          query.setParameter("dealOfferId", dealOfferPurchase.getDealOffer().getId(), PostgresUUIDType.INSTANCE);

          totalDeleted = query.executeUpdate();
        }

        return new RefundResult(refundType, totalDeleted);
      }

      throw new ServiceException("Refund/Void was not successful: " + result.getTransactionResult().getErrorText());

    } catch (ProcessorException e) {
      throw new ServiceException("problem refund/void transactionId: " + transactionId, e);
    }

  }

  @Override
  public void deleteDealSafely(UUID dealId, Long merchantAccountId) throws ServiceException {
    final String kirkeTitle = "Talool Tools Admin Book";
    final DealSummary summary = getDealSummary(dealId);
    Deal deal = getDeal(dealId);
    if (summary.getDealCount() == 0) {
      deleteDeal(deal.getId());
    } else if (deal.getDealOffer().getType() == DealType.KIRKE_BOOK) {
      // throw exception if the count is > 0 and the deal is already in a Kirke book
      throw new ServiceException(ErrorCode.DEAL_CAN_NOT_BE_DELETED, "This deal is in a " + kirkeTitle + " and can not be deleted.");
    } else {
      // Get a Kirke Book
      final String originalBookTitle = deal.getDealOffer().getTitle();
      DealOffer kirkeBook = null;
      Merchant publisher = deal.getDealOffer().getMerchant();
      List<DealOffer> offers = getDealOffersByMerchantId(publisher.getId());
      for (DealOffer offer : offers) {
        if (offer.getType() == DealType.KIRKE_BOOK) {
          kirkeBook = offer;
          break;
        }
      }
      if (kirkeBook == null) {
        // create a Kirke Book
        final MerchantAccount createdByMerchant = getMerchantAccountById(merchantAccountId);
        kirkeBook = new DealOfferImpl(publisher, createdByMerchant);
        kirkeBook.setUpdatedByMerchantAccount(createdByMerchant);
        kirkeBook.setTitle(kirkeTitle);
        kirkeBook.setActive(false);
        kirkeBook.setDealType(DealType.KIRKE_BOOK);
        kirkeBook.setPrice(1000.0f);
        kirkeBook.setScheduledEndDate(new Date());
        kirkeBook.setScheduledStartDate(new Date());
        kirkeBook
            .setSummary("This book was created by Talool Tools.  You can't sell this book, but you can move its deals into other books you'd like to sell.");
        save(kirkeBook);
      }

      // Move it so it can't be sold
      deal.setDealOffer(kirkeBook);

      // Expire it so it can't be used
      // TODO This will lead to unhappy customers. Do we really want to do this?
      Calendar c = Calendar.getInstance();
      c.roll(Calendar.DAY_OF_YEAR, -62); // about 2 months ago, so app logic will cause it to be
                                         // hidden
      deal.setExpires(c.getTime());
      deal.setActive(false);

      // save it
      merge(deal);
      daoDispatcher.flush(DealImpl.class);
      daoDispatcher.refresh((DealImpl) deal);

      // throw exception so we know the deal was moved rather than deleted.
      StringBuilder sb = new StringBuilder("This deal was moved to your ");
      sb.append(kirkeTitle).append(" so it will no longer be for sale in ").append(originalBookTitle)
          .append(".  The expiration date of this deal was also changed ")
          .append("so it can not be used by customers who acquired the deal previously.");
      throw new ServiceException(ErrorCode.DEAL_MOVED_NOT_DELETED, sb.toString());
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  private void deleteDeal(final UUID id) throws ServiceException {
    try {
      Query query =
          getCurrentSession().createSQLQuery("delete from deal_tag where deal_id=:dealId").setParameter("dealId", id, PostgresUUIDType.INSTANCE);
      query.executeUpdate();

      query = getCurrentSession().createQuery("delete from DealImpl where id=:dealId").setParameter("dealId", id);
      query.executeUpdate();
    } catch (Exception e) {
      throw new ServiceException("Problem deleteing deal id " + id, e);
    }
  }

  @Override
  public DealSummary getDealSummary(UUID dealId) throws ServiceException {
    DealSummary summary = null;
    try {

      String newSql = QueryHelper.buildQuery(QueryType.DealSummaryByDealId, null, null, true);

      SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
      query.setResultTransformer(Transformers.aliasToBean(DealSummary.class));
      query.addScalar("dealId", PostgresUUIDType.INSTANCE);
      query.addScalar("offerId", PostgresUUIDType.INSTANCE);
      query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("createdByMerchantId", PostgresUUIDType.INSTANCE);
      query.addScalar("title", StandardBasicTypes.STRING);
      query.addScalar("summary", StandardBasicTypes.STRING);
      query.addScalar("details", StandardBasicTypes.STRING);
      query.addScalar("tags", StandardBasicTypes.STRING);
      query.addScalar("imageUrl", StandardBasicTypes.STRING);
      query.addScalar("merchantName", StandardBasicTypes.STRING);
      query.addScalar("merchantCity", StandardBasicTypes.STRING);
      query.addScalar("merchantState", StandardBasicTypes.STRING);
      query.addScalar("createdByMerchantName", StandardBasicTypes.STRING);
      query.addScalar("offerTitle", StandardBasicTypes.STRING);
      query.addScalar("expires", StandardBasicTypes.DATE);
      query.addScalar("isActive", StandardBasicTypes.BOOLEAN);
      query.addScalar("acquireCount", StandardBasicTypes.INTEGER);
      query.addScalar("redemptionCount", StandardBasicTypes.INTEGER);
      query.addScalar("giftCount", StandardBasicTypes.INTEGER);

      query.setParameter("dealId", dealId, PostgresUUIDType.INSTANCE);

      summary = (DealSummary) query.uniqueResult();
    } catch (Exception ex) {
      throw new ServiceException("Problem getDealSummary: " + ex.getMessage(), ex);
    }

    return summary;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<UUID> getDealOfferPurchaseIds(final UUID customerId, final UUID dealOfferId, final PropertyCriteria propertyCriteria)
      throws ServiceException {
    List<UUID> dealOfferPurchaseIds = null;
    String queryStr = null;

    try {
      final boolean hasFilters = propertyCriteria != null && propertyCriteria.hasFilters();
      if (hasFilters) {
        queryStr =
            "select id from DealOfferPurchaseImpl where customer.id=:customerId and dealOffer.id=:dealOfferId and "
                + propertyCriteria.buildFilterClause("props");
      } else {
        queryStr = "select id from DealOfferPurchaseImpl where customer.id=:customerId and dealOffer.id=:dealOfferId";
      }

      final Query query = getCurrentSession().createQuery(queryStr);
      query.setParameter("customerId", customerId);
      query.setParameter("dealOfferId", dealOfferId);
      dealOfferPurchaseIds = query.list();
    } catch (Exception ex) {
      throw new ServiceException("Problem getDealOfferPurchase", ex);
    }
    return dealOfferPurchaseIds;
  }
}
