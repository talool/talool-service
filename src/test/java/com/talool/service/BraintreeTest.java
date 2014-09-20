package com.talool.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.braintreegateway.Result;
import com.braintreegateway.SettlementBatchSummary;
import com.talool.core.DealOffer;
import com.talool.domain.DealOfferImpl;
import com.talool.payment.braintree.BraintreeUtil;

@Ignore
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

	public static void main(String args[])
	{
		ServiceConfig.createInstance("service.properties");

		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.addDays(new Date(), -60));
		Result<SettlementBatchSummary> result = null;
		// Result<SettlementBatchSummary> result = BraintreeUtil.get().settlementBatchSummary(cal);

		if (result.isSuccess())
		{
			List<Map<String, String>> records = result.getTarget().getRecords();

			for (Map<String, String> record : records)
			{
				for (Entry<String, String> entry : record.entrySet())
				{
					System.out.println("key:" + entry.getKey() + ",val:" + entry.getValue());
				}
			}
		}
	}
}
