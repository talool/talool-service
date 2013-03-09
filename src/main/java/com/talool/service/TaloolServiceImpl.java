package com.talool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.talool.core.Address;
import com.talool.core.Customer;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.entity.AddressImpl;
import com.talool.entity.CustomerImpl;
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
	public void registerCustomer(Customer customer, String password) throws ServiceException
	{
		try
		{
			final String md5pass = EncryptService.MD5(password);

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Encyrpting password: " + md5pass);
			}
			customer.setPassword(md5pass);
			saveCustomer(customer);
		}
		catch (Exception e)
		{
			final String err = "There was a problem registering customer " + customer;
			LOG.error(err, e);
			throw new ServiceException(err, e);
		}

	}

	@Override
	public void saveCustomer(Customer customer) throws ServiceException
	{
		try
		{
			taloolDao.saveCustomer(customer);
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
	public Customer authCustomer(String email, String password) throws ServiceException
	{
		Customer customer;
		try
		{
			customer = taloolDao.authCustomer(email, EncryptService.MD5(password));
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem authentication customer " + email, ex);
		}

		return customer;
	}

}
