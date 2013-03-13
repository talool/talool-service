package com.talool.persistence;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Address;
import com.talool.core.Customer;
import com.talool.core.SocialNetwork;
import com.talool.domain.CustomerImpl;
import com.talool.domain.SocialNetworkImpl;

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
	public Customer getCustomerByEmail(final String email) throws DaoException
	{
		final Criteria criteria = getCurrentSession().createCriteria(CustomerImpl.class);
		criteria.add(Restrictions.eq("email", email));
		return (Customer) criteria.uniqueResult();
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
	public void save(Customer customer) throws DaoException
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
	public void save(Address address) throws DaoException
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
	public Customer authenticateCustomer(String email, String password) throws DaoException
	{
		final Criteria criteria = getCurrentSession().createCriteria(CustomerImpl.class);
		criteria.add(Restrictions.eq("email", email)).add(Restrictions.eq("password", password));
		return (Customer) criteria.uniqueResult();
	}

	@Override
	public Customer getCustomerById(final Long id) throws DaoException
	{
		try
		{
			return (Customer) getCurrentSession().load(CustomerImpl.class, id);
		}
		catch (Exception e)
		{
			throw new DaoException("Problem getting customerById", e);
		}

	}

	@Override
	public SocialNetwork getSocialNetwork(final String name) throws DaoException
	{
		final Criteria criteria = getCurrentSession().createCriteria(SocialNetworkImpl.class);
		criteria.add(Restrictions.eq("name", name));
		return (SocialNetwork) criteria.uniqueResult();
	}
}
