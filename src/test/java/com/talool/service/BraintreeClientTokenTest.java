package com.talool.service;

import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.talool.payment.braintree.BraintreeUtil;

/**
 * 
 * @author dmccuen
 * 
 */
public class BraintreeClientTokenTest
{

	@BeforeClass
	public static void init()
	{
		ServiceConfig.createInstance("service.properties");
	}
	
	@Test
	public void testGenerateClientToken()
	{
		UUID customerId = UUID.randomUUID();
		String token = BraintreeUtil.get().generateClientToken(customerId);
		Assert.assertNotNull(token);

	}
}
