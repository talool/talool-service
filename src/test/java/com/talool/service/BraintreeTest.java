package com.talool.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.talool.core.DealOffer;
import com.talool.domain.DealOfferImpl;
import com.talool.payment.braintree.BraintreeUtil;

public class BraintreeTest
{

	@BeforeClass
	public static void init()
	{
		ServiceConfig.createInstance("service.properties");
	}

	@Test
	public void testDescriptor()
	{
		DealOffer dof = new DealOfferImpl();

		// tests removing special characters
		dof.setTitle("Co'mpany&@+");

		Assert.assertEquals(BraintreeUtil.COMPANY_PREFIX_DESCRIPTOR + "COMPANY", BraintreeUtil.get().createDescriptor(dof));

		dof.setTitle("Longhouse Council, BSA");

		Assert.assertEquals(BraintreeUtil.COMPANY_PREFIX_DESCRIPTOR + "LONGHOUSE COUN", BraintreeUtil.get().createDescriptor(dof));

	}
}
