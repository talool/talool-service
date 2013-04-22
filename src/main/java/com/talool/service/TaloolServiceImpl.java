package com.talool.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.hibernate.DAODispatcher;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;
import com.talool.core.AccountType;
import com.talool.core.AcquireStatus;
import com.talool.core.AcquireStatusType;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealAcquireHistory;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.FactoryManager;
import com.talool.core.IdentifiableS;
import com.talool.core.IdentifiableUUID;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantIdentity;
import com.talool.core.MerchantManagedLocation;
import com.talool.core.Relationship;
import com.talool.core.SearchOptions;
import com.talool.core.SearchOptions.SortType;
import com.talool.core.SocialNetwork;
import com.talool.core.Tag;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.domain.AcquireStatusImpl;
import com.talool.domain.CustomerImpl;
import com.talool.domain.DealAcquireHistoryImpl;
import com.talool.domain.DealAcquireImpl;
import com.talool.domain.DealImpl;
import com.talool.domain.DealOfferImpl;
import com.talool.domain.DealOfferPurchaseImpl;
import com.talool.domain.MerchantAccountImpl;
import com.talool.domain.MerchantIdentityImpl;
import com.talool.domain.MerchantImpl;
import com.talool.domain.MerchantManagedLocationImpl;
import com.talool.domain.RelationshipImpl;
import com.talool.domain.SocialNetworkImpl;
import com.talool.domain.TagImpl;

/**
 * Implementation of the TaloolService
 * 
 * @author clintz
 */
@Transactional(readOnly = true)
@Service
@Repository
public class TaloolServiceImpl implements TaloolService
{
	private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceImpl.class);

	private static final String GET_DEAL_ACQUIRES = "select dealAcquire from DealAcquireImpl dealAcquire, DealImpl d where d.merchant.id=:merchantId and dealAcquire.deal.id=d.id and dealAcquire.customer.id=:customerId";

	private static final String GET_MERCHANT_ACQUIRES = "select distinct merchant from MerchantImpl merchant, DealAcquireImpl da, DealImpl d where da.customer.id=:customerId and da.deal.id=d.id and d.merchant.id=merchant.id";

	private DAODispatcher daoDispatcher;
	private SessionFactory sessionFactory;

	public TaloolServiceImpl()
	{}

	public SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void createAccount(final Customer customer, final String password) throws ServiceException
	{
		createAccount(AccountType.CUS, customer, password);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final Customer customer) throws ServiceException
	{
		try
		{
			daoDispatcher.save((CustomerImpl) customer);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving customer " + customer;
			LOG.error(err, e);
			throw new ServiceException(err, e);
		}

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
	public Customer getCustomerByEmail(final String email) throws ServiceException
	{
		Customer customer = null;

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

	@Override
	public SocialNetwork getSocialNetwork(final String name) throws ServiceException
	{
		SocialNetwork snet;
		try
		{
			final Search search = new Search(SocialNetworkImpl.class);
			search.addFilterEqual("name", name);
			snet = (SocialNetwork) daoDispatcher.searchUnique(search);

		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getSocialNetwork  " + name, ex);
		}

		return snet;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteCustomer(final UUID id) throws ServiceException
	{
		removeElement(id, CustomerImpl.class);
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

	private void createAccount(final AccountType accountType, final IdentifiableS account,
			final String password) throws ServiceException
	{
		try
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Creating accountType:" + accountType + ": " + account.toString());
			}

			final String md5pass = EncryptService.MD5(password);

			if (accountType == AccountType.MER)
			{
				save((MerchantImpl) account);
				daoDispatcher.flush(MerchantImpl.class);
				daoDispatcher.refresh((MerchantImpl) account);
			}
			else
			{
				((CustomerImpl) (account)).setPassword(md5pass);
				save((CustomerImpl) account);
				daoDispatcher.flush(CustomerImpl.class);
				daoDispatcher.refresh((CustomerImpl) account);
			}
		}
		catch (Exception e)
		{
			final String err = "There was a problem registering  " + account;
			LOG.error(err, e);
			throw new ServiceException(err, e);
		}

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

			final String md5pass = EncryptService.MD5(password);

			((CustomerImpl) (account)).setPassword(md5pass);
			save((CustomerImpl) account);
			daoDispatcher.flush(CustomerImpl.class);
			daoDispatcher.refresh((CustomerImpl) account);

		}
		catch (Exception e)
		{
			final String err = "There was a problem registering  " + account;
			LOG.error(err, e);
			throw new ServiceException(err, e);
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
			daoDispatcher.save((MerchantImpl) merchant);
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

	private void removeElement(final UUID id, Class<CustomerImpl> clazz) throws ServiceException
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
	public void refresh(final Object obj) throws ServiceException
	{
		try
		{
			daoDispatcher.flush(obj.getClass());
			daoDispatcher.refresh(obj);
		}
		catch (Exception e)
		{
			throw new ServiceException("There was a problem refreshing", e);
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
			return daoDispatcher.find(DealOfferImpl.class, dealOfferId);
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
			final Search search = new Search(MerchantImpl.class);
			return daoDispatcher.search(search);
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

	@SuppressWarnings("unchecked")
	@Override
	public List<DealOffer> getDealOffersByMerchantId(final UUID merchantId) throws ServiceException
	{
		try
		{
			final Search search = new Search(DealOfferImpl.class);
			search.addSort(Sort.desc("createdUpdated.updated"));
			// TODO return only deals they can see . For now we must return all Deal
			// Offers available
			// search.addFilterEqual("merchant.id", merchantId);
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
		return tagName.trim();
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
	public List<MerchantManagedLocation> getLocationsForMerchant(UUID merchantId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(MerchantManagedLocationImpl.class);
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
	public MerchantManagedLocation getMerchantLocationById(Long merchantManagedLocationId)
			throws ServiceException
	{
		MerchantManagedLocation merchantLocation;
		try
		{
			merchantLocation = daoDispatcher.find(MerchantManagedLocationImpl.class,
					merchantManagedLocationId);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getMerchantLocationById  " + merchantManagedLocationId,
					ex);
		}

		return merchantLocation;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final MerchantManagedLocation merchantManagedLocation) throws ServiceException
	{
		try
		{
			daoDispatcher.save((MerchantManagedLocationImpl) merchantManagedLocation);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving MerchantManagedLocation "
					+ merchantManagedLocation;
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
			// search.addFilterEqual("merchant.id", merchantId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getAllRelatedDealsOffersForMerchantId %s",
					merchantId), ex);
		}
	}

	@Override
	public AcquireStatus getAcquireStatus(final AcquireStatusType type) throws ServiceException
	{
		// TODO Replace with a cache or 2nd level query cache
		try
		{
			final Search search = new Search(AcquireStatusImpl.class);
			search.addFilter(Filter.equal("status", type.toString()));

			return (AcquireStatus) daoDispatcher.searchUnique(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getAcquireStatus %s", type), ex);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void giveDeal(final DealAcquire dealAcquire, final Customer toCustomer)
			throws ServiceException
	{
		final DealAcquireImpl dealAcq = (DealAcquireImpl) dealAcquire;

		if (dealAcq.getAcquireStatus().getStatus().equals(AcquireStatusType.REDEEMED))
		{
			throw new ServiceException("Cannot give an already redeemed deal " + dealAcquire);
		}

		try
		{
			dealAcq.setAcquireStatus(ServiceFactory.get().getTaloolService()
					.getAcquireStatus(AcquireStatusType.PENDING_ACCEPT_CUSTOMER_SHARE));

			dealAcq.setSharedByCusomer(dealAcquire.getCustomer());
			dealAcq.setCustomer(toCustomer);
			dealAcq.incrementShareCount();
			daoDispatcher.save(dealAcq);

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem giveDealToCustomer %s %s", dealAcquire,
					toCustomer), ex);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void acceptDeal(final DealAcquire dealAcquire, final UUID customerId)
			throws ServiceException
	{
		// TODO apply state change logic. only accept deals in valid states to be
		// accepted

		if (dealAcquire.getAcquireStatus().getStatus().equals(AcquireStatusType.REDEEMED))
		{
			throw new ServiceException("Cannot acceptDeal an already redeemed deal " + dealAcquire);
		}
		try
		{
			final DealAcquireImpl dealAcq = (DealAcquireImpl) dealAcquire;

			dealAcq.setAcquireStatus(ServiceFactory.get().getTaloolService()
					.getAcquireStatus(AcquireStatusType.ACCEPTED_CUSTOMER_SHARE));

			daoDispatcher.save(dealAcq);

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem acceptDeal %s %s", dealAcquire), ex);
		}

	}

	@Override
	/**
	 * Current only supports rejecting deals given by customers (not merchants)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectDeal(final DealAcquire dealAcquire, final UUID customerId)
			throws ServiceException
	{
		// TODO apply state change logic. only reject deals in valid states to be
		// accepted

		if (dealAcquire.getAcquireStatus().getStatus().equals(AcquireStatusType.REDEEMED))
		{
			throw new ServiceException("Cannot rejectDeal an already redeemed deal " + dealAcquire);
		}

		if (!dealAcquire.getCustomer().getId().equals(customerId))
		{
			throw new ServiceException(ServiceException.Type.CUSTOMER_DOES_NOT_OWN_DEAL,
					"Customer does not own deal");
		}

		try
		{
			final DealAcquireImpl dealAcq = (DealAcquireImpl) dealAcquire;

			// give it back to the original share
			final Customer rejectedBy = dealAcquire.getCustomer();
			dealAcq.setCustomer(dealAcquire.getSharedByCustomer());
			dealAcq.setSharedByCusomer(rejectedBy);

			daoDispatcher.save(dealAcquire);

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem acceptDeal %s %s", dealAcquire), ex);
		}

	}

	@Override
	public void redeemDeal(final DealAcquire dealAcquire, final UUID customerId)
			throws ServiceException
	{
		final DealAcquireImpl dealAcq = (DealAcquireImpl) dealAcquire;

		if (dealAcq.getAcquireStatus().getStatus().equals(AcquireStatusType.REDEEMED))
		{
			throw new ServiceException("Cannot redeem already redeemed deal " + dealAcquire);
		}
		if (!dealAcquire.getCustomer().getId().equals(customerId))
		{
			throw new ServiceException(ServiceException.Type.CUSTOMER_DOES_NOT_OWN_DEAL,
					"Customer does not own deal");
		}

		try
		{
			dealAcq.setAcquireStatus(ServiceFactory.get().getTaloolService()
					.getAcquireStatus(AcquireStatusType.REDEEMED));

			daoDispatcher.save(dealAcq);

		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem redeemDeal %s", ex);
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
	public DealAcquire getDealAcquire(final UUID dealAcquireId) throws ServiceException
	{
		try
		{
			return daoDispatcher.find(DealAcquireImpl.class, dealAcquireId);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealAcquire %s", dealAcquireId), ex);
		}

	}

	@Override
	public void evict(Object obj) throws ServiceException
	{
		try
		{
			getSessionFactory().getCurrentSession().evict(obj);

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem evicting %s", obj), ex);
		}

	}

	static String getQueryWithOrder(final String firstLevelName, final String query,
			final SearchOptions searchOpts)
	{
		if (searchOpts == null)
		{
			return query;
		}

		if (searchOpts.getSortProperty() == null)
		{
			return query;
		}

		final StringBuilder sb = new StringBuilder();
		sb.append(query);
		sb.append(" order by ");

		if (firstLevelName != null)
		{
			sb.append(firstLevelName).append(".");
		}

		sb.append(searchOpts.getSortProperty());

		if (searchOpts.getSortType() == SortType.Asc)
		{
			sb.append(" ASC");
		}
		else
		{
			sb.append(" DESC");
		}

		return sb.toString();
	}

	static String decorateQuery(final String firstLevelName, final String query,
			final SearchOptions searchOpts)
	{
		return searchOpts != null && searchOpts.getSortProperty() != null ? getQueryWithOrder(
				firstLevelName, query, searchOpts) : query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealAcquire> getDealAcquires(final UUID customerId, final UUID merchantId,
			final SearchOptions searchOpts) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory.getCurrentSession().createQuery(
					decorateQuery("dealAcquire", GET_DEAL_ACQUIRES, searchOpts));

			query.setParameter("customerId", customerId);
			query.setParameter("merchantId", merchantId);

			if (searchOpts != null)
			{
				query.setMaxResults(searchOpts.getMaxResults());
				query.setFirstResult(searchOpts.getMaxResults() * searchOpts.getPage());
			}

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
			final Query query = sessionFactory.getCurrentSession().createQuery(
					decorateQuery("merchant", GET_MERCHANT_ACQUIRES, searchOpts));

			query.setParameter("customerId", customerId);

			if (searchOpts != null)
			{
				query.setMaxResults(searchOpts.getMaxResults());
				query.setFirstResult(searchOpts.getMaxResults() * searchOpts.getPage());

			}

			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getMerchantAcquires customerId %s",
					customerId), ex);
		}
	}
}
