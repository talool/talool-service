package com.talool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import com.talool.persistence.DaoException;
import com.talool.persistence.TaloolDao;

/**
 * Implementation of the TaloolService
 * 
 * 
 * @author clintz
 */
@Transactional(readOnly = true)
public class TaloolServiceImpl implements TaloolService
{
	private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceImpl.class);

	private TaloolDao taloolDao;

	public TaloolServiceImpl()
	{}

	public TaloolDao getTaloolDao()
	{
		return taloolDao;
	}

	public void setTaloolDao(TaloolDao taloolDao)
	{
		this.taloolDao = taloolDao;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void createAccount(Customer customer, String password) throws ServiceException
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
		}
		catch (Exception e)
		{
			final String err = "There was a problem registering customer " + customer;
			LOG.error(err, e);
			throw new ServiceException(err, e);
		}

	}

	@Override
	public void save(Customer customer) throws ServiceException
	{
		try
		{
			taloolDao.save(customer);
		}
		catch (DaoException e)
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
	public Customer authenticateCustomer(String email, String password) throws ServiceException
	{
		Customer customer;
		try
		{
			customer = taloolDao.authenticateCustomer(email, EncryptService.MD5(password));
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem authentication customer " + email, ex);
		}

		return customer;
	}

	@Override
	public Customer getCustomerById(final Long id) throws ServiceException
	{
		Customer customer;
		try
		{
			customer = taloolDao.getCustomerById(id);
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
		Customer customer;
		try
		{
			customer = taloolDao.getCustomerByEmail(email);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getCustomerByEmail  " + email, ex);
		}

		return customer;
	}

	@Override
	public SocialAccount newSocialAccount(final String socialNetworkName, final AccountType accountType)
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
			snet = taloolDao.getSocialNetwork(name);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getSocialNetwork  " + name, ex);
		}

		return snet;
	}

}
