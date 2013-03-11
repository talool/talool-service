package com.talool;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.talool.core.service.ServiceException;
import com.talool.service.ServiceFactory;

/**
 * Unit test for simple App.
 */
public class DBTest
{
	private final AbstractApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-test.xml");

	@Test
	public void testDb1()
	{
		SessionFactory sf = (SessionFactory) context.getBean("sessionFactory");
		Session sesssion = sf.openSession();

		try
		{
			ServiceFactory.get().getTaloolService().authCustomer("christopher5.justin@gmail.com", "pass123");
		}
		catch (ServiceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Got here!");
	}

}
