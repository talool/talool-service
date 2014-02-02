package com.talool.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
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
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealAcquireHistory;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferGeoSummariesResult;
import com.talool.core.DealOfferGeoSummary;
import com.talool.core.DealOfferPurchase;
import com.talool.core.FactoryManager;
import com.talool.core.Location;
import com.talool.core.MediaType;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantIdentity;
import com.talool.core.MerchantLocation;
import com.talool.core.MerchantMedia;
import com.talool.core.SearchOptions;
import com.talool.core.Tag;
import com.talool.core.purchase.UniqueCodeStrategy;
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
import com.talool.domain.MerchantIdentityImpl;
import com.talool.domain.MerchantImpl;
import com.talool.domain.MerchantLocationImpl;
import com.talool.domain.TagImpl;
import com.talool.domain.social.SocialNetworkImpl;
import com.talool.persistence.QueryHelper;
import com.talool.persistence.QueryHelper.QueryType;
import com.talool.purchase.DealUniqueConfirmationCodeStrategyImpl;
import com.talool.stats.DealOfferMetadata;
import com.talool.stats.DealOfferMetrics;
import com.talool.utils.SpatialUtils;
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
public class TaloolServiceImpl extends AbstractHibernateService implements TaloolService
{
	private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceImpl.class);
	public static final float MILES_TO_METERS = 1609.34f;

	private UniqueCodeStrategy activiationCodeStrategy = new DealUniqueConfirmationCodeStrategyImpl(7);

	public TaloolServiceImpl()
	{}

	@Override
	public SocialNetwork getSocialNetwork(final SocialNetwork.NetworkName name) throws ServiceException
	{
		SocialNetwork snet;
		try
		{
			final Search search = new Search(SocialNetworkImpl.class);
			search.addFilterEqual("name", name.toString());
			snet = (SocialNetwork) daoDispatcher.searchUnique(search);

		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getSocialNetwork  " + name.toString(), ex);
		}

		return snet;
	}

	@Override
	public boolean emailExists(final AccountType accountType, final String email)
			throws ServiceException
	{
		try
		{
			final Search search = accountType == AccountType.CUS ? new Search(CustomerImpl.class)
					: new Search(MerchantImpl.class);

			search.addField("id");
			search.addFilterEqual("email", email.toLowerCase());
			final UUID id = (UUID) daoDispatcher.searchUnique(search);
			return id == null ? false : true;
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem emailExists  " + email, ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteMerchant(final String id) throws ServiceException
	{
		removeElement(id, MerchantImpl.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final Merchant merchant) throws ServiceException
	{
		try
		{
			daoDispatcher.save(merchant);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving merchant " + merchant;
			LOG.error(err, e);
			throw new ServiceException(err, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Merchant getMerchantById(final UUID id) throws ServiceException
	{
		Merchant merchant;
		try
		{
			merchant = daoDispatcher.find(MerchantImpl.class, id);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getMerchantById  " + id, ex);
		}

		return merchant;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchantByName(final String name) throws ServiceException
	{
		List<Merchant> merchants = null;

		try
		{
			final Search search = new Search(MerchantImpl.class);
			search.addFilterEqual("name", name);
			merchants = daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getMerchantByName  " + name, ex);
		}

		return merchants;
	}

	private void removeElement(final String id, Class<MerchantImpl> clazz) throws ServiceException
	{
		boolean deleted = false;
		try
		{
			deleted = daoDispatcher.removeById(clazz, id);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem removing ID '%d' for domain %s", id,
					clazz.getSimpleName()), ex);
		}

		if (!deleted)
		{
			throw new ServiceException((String.format("Element ID '%d' not found for domain %s", id,
					clazz.getSimpleName())));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Deal> getMerchantDeals(final UUID merchantId, final Boolean isActive)
			throws ServiceException
	{
		List<Deal> merchantDeals = null;

		try
		{
			final Search search = new Search(DealImpl.class);
			search.addFilterEqual("merchant.id", merchantId);
			if (isActive != null)
			{
				search.addFilterEqual("isActive", isActive);
			}
			merchantDeals = daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getMerchantDeals for merchantId " + merchantId, ex);
		}

		return merchantDeals;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final Deal merchantDeal) throws ServiceException
	{
		try
		{
			daoDispatcher.save((DealImpl) merchantDeal);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving merchantDeal " + merchantDeal;
			throw new ServiceException(err, e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Deal> getDealsByMerchantId(final UUID merchantId) throws ServiceException
	{
		try
		{
			final Search search = new Search(DealImpl.class);
			search.addFilterEqual("merchant.id", merchantId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealsByMerchantId %s", merchantId), ex);
		}
	}

	@Override
	public Tag getTag(final String tagName) throws ServiceException
	{
		try
		{
			final Search search = new Search(TagImpl.class);
			search.addFilterEqual("name", tagName);
			return (Tag) daoDispatcher.searchUnique(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getTag %s", tagName), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tag> getTags() throws ServiceException
	{
		try
		{
			final Search search = new Search(TagImpl.class);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getTags", ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addTags(final List<Tag> tags) throws ServiceException
	{
		try
		{
			for (Tag tag : tags)
			{
				daoDispatcher.save((TagImpl) tag);
			}

		}
		catch (Exception e)
		{
			throw new ServiceException("Problem adding tags", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final MerchantAccount merchantAccount) throws ServiceException
	{
		try
		{
			daoDispatcher.save((MerchantAccountImpl) merchantAccount);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving merchantAccount " + merchantAccount;
			throw new ServiceException(err, e);
		}
	}

	@Override
	public MerchantAccount authenticateMerchantAccount(final UUID merchantId, final String email,
			final String password) throws ServiceException
	{
		try
		{
			final String md5pass = EncryptService.MD5(password);
			final Query query = sessionFactory
					.getCurrentSession()
					.createQuery(
							"from MerchantAccountImpl where merchant.id=:merchantId and email=:email and password=:pass");

			query.setParameter("merchantId", merchantId);
			query.setParameter("email", email);
			query.setParameter("pass", md5pass);
			return (MerchantAccount) query.uniqueResult();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem authenticateMerchantAccount %s %s",
					merchantId, email), ex);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final DealOffer dealOffer) throws ServiceException
	{
		try
		{
			daoDispatcher.save(dealOffer);
		}
		catch (Exception e)
		{
			throw new ServiceException("There was a problem saving dealOffer ", e);
		}

	}

	@Override
	public DealOffer getDealOffer(final UUID dealOfferId) throws ServiceException
	{
		try
		{
			final Search search = new Search(DealOfferImpl.class);
			search.addFilterEqual("id", dealOfferId);
			return (DealOffer) daoDispatcher.searchUnique(search);
		}
		catch (Exception e)
		{
			throw new ServiceException("There was a problem in getDealOfferById", e);
		}
	}

	@Override
	public Deal getDeal(final UUID dealId) throws ServiceException
	{
		try
		{
			return daoDispatcher.find(DealImpl.class, dealId);
		}
		catch (Exception e)
		{
			throw new ServiceException("There was a problem in getDeal " + dealId, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Deal> getDealsByDealOfferId(final UUID dealOfferId, final SearchOptions searchOpts, boolean activeDealsOnly) throws ServiceException
	{
		List<Deal> deals = null;

		try
		{
			final String newSql = activeDealsOnly ? QueryHelper.buildQuery(QueryType.ActiveDealsByDealOfferId, null, searchOpts) : QueryHelper.buildQuery(
					QueryType.DealsByDealOfferId, null, searchOpts);
			final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
			query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			query.setParameter("dealOfferId", dealOfferId, PostgresUUIDType.INSTANCE);

			QueryHelper.applyOffsetLimit(query, searchOpts);

			deals = query.list();

		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getDealsByDealOfferId for dealOfferId " + dealOfferId + " " + ex.getLocalizedMessage(),
					ex);
		}

		return deals;

	}

	private static void setSearchOptions(final Search search, final SearchOptions searchOpts, final String[] eagerlyLoadedProps)
	{
		if (searchOpts != null)
		{
			if (searchOpts.getMaxResults() != null)
			{
				search.setMaxResults(searchOpts.getMaxResults());
			}
			if (searchOpts.getPage() != null)
			{
				search.setPage(searchOpts.getPage());
			}
			if (searchOpts.getSortProperty() != null)
			{
				search.addSort(searchOpts.getSortProperty(), !searchOpts.isAscending());
			}
		}

		search.addFetches(eagerlyLoadedProps);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealOfferPurchase> getDealOfferPurchasesByDealOfferId(final UUID dealOfferId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(DealOfferPurchaseImpl.class);
			search.addFilterEqual("dealOffer.id", dealOfferId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealBookPurchasesByDealOfferId %s",
					dealOfferId), ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final DealOfferPurchase dealOfferPurchase) throws ServiceException
	{
		try
		{
			daoDispatcher.save(dealOfferPurchase);
		}
		catch (Exception e)
		{
			throw new ServiceException("There was a problem saving dealOfferPurchase ", e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchants() throws ServiceException
	{
		try
		{
			Criteria crit = getSessionFactory().getCurrentSession().createCriteria(MerchantImpl.class);
			crit.setCacheMode(CacheMode.GET);
			crit.setCacheable(true);
			return crit.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getMerchants"), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getAllMerchants() throws ServiceException
	{
		List<Merchant> merchants = null;

		try
		{
			final Criteria criteria = getCurrentSession().createCriteria(MerchantImpl.class);
			criteria.addOrder(Order.asc("name"));

			merchants = criteria.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealOffers"), ex);
		}

		return merchants;
	}

	@Override
	public Long sizeOfCollection(Object collection) throws ServiceException
	{
		return ((Long) getSessionFactory().getCurrentSession()
				.createFilter(collection, "select count(*)").list().get(0)).longValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<Tag> getDealOfferTags(final UUID dealOfferId) throws ServiceException
	{
		Set<Tag> tags = null;

		try
		{
			final Query query = getSessionFactory()
					.getCurrentSession()
					.createSQLQuery(
							"select distinct t.* from tag as t, deal_tag as dt,deal_offer as dof, deal as d where t.tag_id=dt.tag_id and dof.deal_offer_id=d.deal_offer_id and d.deal_offer_id=:dealOfferId and d.deal_id=dt.deal_id")
					.addEntity(TagImpl.class);

			query.setParameter("dealOfferId", dealOfferId, PostgresUUIDType.INSTANCE);

			final List<Tag> tagList = (query.list());

			if (CollectionUtils.isNotEmpty(tagList))
			{
				tags = new HashSet<Tag>();
				tags.addAll(tagList);
			}

			return tags;

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealOfferTags %s", dealOfferId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealOffer> getDealOffers() throws ServiceException
	{
		try
		{
			final Criteria criteria = getCurrentSession().createCriteria(DealOfferImpl.class);
			return criteria.list();

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealOffers"), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealOffer> getDealOffersByMerchantId(final UUID merchantId) throws ServiceException
	{
		try
		{
			final Search search = new Search(DealOfferImpl.class);
			search.addSort(Sort.desc("createdUpdated.updated"));
			search.addFilterEqual("merchant.id", merchantId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealOffersByMerchantId %s", merchantId),
					ex);
		}
	}

	private String cleanTagName(final String tagName)
	{
		return tagName.trim().toLowerCase();
	}

	/**
	 * TODO read tags from a TagCache or use 2nd level caching!
	 */
	@Override
	public Set<Tag> getOrCreateTags(final String... tags) throws ServiceException
	{
		Set<Tag> tagList = new HashSet<Tag>();

		for (final String tagName : tags)
		{

			Tag _tag = getTag(cleanTagName(tagName));
			if (_tag != null)
			{
				tagList.add(_tag);
			}
			else
			{
				_tag = FactoryManager.get().getDomainFactory().newTag(tagName);
				_tag.setName(tagName);
				tagList.add(_tag);
			}
		}

		return tagList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MerchantAccount> getAccountsForMerchant(final UUID merchantId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(MerchantAccountImpl.class);
			search.addFilterEqual("merchant.id", merchantId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getAccountsForMerchant %s", merchantId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MerchantLocation> getLocationsForMerchant(UUID merchantId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(MerchantLocationImpl.class);
			search.addFilterEqual("merchant.id", merchantId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getLocationsForMerchant %s", merchantId),
					ex);
		}
	}

	@Override
	public MerchantLocation getMerchantLocationById(Long merchantLocationId)
			throws ServiceException
	{
		MerchantLocation merchantLocation;
		try
		{
			merchantLocation = daoDispatcher.find(MerchantLocationImpl.class,
					merchantLocationId);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getMerchantLocationById  " + merchantLocationId,
					ex);
		}

		return merchantLocation;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final MerchantLocation merchantLocation) throws ServiceException
	{
		try
		{
			daoDispatcher.save((MerchantLocationImpl) merchantLocation);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving MerchantLocation "
					+ merchantLocation;
			throw new ServiceException(err, e);
		}

	}

	@Override
	public MerchantAccount getMerchantAccountById(Long merchantAccountId) throws ServiceException
	{
		MerchantAccount merchantAccount;
		try
		{
			merchantAccount = daoDispatcher.find(MerchantAccountImpl.class, merchantAccountId);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getMerchantLocationById  " + merchantAccountId, ex);
		}

		return merchantAccount;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MerchantAccount authenticateMerchantAccount(final String email, final String password)
			throws ServiceException
	{
		List<MerchantAccount> accounts = null;

		try
		{
			final Search search = new Search(MerchantAccountImpl.class);
			search.addFilterEqual("email", email);
			accounts = daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem authenticateMerchantAccount %s", email), ex);
		}

		if (CollectionUtils.isNotEmpty(accounts))
		{
			if (accounts.size() > 1)
			{
				throw new ServiceException(
						String
								.format(
										"%s is associated with mutliple merchant accounts. Cannot authenticate via a simple email/pass",
										email));
			}

			try
			{
				if (accounts.get(0).getPassword().equals(EncryptService.MD5(password)))
				{
					return accounts.get(0);
				}
			}
			catch (Exception ex)
			{
				throw new ServiceException("Problem authentication merchant account for email " + email, ex);
			}
		}

		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MerchantIdentity> getAuthorizedMerchantIdentities(final Long merchantAccountId)
			throws ServiceException
	{
		List<MerchantIdentity> identies;

		try
		{
			final Query query = getSessionFactory()
					.getCurrentSession()
					.createSQLQuery(
							"select m.merchant_id as id, m.merchant_name as name from merchant as m order by m.merchant_name asc")
					.addScalar("id", StandardBasicTypes.UUID_CHAR)
					.addScalar("name", StandardBasicTypes.STRING)
					.setResultTransformer(Transformers.aliasToBean(MerchantIdentityImpl.class));

			identies = query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getAuthorizedMerchants " + merchantAccountId, ex);
		}

		return identies;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Deal> getAllRelatedDealsForMerchantId(final UUID merchantId) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory.getCurrentSession().getNamedQuery("allRelatedDeals");

			query.setParameter("merchantId", merchantId);

			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getAllRelatedDealsForMerchantId %s",
					merchantId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealOffer> getAllRelatedDealsOffersForMerchantId(final UUID merchantId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(DealOfferImpl.class);
			// TODO read permissions or other to get only deal offers visible to
			// merchantId
			search.addFilterEqual("merchant.id", merchantId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getAllRelatedDealsOffersForMerchantId %s",
					merchantId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealAcquireHistory> getDealAcquireHistory(final UUID dealAcquireId, boolean chronological)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(DealAcquireHistoryImpl.class);
			search.addFilter(Filter.equal("primaryKey.dealAcquire.id", dealAcquireId));

			if (chronological)
			{
				search.addSort(Sort.asc("primaryKey.updated"));
			}
			else
			{
				search.addSort(Sort.desc("primaryKey.updated"));
			}

			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealAcquireHistory %s", dealAcquireId),
					ex);
		}
	}

	@Override
	public List<Merchant> getMerchantsWithin(final Location location,
			final int maxMiles, SearchOptions searchOpts) throws ServiceException
	{
		final List<Merchant> merchants = new ArrayList<Merchant>();

		final Point point = new Point(location.getLongitude(), location.getLatitude());
		point.setSrid(4326);

		final ImmutableMap<String, Object> params = ImmutableMap.<String, Object> builder()
				.put("point", point.toString())
				.put("isDiscoverable", true)
				.put("distanceInMeters", SpatialUtils.milesToMeters(maxMiles)).build();

		final String newSql = QueryHelper.buildQuery(QueryType.MerchantsWithinMeters, params,
				searchOpts);

		try
		{
			final SQLQuery query =
					getSessionFactory().getCurrentSession().createSQLQuery(newSql);
			query.addScalar("distanceInMeters", StandardBasicTypes.DOUBLE);
			query.addScalar("merchantId", PostgresUUIDType.INSTANCE);
			query.addScalar("name", StandardBasicTypes.STRING);
			query.addEntity("merchant_location", MerchantLocationImpl.class);
			query.addEntity("category", CategoryImpl.class);

			final Map<UUID, MerchantImpl> merchantMap = new HashMap<UUID, MerchantImpl>();

			query.setResultTransformer(new ResultTransformer()
			{

				private static final long serialVersionUID = 1L;

				@Override
				public Object transformTuple(Object[] tuple, String[] aliases)
				{
					final UUID uuid = (UUID) tuple[1];
					final String name = (String) tuple[2];
					final MerchantLocationImpl location = (MerchantLocationImpl) tuple[3];
					final Category category = (Category) tuple[4];
					location.setDistanceInMeters((Double) tuple[0]);
					MerchantImpl merchant = merchantMap.get(uuid);

					if (merchant == null)
					{
						merchant = new MerchantImpl();
						merchant.setId(uuid);
						merchant.setName(name);
						merchant.setCategory(category);

						merchant.getLocations().add(location);
						merchantMap.put(uuid, merchant);
						merchants.add(merchant);
					}
					else
					{
						merchant.getLocations().add(location);
					}

					return null;
				}

				@SuppressWarnings("rawtypes")
				@Override
				public List transformList(List collection)
				{
					return merchants;
				}
			});

			query.list();

		}
		catch (Exception ex)
		{
			String msg = String.format(
					"Problem getting merchants within lng/lat %s and maxMiles %d", location, maxMiles);
			LOG.error(msg, ex);
			throw new ServiceException(msg, ex);
		}

		return merchants;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchantsWithin2(final Location location,
			final int maxMiles, SearchOptions searchOpts) throws ServiceException
	{
		List<Merchant> merchants = null;

		final Coordinate coord = new Coordinate(location.getLongitude(), location.getLatitude());

		@SuppressWarnings("deprecation")
		final com.vividsolutions.jts.geom.Geometry point = new com.vividsolutions.jts.geom.Point(coord,
				new PrecisionModel(PrecisionModel.FLOATING), 4326);

		try
		{
			final Criteria criteria = getCurrentSession().createCriteria(MerchantImpl.class, "merchant");
			criteria.createAlias("locations", "loc");

			criteria.add(SpatialRestrictions.distanceWithin("loc.geometry", point, SpatialUtils.milesToMeters(maxMiles)));

			merchants = criteria.list();

			for (final Merchant merch : merchants)
			{
				for (MerchantLocation loc : merch.getLocations())
				{
					System.out.println("Distance away : " + SpatialUtils.metersToMiles(loc.getGeometry().distance(point)));
				}

			}

		}
		catch (Exception ex)
		{
			String msg = String.format(
					"Problem getting merchants within lng/lat %s and maxMiles %d", location, maxMiles);
			LOG.error(msg, ex);
			throw new ServiceException(msg, ex);
		}

		return merchants;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Category> getAllCategories() throws ServiceException
	{
		try
		{
			final Search search = new Search(CategoryImpl.class);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(
					String.format("Problem getAllCategories"), ex);
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	protected void persistObject(Object obj) throws ServiceException
	{
		try
		{
			daoDispatcher.save(obj);
		}
		catch (Exception e)
		{
			throw new ServiceException("There was a problem saving object" + obj, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final Tag tag) throws ServiceException
	{
		persistObject(tag);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final Category category) throws ServiceException
	{
		persistObject(category);
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public CategoryTag createCategoryTag(final String categoryName, final String tagName)
			throws ServiceException
	{
		CategoryTag categoryTag;

		try
		{
			Category category = getCategory(categoryName);
			Tag tag = getTag(tagName);
			if (category == null)
			{
				category = new CategoryImpl();
				category.setName(categoryName);
			}
			if (tag == null)
			{
				tag = new TagImpl();
				tag.setName(tagName);
			}

			categoryTag = new CategoryTagImpl(category, tag);

			daoDispatcher.save(category);
			daoDispatcher.save(tag);
			daoDispatcher.save(categoryTag);

		}
		catch (Exception e)
		{
			throw new ServiceException(String.format(
					"There was a problem saving category %s tageName %s", categoryName, tagName), e);
		}

		return categoryTag;
	}

	@Override
	public Category getCategory(final String categoryName) throws ServiceException
	{
		try
		{
			final Search search = new Search(CategoryImpl.class);
			search.addFilterEqual("name", categoryName);
			return (Category) daoDispatcher.searchUnique(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getCategory %s", categoryName), ex);
		}
	}

	@Override
	public Category getCategory(final Integer categoryId) throws ServiceException
	{
		try
		{
			return (Category) getCurrentSession().get(CategoryImpl.class, categoryId);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getCategory %s", categoryId), ex);
		}
	}

	@Override
	public Map<Category, List<Tag>> getCategoryTags() throws ServiceException
	{
		final Map<Category, List<Tag>> catTagMap = new HashMap<Category, List<Tag>>();

		try
		{
			final Query query = getSessionFactory().getCurrentSession().getNamedQuery("allCategoryTags")
					.setResultTransformer(new ResultTransformer()
					{
						private static final long serialVersionUID = 1L;

						@Override
						public Object transformTuple(final Object[] tuple, final String[] aliases)
						{
							final Category cat = (Category) tuple[0];
							final Tag tag = (Tag) tuple[1];

							List<Tag> tagList = catTagMap.get(cat);
							if (tagList == null)
							{
								tagList = new ArrayList<Tag>();
							}
							tagList.add(tag);

							catTagMap.put(cat, tagList);

							return null;
						}

						@SuppressWarnings("rawtypes")
						@Override
						public List transformList(List collection)
						{
							// nothing to return, we built our map
							return collection;
						}
					});

			query.list();

		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getCategoryTags", ex);
		}

		return catTagMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MerchantMedia> getMerchantMedias(final UUID merchantId,
			final MediaType[] mediaTypes, final SearchOptions searchOpts) throws ServiceException
	{
		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.GetMerchantMedias, null, searchOpts,
					true);

			final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
			query.setParameter("merchantId", merchantId);
			query.setParameterList("mediaTypes", mediaTypes);
			QueryHelper.applyOffsetLimit(query, searchOpts);
			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format(
					"Problem getMerchantMedias merchantId %s", merchantId, merchantId), ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveMerchantMedia(final MerchantMedia merchantMedia) throws ServiceException
	{
		try
		{
			daoDispatcher.save(merchantMedia);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format(
					"Problem saving merchantMedia for merchantId '%s' mediaUrl '%s' ",
					merchantMedia.getMerchantId(),
					merchantMedia.getMediaUrl(), ex));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealAcquire> getRedeemedDealAcquires(final UUID merchantId, final String redemptionCode) throws ServiceException
	{
		try
		{

			final Search search = new Search(DealAcquireImpl.class);
			search.addFilterEqual("redemptionCode", redemptionCode);
			search.addFilterEqual("deal.merchant.id", merchantId);
			return (List<DealAcquire>) daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getRedeemedDealAcquires redemptionCode %s", redemptionCode), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealAcquireHistory> getDealAcquireHistoryByGiftId(final UUID giftId, final boolean chronological)
			throws ServiceException
	{
		try
		{
			final Query query = getCurrentSession()
					.createQuery(
							"select d from DealAcquireHistoryImpl as d, GiftImpl as g where d.primaryKey.dealAcquire.id=g.dealAcquire.id and g.id=:giftId order by d.primaryKey.updated desc");
			query.setParameter("giftId", giftId, PostgresUUIDType.INSTANCE);

			return query.list();

		}
		catch (Exception ex)
		{
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
	public void createActivationCodes(final UUID dealOfferId, final int totalCodes) throws ServiceException
	{
		final Set<ActivationCode> currentCodes = new HashSet<ActivationCode>();
		final List<ActivationCode> newCodes = new ArrayList<ActivationCode>();

		try
		{
			final Search search = new Search(ActivationCodeImpl.class);
			search.addFilterEqual("dealOfferId", dealOfferId);
			search.addField("code");
			currentCodes.addAll(daoDispatcher.search(search));

			int i = 0;
			while (i < totalCodes)
			{
				final String code = activiationCodeStrategy.generateCode();

				if (!currentCodes.contains(code))
				{
					final ActivationCode actCode = new ActivationCodeImpl();
					actCode.setCode(code);
					actCode.setDealOfferId(dealOfferId);
					newCodes.add(actCode);
					i++;
				}
				else
				{
					LOG.warn(String.format("duplicated activation code %s for dealOfferId %s", code, dealOfferId));
				}
			}

			daoDispatcher.save(newCodes.toArray());

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem createActivationCodes dealOfferId %s", dealOfferId), ex);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getActivationCodes(final UUID dealOfferId) throws ServiceException
	{
		try
		{
			final Search search = new Search(ActivationCodeImpl.class);
			search.addFilterEqual("dealOfferId", dealOfferId);
			search.addField("code");
			// we want ascending so the first created code is first in the list
			search.addSort(new Sort("created", false));
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem createActivationCodes dealOfferId %s", dealOfferId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivationSummary> getActivationSummaries(final UUID merchantId) throws ServiceException
	{
		List<ActivationSummary> summaries = null;

		try
		{

			final Query query = getCurrentSession()
					.createSQLQuery(QueryHelper.QueryType.ActivationSummary.getQuery()).
					addScalar("totalCodes", StandardBasicTypes.INTEGER).
					addScalar("totalActivations", StandardBasicTypes.INTEGER).
					addScalar("title", StandardBasicTypes.STRING).
					addScalar("dealOfferId", PostgresUUIDType.INSTANCE);

			query.setParameter("merchantId", merchantId, PostgresUUIDType.INSTANCE);

			query.setResultTransformer(Transformers.aliasToBean(ActivationSummary.class));

			summaries = query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getActivationSummaries for merchantId %s", merchantId), ex);
		}

		return summaries;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteCustomer(final UUID customerId) throws ServiceException
	{
		try
		{
			final Query query = getCurrentSession().createQuery("delete from CustomerImpl where id=:customerId").
					setParameter("customerId", customerId);

			query.executeUpdate();
		}
		catch (Exception e)
		{
			throw new ServiceException("Problem deleteing customerId " + customerId, e);
		}

	}

	@Override
	public DealOfferGeoSummariesResult getDealOfferGeoSummariesWithin(final Location location, final int maxMiles,
			final SearchOptions searchOpts, final SearchOptions fallbackSearchOpts)
			throws ServiceException
	{
		final List<DealOfferGeoSummary> summaries = new ArrayList<DealOfferGeoSummary>();
		boolean usingFallback = !isLocationAvailable(location);
		String newSql = null;
		SQLQuery query = null;

		final ResultTransformer resultTransformer = new ResultTransformer()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] tuple, String[] aliases)
			{
				final UUID dealOfferId = (UUID) tuple[0];
				final Double distanceInMeters = tuple.length == 2 ? (Double) tuple[1] : null;
				final DealOfferMetadata metadata = DealOfferMetadataCache.get().getDealOfferMetrics(dealOfferId);

				if (metadata == null)
				{
					LOG.warn(String.format("Metadata cache does not yet contain dealOfferId %s . Skipping", dealOfferId));
					return null;
				}

				final DealOfferGeoSummary geoSummary = new DealOfferGeoSummary(metadata.getDealOffer(),
						distanceInMeters, null, metadata.getDealOfferMetrics().getLongMetrics(), metadata.getDealOfferMetrics().getDoubleMetrics());
				summaries.add(geoSummary);
				return null;
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List collection)
			{
				return summaries;
			}
		};

		try
		{
			if (!usingFallback)
			{
				final Point point = new Point(location.getLongitude(), location.getLatitude());
				point.setSrid(4326);
				final ImmutableMap<String, Object> params = ImmutableMap.<String, Object> builder()
						.put("point", point.toString())
						.put("distanceInMeters", SpatialUtils.milesToMeters(maxMiles)).build();

				newSql = QueryHelper.buildQuery(QueryType.ActiveDealOfferIDsWithinMeters, params,
						searchOpts);
				query = getSessionFactory().getCurrentSession().createSQLQuery(newSql);
				query.addScalar("dealOfferId", PostgresUUIDType.INSTANCE);
				query.addScalar("distanceInMeters", StandardBasicTypes.DOUBLE);

				query.setResultTransformer(resultTransformer);
				query.list();
			}

			// if no results are found with the spacial query, or we are using the
			// fall back because location is unavailable, then fall back to query
			if (CollectionUtils.isEmpty(summaries) && fallbackSearchOpts != null)
			{
				usingFallback = true;
				newSql = QueryHelper.buildQuery(QueryType.ActiveDealOfferIDs, null, fallbackSearchOpts);
				query = getSessionFactory().getCurrentSession().createSQLQuery(newSql);
				query.addScalar("dealOfferId", PostgresUUIDType.INSTANCE);
				query.setResultTransformer(resultTransformer);
				query.list();
			}

		}
		catch (Exception ex)
		{
			String msg = String.format(
					"Problem getting merchants within lng/lat %s and maxMiles %d", location, maxMiles);
			LOG.error(msg, ex);
			throw new ServiceException(msg, ex);
		}

		return new DealOfferGeoSummariesResult(summaries, usingFallback);
	}

	protected static boolean isLocationAvailable(final Location location)
	{
		return (location != null && location.getLongitude() != null && location.getLatitude() != null && (location.getLongitude() != 0.0 && location
				.getLatitude() != 0.0));
	}

	@Override
	public Map<UUID, DealOfferMetrics> getDealOfferMetrics() throws ServiceException
	{
		final Map<UUID, DealOfferMetrics> dealOfferMetrics = new HashMap<UUID, DealOfferMetrics>();

		final String newSql = QueryHelper.buildQuery(QueryType.DealOfferBasicStats, null, null);

		try
		{
			final SQLQuery query =
					getSessionFactory().getCurrentSession().createSQLQuery(newSql);

			query.addScalar("dealOfferId", PostgresUUIDType.INSTANCE);
			query.addScalar("totalMerchants", StandardBasicTypes.LONG);
			query.addScalar("totalDeals", StandardBasicTypes.LONG);

			query.setResultTransformer(new ResultTransformer()
			{

				private static final long serialVersionUID = 1L;

				@Override
				public Object transformTuple(Object[] tuple, String[] aliases)
				{
					final UUID dealOfferId = (UUID) tuple[0];
					final DealOfferMetrics metric = new DealOfferMetrics(dealOfferId);
					metric.addLongMetric(DealOfferMetrics.MetricType.TotalMerchants.toString(), (Long) tuple[1]);
					metric.addLongMetric(DealOfferMetrics.MetricType.TotalDeals.toString(), (Long) tuple[2]);
					dealOfferMetrics.put(dealOfferId, metric);
					return null;
				}

				@SuppressWarnings("rawtypes")
				@Override
				public List transformList(List collection)
				{
					return collection;
				}

			});

			query.list();

		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem executing dealOfferMetrics", ex);
		}

		return dealOfferMetrics;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchantsByDealOfferId(final UUID dealOfferId, final SearchOptions searchOpts) throws ServiceException
	{
		try
		{
			final String newHql = QueryHelper.buildQuery(QueryType.MerchantsByDealOfferId, null, searchOpts,
					true);

			final Query query = sessionFactory.getCurrentSession().createQuery(newHql);
			query.setParameter("dealOfferId", dealOfferId);

			QueryHelper.applyOffsetLimit(query, searchOpts);

			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getMerchantsByDealOfferId %s ", dealOfferId), ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public DealOffer deepCopyDealOffer(final UUID dealOfferId) throws ServiceException
	{
		try
		{
			final DealOffer dealOffer = getDealOffer(dealOfferId);
			final DealOffer newDealOffer = ((DealOfferImpl) dealOffer).copy();
			newDealOffer.setActive(false);
			newDealOffer.setTitle("copy of '" + dealOffer.getTitle() + "'");

			daoDispatcher.save(newDealOffer);

			final List<Deal> deals = getDealsByDealOfferId(dealOffer.getId(), null, false);
			final Deal[] newDeals = new Deal[deals.size()];
			int idx = 0;

			for (Deal deal : deals)
			{
				final Deal newDeal = ((DealImpl) deal).copy();
				deal = null;
				newDeal.setDealOffer(newDealOffer);
				newDeals[idx++] = newDeal;
			}

			daoDispatcher.save((Object[]) newDeals);

			return newDealOffer;
		}
		catch (ServiceException se)
		{
			throw se;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchantsCreatedByMerchant(final UUID merchantId) throws ServiceException
	{
		List<Merchant> merchants;

		try
		{
			final Query query = sessionFactory.getCurrentSession().getNamedQuery("getMerchantsCreatedByMerchant");
			query.setParameter("merchantId", merchantId, PostgresUUIDType.INSTANCE);
			merchants = query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getMerchantsCreatedByMerchant merchantId %s", merchantId), ex);
		}

		return merchants;
	}

}
