package com.talool.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.talool.core.Customer;
import com.talool.core.Merchant;
import com.talool.core.service.InvalidInputException;
import com.talool.core.service.ServiceException;
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
}
