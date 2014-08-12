package com.talool.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.talool.core.Customer;
import com.talool.core.Merchant;
import com.talool.core.Sex;
import com.talool.core.service.InvalidInputException;
import com.talool.core.service.ServiceException;
import com.talool.domain.CustomerCriteria;
import com.talool.domain.MerchantImpl;
import com.talool.utils.KeyValue;

/**
 * 
 * @author dmccuen
 * 
 */
public class MerchantMessageTest extends HibernateFunctionalTestBase
{

	@BeforeClass
	public static void init()
	{
		ServiceConfig.createInstance("service.properties");
	}
	
	@Test
	public void testGetCustomerForMerchant() throws ServiceException, InvalidInputException
	{
		Customer customer;
		
		// Merchant without a customer id
		Merchant merchant = new MerchantImpl();
		merchant.setName("Dummy Merchant");
		
		customer = taloolService.getCustomerForMerchant(merchant);
		Assert.assertNotNull(customer);
		Assert.assertNotNull(customer.getId());
		
		// mock a Merchant without a customer id
		String id = merchant.getProperties().getAsString(KeyValue.merchantCustomerId);
		Assert.assertNotNull(id);
		customer = taloolService.getCustomerForMerchant(merchant);
		Assert.assertNotNull(customer);
		Assert.assertNotNull(customer.getId());
	}
	
	@Test
	public void testGetCustomers() throws ServiceException, InvalidInputException
	{
		List<Customer> customers;
		
		CustomerCriteria cc = new CustomerCriteria();
		
		cc.setSex(Sex.Female);
		customers = customerService.getCustomers(cc);
		Assert.assertNotNull(customers);
		Assert.assertNotEquals(0, customers.size());
		
		cc.setAges(new Date(), null);
		customers = customerService.getCustomers(cc);
		Assert.assertNotNull(customers);
		Assert.assertNotEquals(0, customers.size());
		
		cc.setDealOfferId(UUID.randomUUID());
		customers = customerService.getCustomers(cc);
		Assert.assertNotNull(customers);
		Assert.assertEquals(0, customers.size());
	}
}
