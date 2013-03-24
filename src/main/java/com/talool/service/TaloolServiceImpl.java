package com.talool.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.hibernate.DAODispatcher;
import com.googlecode.genericdao.search.Search;
import com.talool.core.AccountType;
import com.talool.core.Address;
import com.talool.core.Customer;
import com.talool.core.DealBook;
import com.talool.core.Identifiable;
import com.talool.core.Merchant;
import com.talool.core.SocialAccount;
import com.talool.core.SocialNetwork;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.domain.AddressImpl;
import com.talool.domain.CustomerImpl;
import com.talool.domain.DealBookImpl;
import com.talool.domain.MerchantImpl;
import com.talool.domain.SocialAccountImpl;
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
	private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceImpl.class);

	private DAODispatcher daoDispatcher;

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
	public Address newAddress()
	{
		return new AddressImpl();
	}

	@Override
	public Customer newCustomer()
	{
		return new CustomerImpl();
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
	public SocialAccount newSocialAccount(final String socialNetworkName,
			final AccountType accountType)
	{
		try
		{
			return new SocialAccountImpl(getSocialNetwork(socialNetworkName), accountType);
		}
		catch (ServiceException e)
		{
			LOG.error("Problem getSocialNetwork " + socialNetworkName, e);
		}

		return new SocialAccountImpl();
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
		try
		{
			daoDispatcher.removeById(CustomerImpl.class, id);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem deleteing customer", ex);
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

	@Override
	public Merchant newMerchant()
	{
		return new MerchantImpl();
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
		try
		{
			daoDispatcher.removeById(MerchantImpl.class, id);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem deleteing customer", ex);
		}

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
	public DealBook newDealBook(final Merchant merchant)
	{
		return new DealBookImpl(merchant);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final DealBook dealBook) throws ServiceException
	{
		try
		{
			daoDispatcher.save((DealBookImpl) dealBook);
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
			final Search search = new Search(DealBookImpl.class);
			search.addFilterEqual("merchant.email", email);
			dealBooks = daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getDealBooksByEmail  " + email, ex);
		}

		return dealBooks;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteDealBook(final Long id) throws ServiceException
	{
		try
		{
			daoDispatcher.removeById(DealBookImpl.class, id);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem deleteDealBook", ex);
		}

	}
}
