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
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.StandardBasicTypes;
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
import com.talool.core.AccountType;
import com.talool.core.Category;
import com.talool.core.CategoryTag;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealAcquireHistory;
import com.talool.core.DealOffer;
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
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.core.social.SocialNetwork;
import com.talool.domain.AddressImpl;
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
import com.talool.utils.SpatialUtils;

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
			search.addFilterEqual("email", email);
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
	public List<Deal> getDealsByDealOfferId(final UUID dealOfferId) throws ServiceException
	{
		try
		{
			final Search search = new Search(DealImpl.class);
			search.addFilterEqual("dealOffer.id", dealOfferId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getDealsByDealOfferId for dealOfferId " + dealOfferId, ex);
		}

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
			final Search search = new Search(DealOfferImpl.class);
			search.addSort(Sort.desc("createdUpdated.updated"));
			return daoDispatcher.search(search);
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
	public List<DealAcquireHistory> getDealAcquireHistory(final UUID dealAcquireId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(DealAcquireHistoryImpl.class);
			search.addFilter(Filter.equal("primaryKey.dealAcquire.id", dealAcquireId));

			search.addSort(Sort.asc("primaryKey.updated"));
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
			query.addEntity("address", AddressImpl.class);
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
					final Category category = (Category) tuple[5];
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
			throw new ServiceException(String.format(
					"Problem getting merchants within lng/lat %s and maxMiles %d", location, maxMiles), ex);
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

}
