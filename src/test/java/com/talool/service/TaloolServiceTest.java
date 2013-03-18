package com.talool.service;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.talool.core.Customer;

/**
 * Talool Service integration test cases
 * 
 * @author clintz
 * 
 */

@TestExecutionListeners(TransactionalTestExecutionListener.class)
// Rolls back transactions by default
public class TaloolServiceTest extends HibernateFunctionalTestBase
{
	private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceTest.class);

	@Test
	public void createAccountTest() throws Exception
	{
		try
		{
			String email = "christopher.justin-1000@gmail.com";

			Customer cust = taloolService.newCustomer();
			cust.setEmail(email);
			cust.setFirstName("Chris");
			cust.setLastName("Lintz");
			cust.setPassword("pass123");
			cust.setBirthDate(new Date());

			// create account
			taloolService.createAccount(cust, "pass123");

			// authenticate
			Customer cust2 = taloolService.authenticateCustomer(email, "pass123");

			Assert.assertNotNull(cust2.getId());

			// find by email
			boolean emailExists = taloolService.customerEmailExists(email);
			Assert.assertTrue(emailExists);

			// lets delete
			taloolService.deleteCustomer(cust2.getId());

			// assert they are deleted
			emailExists = taloolService.customerEmailExists(email);
			Assert.assertFalse(emailExists);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}

	}

}
