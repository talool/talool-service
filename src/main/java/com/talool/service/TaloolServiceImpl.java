package com.talool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.entity.AddressImpl;
import com.talool.entity.CustomerImpl;
import com.talool.persistence.DaoException;
import com.talool.persistence.TaloolDao;
import com.talool.thrift.Address;
import com.talool.thrift.Customer;

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
	public void registerCustomer(CustomerImpl customer, String password) throws ServiceException
	{
		try
		{
			LOG.info("Encyrpting password: " + EncryptService.MD5(password));
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
	public void saveCustomer(CustomerImpl customer) throws ServiceException
	{
		try
		{
			taloolDao.saveCustomer(customer);
			// taloolDao.saveAddress(customer.getAddress());
		}
		catch (DaoException e)
		{
			final String err = "There was a problem saving customer " + customer;
			LOG.error(err, e);
			throw new ServiceException(err, e);
		}

	}

	@Override
	public CustomerImpl newCustomer(Customer customer)
	{
		return new CustomerImpl(customer);
	}

	@Override
	public AddressImpl newAddress()
	{
		return new AddressImpl();
	}

	@Override
	public CustomerImpl newCustomer()
	{
		return new CustomerImpl();
	}

	@Override
	public AddressImpl newAddress(Address address)
	{
		return new AddressImpl(address);
	}

}
