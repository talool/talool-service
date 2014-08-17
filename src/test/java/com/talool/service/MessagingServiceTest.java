package com.talool.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DomainFactory;
import com.talool.core.FactoryManager;
import com.talool.core.MerchantAccount;
import com.talool.core.service.InvalidInputException;
import com.talool.core.service.ServiceException;
import com.talool.messaging.MessagingFactory;
import com.talool.messaging.job.MerchantGiftJob;

@TestExecutionListeners(TransactionalTestExecutionListener.class)
// Rolls back transactions by default
public class MessagingServiceTest extends HibernateFunctionalTestBase
{

	private static final Logger LOG = LoggerFactory.getLogger(MessagingServiceTest.class);

	private DomainFactory domainFactory;

	@Before
	public void setup()
	{
		domainFactory = FactoryManager.get().getDomainFactory();
	}

	@Test
	public void testSchedulingJob() throws ServiceException, InvalidInputException
	{
		try
		{
			List<Customer> targetedCustomers = new ArrayList<Customer>();
			targetedCustomers.add(customerService.getCustomerByEmail("douglasmccuen@yahoo.com"));
			targetedCustomers.add(customerService.getCustomerByEmail("doug@talool.com"));
			targetedCustomers.add(customerService.getCustomerByEmail("christopher.justin@gmail.com"));
			targetedCustomers.add(customerService.getCustomerByEmail("cory@talool.com"));

			Customer fromCustomer = customerService.getCustomerByEmail("chris@talool.com");

			Deal deal = taloolService.getDeal(UUID.fromString("5a2f1b65-53f6-4db7-9a66-5dbdabf2f932"));

			MerchantAccount merchantAccount = taloolService.getMerchantAccountById(2l); // chris@talool.com

			MerchantGiftJob job = MessagingFactory.newMerchantGiftJob(merchantAccount, fromCustomer, deal, new Date(), "some job notes");

			ServiceFactory.get().getMessagingService().scheduleMessagingJob(job, targetedCustomers);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Test
	public void testJobManager() throws ServiceException, InvalidInputException
	{

		LOG.info("waiting");
	}
}