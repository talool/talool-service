package com.talool.service;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.hibernate.DAODispatcher;
import com.talool.core.service.ServiceException;

/**
 * 
 * @author clintz
 * 
 */
public abstract class AbstractHibernateService implements HibernateService
{
	protected DAODispatcher daoDispatcher;
	protected SessionFactory sessionFactory;

	public AbstractHibernateService()
	{}

	public SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void merge(final Object obj) throws ServiceException
	{
		try
		{
			getSessionFactory().getCurrentSession().merge(obj);
		}
		catch (Exception e)
		{
			throw new ServiceException("There was a problem merging", e);
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

	public void reattach(final Object obj) throws ServiceException
	{
		try
		{
			getSessionFactory().getCurrentSession().buildLockRequest(LockOptions.NONE).lock(obj);
		}
		catch (Exception e)
		{
			throw new ServiceException("There was a problem reattaching", e);
		}
	}

	@Override
	public void initialize(final Object obj) throws ServiceException
	{
		try
		{
			Hibernate.initialize(obj);
		}
		catch (HibernateException he)
		{
			throw new ServiceException(he.getLocalizedMessage(), he);
		}
	}

	@Override
	public void isInitialized(final Object obj) throws ServiceException
	{
		try
		{
			Hibernate.isInitialized(obj);
		}
		catch (HibernateException he)
		{
			throw new ServiceException(he.getLocalizedMessage(), he);
		}
	}

	public Session getCurrentSession()
	{
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void evict(Object obj) throws ServiceException
	{
		try
		{
			getSessionFactory().getCurrentSession().evict(obj);

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem evicting %s", obj), ex);
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

}
