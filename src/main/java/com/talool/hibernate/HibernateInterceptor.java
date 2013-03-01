/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talool.hibernate;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * This interceptor binds a new Hibernate Session to the thread before a method
 * call, closing and removing it afterwards in case of any method outcome. If
 * there already is a pre-bound Session (e.g. from HibernateTransactionManager,
 * or from a surrounding Hibernate-intercepted method), the interceptor simply
 * participates in it.
 * 
 * <p>
 * Application code must retrieve a Hibernate Session via the
 * <code>SessionFactoryUtils.getSession</code> method or - preferably -
 * Hibernate's own <code>SessionFactory.getCurrentSession()</code> method, to be
 * able to detect a thread-bound Session. Typically, the code will look like as
 * follows:
 * 
 * <pre>
 * public void doSomeDataAccessAction() {
 *   Session session = this.sessionFactory.getCurrentSession();
 *   ...
 *   // No need to close the Session or translate exceptions!
 * }
 * </pre>
 * 
 * Note that this interceptor automatically translates HibernateExceptions, via
 * delegating to the
 * <code>SessionFactoryUtils.convertHibernateAccessException</code> method that
 * converts them to exceptions that are compatible with the
 * <code>org.springframework.dao</code> exception hierarchy (like
 * HibernateTemplate does). This can be turned off if the raw exceptions are
 * preferred.
 * 
 * <p>
 * This class can be considered a declarative alternative to HibernateTemplate's
 * callback approach. The advantages are:
 * <ul>
 * <li>no anonymous classes necessary for callback implementations;
 * <li>the possibility to throw any application exceptions from within data
 * access code.
 * </ul>
 * 
 * <p>
 * The drawback is the dependency on interceptor configuration. However, note
 * that this interceptor is usually <i>not</i> necessary in scenarios where the
 * data access code always executes within transactions. A transaction will
 * always have a thread-bound Session in the first place, so adding this
 * interceptor to the configuration just adds value when fine-tuning Session
 * settings like the flush mode - or when relying on exception translation.
 * 
 * <p>
 * Note well this is a version adapted from the existing HibernateInterceptor
 * with no support for Hibernate Filters as yet, unlike the Hibernate 3.x
 * versions of Spring's Hibernate Interceptor
 * 
 * @author Juergen Hoeller
 * @author Andres Olave
 * 
 * @see org.hibernate.SessionFactory#getCurrentSession()
 * @see HibernateTransactionManager
 * @see HibernateTemplate
 */
public class HibernateInterceptor implements MethodInterceptor
{

	protected final Log logger = LogFactory.getLog(getClass());

	private boolean exceptionConversionEnabled = true;
	/**
	 * Never flush is a good strategy for read-only units of work. Hibernate will
	 * not track and look for changes in this case, avoiding any overhead of
	 * modification detection.
	 * <p>
	 * In case of an existing Session, FLUSH_NEVER will turn the flush mode to
	 * NEVER for the scope of the current operation, resetting the previous flush
	 * mode afterwards.
	 * 
	 * @see #setFlushMode
	 */
	public static final int FLUSH_NEVER = 0;

	/**
	 * Automatic flushing is the default mode for a Hibernate Session. A session
	 * will get flushed on transaction commit, and on certain find operations that
	 * might involve already modified instances, but not after each unit of work
	 * like with eager flushing.
	 * <p>
	 * In case of an existing Session, FLUSH_AUTO will participate in the existing
	 * flush mode, not modifying it for the current operation. This in particular
	 * means that this setting will not modify an existing flush mode NEVER, in
	 * contrast to FLUSH_EAGER.
	 * 
	 * @see #setFlushMode
	 */
	public static final int FLUSH_AUTO = 1;

	/**
	 * Eager flushing leads to immediate synchronization with the database, even
	 * if in a transaction. This causes inconsistencies to show up and throw a
	 * respective exception immediately, and JDBC access code that participates in
	 * the same transaction will see the changes as the database is already aware
	 * of them then. But the drawbacks are:
	 * <ul>
	 * <li>additional communication roundtrips with the database, instead of a
	 * single batch at transaction commit;
	 * <li>the fact that an actual database rollback is needed if the Hibernate
	 * transaction rolls back (due to already submitted SQL statements).
	 * </ul>
	 * <p>
	 * In case of an existing Session, FLUSH_EAGER will turn the flush mode to
	 * AUTO for the scope of the current operation and issue a flush at the end,
	 * resetting the previous flush mode afterwards.
	 * 
	 * @see #setFlushMode
	 */
	public static final int FLUSH_EAGER = 2;

	/**
	 * Flushing at commit only is intended for units of work where no intermediate
	 * flushing is desired, not even for find operations that might involve
	 * already modified instances.
	 * <p>
	 * In case of an existing Session, FLUSH_COMMIT will turn the flush mode to
	 * COMMIT for the scope of the current operation, resetting the previous flush
	 * mode afterwards. The only exception is an existing flush mode NEVER, which
	 * will not be modified through this setting.
	 * 
	 * @see #setFlushMode
	 */
	public static final int FLUSH_COMMIT = 3;

	/**
	 * Flushing before every query statement is rarely necessary. It is only
	 * available for special needs.
	 * <p>
	 * In case of an existing Session, FLUSH_ALWAYS will turn the flush mode to
	 * ALWAYS for the scope of the current operation, resetting the previous flush
	 * mode afterwards.
	 * 
	 * @see #setFlushMode
	 */
	public static final int FLUSH_ALWAYS = 4;

	private int flushMode = FLUSH_AUTO;

	private SessionFactory sessionFactory;

	public int getFlushMode()
	{
		return flushMode;
	}

	public void setFlushMode(int flushMode)
	{
		this.flushMode = flushMode;
	}

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory()
	{
		return this.sessionFactory;
	}

	/**
	 * Set whether to convert any HibernateException raised to a Spring
	 * DataAccessException, compatible with the
	 * <code>org.springframework.dao</code> exception hierarchy.
	 * <p>
	 * Default is "true". Turn this flag off to let the caller receive raw
	 * exceptions as-is, without any wrapping.
	 * 
	 * @see org.springframework.dao.DataAccessException
	 */
	public void setExceptionConversionEnabled(boolean exceptionConversionEnabled)
	{
		this.exceptionConversionEnabled = exceptionConversionEnabled;
	}

	public Object invoke(MethodInvocation methodInvocation) throws Throwable
	{
		SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager
				.getResource(getSessionFactory());

		boolean existingTransaction = sessionHolder != null;

		Session session;

		if (existingTransaction)
		{
			logger.error("Found thread-bound Session for HibernateInterceptor");
			session = sessionHolder.getSession();
		}
		else
		{
			session = openSession();

			TransactionSynchronizationManager.bindResource(getSessionFactory(),
					new SessionHolder(session));
		}

		FlushMode previousFlushMode = null;
		try
		{
			previousFlushMode = applyFlushMode(session, existingTransaction);
			// enableFilters(session);
			Object retVal = methodInvocation.proceed();
			flushIfNecessary(session, existingTransaction);
			return retVal;
		}
		catch (HibernateException ex)
		{
			if (this.exceptionConversionEnabled)
			{
				throw SessionFactoryUtils.convertHibernateAccessException(ex);
			}
			else
			{
				throw ex;
			}
		}
		finally
		{
			if (existingTransaction)
			{
				logger.debug("Not closing pre-bound Hibernate Session after HibernateInterceptor");
				// disableFilters(session);
				if (previousFlushMode != null)
				{
					session.setFlushMode(previousFlushMode);
				}
			}
			else
			{
				SessionFactoryUtils.closeSession(session);
				if (sessionHolder == null)
				{
					TransactionSynchronizationManager.unbindResource(getSessionFactory());
				}
			}
		}
	}

	/**
	 * Flush the given Hibernate Session if necessary.
	 * 
	 * @param session
	 *          the current Hibernate Session
	 * @param existingTransaction
	 *          if executing within an existing transaction
	 * @throws HibernateException
	 *           in case of Hibernate flushing errors
	 */
	protected void flushIfNecessary(Session session, boolean existingTransaction)
			throws HibernateException
	{
		if (getFlushMode() == FLUSH_EAGER || (!existingTransaction && getFlushMode() != FLUSH_NEVER))
		{
			logger.debug("Eagerly flushing Hibernate session");
			session.flush();
		}
	}

	/**
	 * Open a Session for the SessionFactory that this interceptor uses.
	 * <p>
	 * The default implementation delegates to the
	 * <code>SessionFactory.openSession</code> method and sets the
	 * <code>Session</code>'s flush mode to "MANUAL".
	 * 
	 * @return the Session to use
	 * @throws DataAccessResourceFailureException
	 *           if the Session could not be created
	 * @see org.hibernate.FlushMode#MANUAL
	 */
	protected Session openSession() throws DataAccessResourceFailureException
	{
		try
		{
			Session session = SessionFactoryUtils.openSession(getSessionFactory());
			session.setFlushMode(FlushMode.MANUAL);
			return session;
		}
		catch (HibernateException ex)
		{
			throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex);
		}
	}

	/**
	 * Apply the flush mode that's been specified for this accessor to the given
	 * Session.
	 * 
	 * @param session
	 *          the current Hibernate Session
	 * @param existingTransaction
	 *          if executing within an existing transaction
	 * @return the previous flush mode to restore after the operation, or
	 *         <code>null</code> if none
	 * @see #setFlushMode
	 * @see org.hibernate.Session#setFlushMode
	 */
	protected FlushMode applyFlushMode(Session session, boolean existingTransaction)
	{
		if (getFlushMode() == FLUSH_NEVER)
		{
			if (existingTransaction)
			{
				FlushMode previousFlushMode = session.getFlushMode();
				if (!previousFlushMode.lessThan(FlushMode.COMMIT))
				{
					session.setFlushMode(FlushMode.MANUAL);
					return previousFlushMode;
				}
			}
			else
			{
				session.setFlushMode(FlushMode.MANUAL);
			}
		}
		else if (getFlushMode() == FLUSH_EAGER)
		{
			if (existingTransaction)
			{
				FlushMode previousFlushMode = session.getFlushMode();
				if (!previousFlushMode.equals(FlushMode.AUTO))
				{
					session.setFlushMode(FlushMode.AUTO);
					return previousFlushMode;
				}
			}
			else
			{
				// rely on default FlushMode.AUTO
			}
		}
		else if (getFlushMode() == FLUSH_COMMIT)
		{
			if (existingTransaction)
			{
				FlushMode previousFlushMode = session.getFlushMode();
				if (previousFlushMode.equals(FlushMode.AUTO) || previousFlushMode.equals(FlushMode.ALWAYS))
				{
					session.setFlushMode(FlushMode.COMMIT);
					return previousFlushMode;
				}
			}
			else
			{
				session.setFlushMode(FlushMode.COMMIT);
			}
		}
		else if (getFlushMode() == FLUSH_ALWAYS)
		{
			if (existingTransaction)
			{
				FlushMode previousFlushMode = session.getFlushMode();
				if (!previousFlushMode.equals(FlushMode.ALWAYS))
				{
					session.setFlushMode(FlushMode.ALWAYS);
					return previousFlushMode;
				}
			}
			else
			{
				session.setFlushMode(FlushMode.ALWAYS);
			}
		}
		return null;
	}

}
