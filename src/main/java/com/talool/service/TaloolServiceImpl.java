package com.talool.service;

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
import com.talool.core.SocialAccount;
import com.talool.core.SocialNetwork;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.domain.AddressImpl;
import com.talool.domain.CustomerImpl;
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

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void createAccount(final Customer customer, final String password) throws ServiceException
	{
		try
		{
			final String md5pass = EncryptService.MD5(password);

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Encyrpting password: " + md5pass);
			}
			customer.setPassword(md5pass);
			save(customer);
			daoDispatcher.flush(CustomerImpl.class);
			daoDispatcher.refresh((CustomerImpl) customer);

			LOG.info(customer.toString());

		}
		catch (Exception e)
		{
			final String err = "There was a problem registering customer " + customer;
			LOG.error(err, e);
			throw new ServiceException(err, e);
		}

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
	public boolean customerEmailExists(final String email) throws ServiceException
	{
		try
		{
			final Search search = new Search(CustomerImpl.class);
			search.addField("id");
			search.addFilterEqual("email", email);
			final Long id = (Long) daoDispatcher.searchUnique(search);
			return id == null ? false : true;
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem customerEmailExists  " + email, ex);
		}
	}

}
