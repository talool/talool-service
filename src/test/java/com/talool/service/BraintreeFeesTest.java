package com.talool.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.talool.core.DealOffer;
import com.talool.core.Merchant;
import com.talool.domain.Properties;

/**
 * 
 * @author clintz
 * 
 */
public class BraintreeFeesTest
{

	@Test
	public void testPublisherFees()
	{
		// mock publisher
		Properties publisherProps = mock(Properties.class);
		when(publisherProps.getAsString("bt_submerch_id")).thenReturn("paybackbook_instant_f2k625v4");
		when(publisherProps.getAsString("bt_submerch_status")).thenReturn("ACTIVE");
		when(publisherProps.getAsString("bt_submerch_status_ts")).thenReturn("1406168391000");
		when(publisherProps.getAsString("publisher")).thenReturn("true");

		Merchant publisher = mock(Merchant.class);
		when(publisher.getName()).thenReturn("Payback Book");
		when(publisher.getProperties()).thenReturn(publisherProps);

		// mock deal offer
		Properties dealOfferProps = mock(Properties.class);
		when(dealOfferProps.getAsFloat("book_percent")).thenReturn(40f);

		DealOffer dealOffer = mock(DealOffer.class);
		when(dealOffer.getMerchant()).thenReturn(publisher);
		when(dealOffer.getProperties()).thenReturn(dealOfferProps);

		System.out.println(publisher.getName());
		System.out.println(publisher.getProperties().getAsFloat("book_percent"));

	}
}
