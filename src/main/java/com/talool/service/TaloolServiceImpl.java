package com.talool.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.hibernate.DAODispatcher;
import com.googlecode.genericdao.search.Search;
import com.talool.core.AccountType;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.FactoryManager;
import com.talool.core.Identifiable;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.SocialNetwork;
import com.talool.core.Tag;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.domain.CustomerImpl;
import com.talool.domain.DealImpl;
import com.talool.domain.DealOfferImpl;
import com.talool.domain.DealOfferPurchaseImpl;
import com.talool.domain.MerchantAccountImpl;
import com.talool.domain.MerchantImpl;
import com.talool.domain.SocialNetworkImpl;
import com.talool.domain.TagImpl;

/**
 * Implementation of the TaloolService
 * 
 * 
 * @author clintz
 */
@Transactional(readOnly = true)
@Service
@Repository
public class TaloolServiceImpl implements TaloolService
{
	public SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceImpl.class);

	private DAODispatcher daoDispatcher;
	private SessionFactory sessionFactory;

	public TaloolServiceImpl()
	{}

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
	public Customer getCustomerById(final Long id) throws ServiceException
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
	public void deleteCustomer(final Long id) throws ServiceException
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
			final Long id = (Long) daoDispatcher.searchUnique(search);
			return id == null ? false : true;
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem emailExists  " + email, ex);
		}
	}

	private void createAccount(final AccountType accountType, final Identifiable account,
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

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteMerchant(final Long id) throws ServiceException
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
	public Merchant getMerchantById(Long id) throws ServiceException
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

	private void removeElement(final Long id, Class clazz) throws ServiceException
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
	public List<Deal> getMerchantDeals(final Long merchantId, final Boolean isActive)
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
	public List<Deal> getDealsByMerchantId(final Long merchantId) throws ServiceException
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
	public List<Deal> getDealsByCustomerId(final Long accountId) throws ServiceException
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
			throw new ServiceException(String.format("Problem getDealsByCustomerId %d", accountId), ex);
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
	public MerchantAccount authenticateMerchantAccount(final Long merchantId, final String email,
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
			throw new ServiceException(String.format("Problem authenticateMerchantAccount %d %d",
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
	public DealOffer getDealOffer(final Long dealOfferId) throws ServiceException
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
	public Deal getDeal(final Long dealId) throws ServiceException
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
	public List<Deal> getDealsByDealOfferId(final Long dealOfferId) throws ServiceException
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
	public List<DealOfferPurchase> getDealOfferPurchasesByCustomerId(final Long customerId)
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
	public List<DealOfferPurchase> getDealOfferPurchasesByDealOfferId(final Long dealOfferId)
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
	public Set<Tag> getDealOfferTags(final Long dealOfferId) throws ServiceException
	{
		Set<Tag> tags = null;

		try
		{
			final Query query = getSessionFactory()
					.getCurrentSession()
					.createSQLQuery(
							"select distinct t.* from tag as t, deal_tag as dt,deal_offer as dof, deal as d where t.tag_id=dt.tag_id and dof.deal_offer_id=d.deal_offer_id and d.deal_offer_id=:dealOfferId and d.deal_id=dt.deal_id")
					.addEntity(TagImpl.class);

			query.setParameter("dealOfferId", dealOfferId);

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
	public List<Customer> getFriends(Long id) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory
					.getCurrentSession()
					.createQuery(
							"from CustomerImpl c, RelationshipImpl r where c.customerId=r.customerId and r.friendId=:customerId");

			query.setParameter("customerId", id);
			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getFriends %d", id), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealOffer> getDealOffersByMerchantId(Long merchantId) throws ServiceException
	{
		try
		{
			final Search search = new Search(DealOfferImpl.class);
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

}
