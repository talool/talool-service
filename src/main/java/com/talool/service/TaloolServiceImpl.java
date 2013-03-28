package com.talool.service;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.hibernate.DAODispatcher;
import com.googlecode.genericdao.search.Search;
import com.talool.core.AccountType;
import com.talool.core.Customer;
import com.talool.core.DealBook;
import com.talool.core.DealBookContent;
import com.talool.core.DealBookPurchase;
import com.talool.core.Identifiable;
import com.talool.core.Merchant;
import com.talool.core.MerchantDeal;
import com.talool.core.SocialNetwork;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.domain.CustomerImpl;
import com.talool.domain.DealBookContentImpl;
import com.talool.domain.DealBookImpl;
import com.talool.domain.DealBookPurchaseImpl;
import com.talool.domain.MerchantDealImpl;
import com.talool.domain.MerchantImpl;
import com.talool.domain.SocialNetworkImpl;

/**
 * Implementation of the TaloolService
 * 
 * 
 * @author clintz
 */
@Transactional(readOnly = true)
@Service
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
				((MerchantImpl) (account)).setPassword(md5pass);
				save((MerchantImpl) account);
				// daoDispatcher.flush(MerchantImpl.class);
				// daoDispatcher.refresh((MerchantImpl) account);
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
	public void createAccount(final Merchant merchant, final String password) throws ServiceException
	{
		createAccount(AccountType.MER, merchant, password);
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

	@Override
	public Merchant getMerchantByEmail(final String email) throws ServiceException
	{
		Merchant merchant = null;

		try
		{
			final Search search = new Search(MerchantImpl.class);
			search.addFilterEqual("email", email);
			merchant = (Merchant) daoDispatcher.searchUnique(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getMerchantByEmail  " + email, ex);
		}

		return merchant;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final DealBook dealBook) throws ServiceException
	{
		try
		{
			daoDispatcher.save((DealBookImpl) dealBook);
			daoDispatcher.flush(DealBookImpl.class);
			daoDispatcher.refresh(dealBook);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving dealBook " + dealBook;
			throw new ServiceException(err, e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealBook> getDealBooksByEmail(final String email) throws ServiceException
	{
		List<DealBook> dealBooks = null;

		try
		{
			final Search search = new Search();
			search.setSearchClass(DealBookImpl.class);
			search.addFilterEqual("merchant.email", email);
			dealBooks = daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getDealBooksByEmail  " + email, ex);
		}

		return dealBooks;
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

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteDealBook(final Long id) throws ServiceException
	{
		removeElement(id, DealBookImpl.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MerchantDeal> getMerchantDeals(final Long merchantId, final Boolean isActive)
			throws ServiceException
	{
		List<MerchantDeal> merchantDeals = null;

		try
		{
			final Search search = new Search(MerchantDealImpl.class);
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
	public void save(final MerchantDeal merchantDeal) throws ServiceException
	{
		try
		{
			daoDispatcher.save((MerchantDealImpl) merchantDeal);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving merchantDeal " + merchantDeal;
			throw new ServiceException(err, e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteMerchantDeal(final Long id) throws ServiceException
	{
		removeElement(id, MerchantDealImpl.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final DealBookContent dealBookContent) throws ServiceException
	{
		try
		{
			daoDispatcher.save((DealBookContentImpl) dealBookContent);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving dealBookContent " + dealBookContent;
			throw new ServiceException(err, e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteDealBookContent(final Long id) throws ServiceException
	{
		removeElement(id, DealBookContentImpl.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final DealBookPurchase dealBookPurchase) throws ServiceException
	{
		try
		{
			daoDispatcher.save((DealBookPurchaseImpl) dealBookPurchase);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving dealBookPurchase " + dealBookPurchase;
			throw new ServiceException(err, e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealBookPurchase> getPurchases(final AccountType accountType, final Long accountId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(DealBookPurchaseImpl.class);
			search.addFilterEqual(
					accountType == AccountType.CUS ? "customer.id" : "dealBook.merchant.id", accountId);

			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealBookPurchasesById %s %d ",
					accountType, accountId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealBookPurchase> getPurchasesByDealBookId(final Long dealBookId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(DealBookPurchaseImpl.class);
			search.addFilterEqual("dealBook.id", dealBookId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getPurchasesByDealBookId %s", dealBookId),
					ex);
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
	public List<MerchantDeal> getDealsByMerchantId(final Long merchantId) throws ServiceException
	{
		try
		{
			final Search search = new Search(MerchantDealImpl.class);
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
	public List<MerchantDeal> getDealsByCustomerId(final Long accountId) throws ServiceException
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
}
