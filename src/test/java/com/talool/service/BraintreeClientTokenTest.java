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
		
		// creates a new BT Customer during token creation
		String token1 = BraintreeUtil.get().generateClientToken(customerId);
		Assert.assertNotNull(token1);
		
		// finds the BT Customer during token creation
		String token2 = BraintreeUtil.get().generateClientToken(customerId);
		Assert.assertNotNull(token2);

		// the same customer id should still lead to different tokens
		Assert.assertFalse(token1.equals(token2));
	}
}
