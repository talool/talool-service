package com.talool.persistence;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Address;
import com.talool.core.Customer;
import com.talool.domain.CustomerImpl;

/**
 * concider http://code.google.com/p/hibernate-generic-dao/
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
	public void saveCustomer(Customer customer) throws DaoException
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
	public void saveAddress(Address address) throws DaoException
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

	@Override
	public Customer authCustomer(String email, String password) throws DaoException
	{
		final Criteria criteria = getCurrentSession().createCriteria(CustomerImpl.class);

		criteria.add(Restrictions.eq("email", email)).add(Restrictions.eq("password", password));

		return (Customer) criteria.uniqueResult();
	}

}
