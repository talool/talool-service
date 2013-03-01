package com.talool.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Customer;
import com.talool.entity.AddressImpl;
import com.talool.entity.CustomerImpl;

/**
 * 
 * 
 * @author clintz
 */
public class TaloolDaoImpl implements TaloolDao
{
	private static final Logger LOG = LoggerFactory.getLogger(TaloolDaoImpl.class);

	private SessionFactory sessionFactory;

	protected Session getCurrentSession()
	{
		return sessionFactory.getCurrentSession();
	}

	@Override
	public Customer getCustomer(final String email) throws DaoException
	{
		return null;
	}

	public SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Customer getCustomer(String email, String password) throws DaoException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveCustomer(CustomerImpl customer) throws DaoException
	{
		try
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Saving customer " + customer);
			}
			getCurrentSession().saveOrUpdate(customer);
		}
		catch (Exception ex)
		{
			throw new DaoException("Problem saving customer", ex);
		}

	}

	@Override
	public void saveAddress(AddressImpl address) throws DaoException
	{

		try
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Saving address " + address);
			}

			getCurrentSession().saveOrUpdate(address);
			// getCurrentSession().flush();
		}
		catch (Exception ex)
		{
			throw new DaoException("Problem saving address", ex);
		}

	}

}
