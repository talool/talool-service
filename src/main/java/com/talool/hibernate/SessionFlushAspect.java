package com.talool.hibernate;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

/**
 * Hibernate session flush aspect because Hibernate4 doesnt act like Hibernate3
 * 
 * http://koenserneels.blogspot.com/2012/05/migrating-from-hibernate-3-to-4-with
 * .html
 * 
 * 
 * @author clintz
 */
@Aspect
public class SessionFlushAspect implements Ordered
{
	private static final Logger LOG = LoggerFactory.getLogger(SessionFlushAspect.class);

	private SessionFactory sessionFactory;
	private int order;

	@After("execution(* com.talool.service.TaloolServiceImpl.*(..))")
	public void flushSession(final JoinPoint joinPoint)
	{
		final Session session = sessionFactory.getCurrentSession();
		if (session != null && session.isDirty())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Flushing open session");
			}
			session.flush();

		}
		else
		{
			LOG.debug("Not Flushing session, not open");
		}

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
	public int getOrder()
	{
		return order;
	}

	public void setOrder(int order)
	{
		this.order = order;
	}

}