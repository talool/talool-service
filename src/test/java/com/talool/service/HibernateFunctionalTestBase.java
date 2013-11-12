package com.talool.service;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.talool.core.FactoryManager;
import com.talool.core.service.CustomerService;
import com.talool.core.service.TaloolService;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@ContextConfiguration("classpath:./taloolService-test.xml")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
/**
 * Before/After behave like OpenSessionInViewFilter
 * @author clintz
 *
 */
public abstract class HibernateFunctionalTestBase extends AbstractJUnit4SpringContextTests
{
	@Autowired
	protected FactoryManager serviceFactory;

	@Autowired
	protected TaloolService taloolService;

	@Autowired
	protected CustomerService customerService;

	@Before
	public void setUp()
	{
		// open a Hibernate session
		final SessionFactory sessionFactory = (SessionFactory) applicationContext
				.getBean("sessionFactory");

		final BasicDataSource dataSource = (BasicDataSource) applicationContext
				.getBean("dataSource");

		logger.info("DataSource " + dataSource.getUrl());

		final Session session = sessionFactory.openSession();
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));

		FlushMode flushMode = session.getFlushMode();
		logger.info("FlushMode " + flushMode);
	}

	@After
	public void tearDown()
	{
		final SessionFactory sessionFactory = (SessionFactory) applicationContext
				.getBean("sessionFactory");
		SessionHolder holder = (SessionHolder) TransactionSynchronizationManager
				.getResource(sessionFactory);
		Session sess = holder.getSession();

		sess.flush();

		// SessionFactoryUtils.closeSession(sess);

		// sessionFactory.close();

		TransactionSynchronizationManager.unbindResource(sessionFactory);

	}

}
