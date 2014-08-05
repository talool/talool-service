package com.talool.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;

import com.talool.core.DealOffer;
import com.talool.core.Merchant;
import com.talool.core.Money;
import com.talool.domain.Properties;
import com.talool.payment.PaymentCalculator;
import com.talool.payment.PaymentProcessor;
import com.talool.payment.PaymentReceipt;
import com.talool.utils.KeyValue;

/**
 * 
 * @author clintz
 * 
 */
public class BraintreeFeesTest
{

	/**
	 * Testing the Boulder/Vancouver case
	 */
	@Test
	public void testPublisherFeesCaseOne()
	{
		// mock publisher
		Properties publisherProps = mock(Properties.class);
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantId)).thenReturn("paybackbook_instant_f2k625v4");
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantStatus)).thenReturn("ACTIVE");
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantStatusTimestamp)).thenReturn("1406168391000");
		when(publisherProps.getAsFloat(KeyValue.fundraiserDistributionPercent)).thenReturn(50.0f);

		Merchant publisher = mock(Merchant.class);
		when(publisher.getName()).thenReturn("Payback Book");
		when(publisher.getProperties()).thenReturn(publisherProps);

		// mock deal offer
		Properties dealOfferProps = mock(Properties.class);

		when(dealOfferProps.getAsFloat(KeyValue.taloolFeeDiscountPercent)).thenReturn(75.0f);
		when(dealOfferProps.getAsFloat(KeyValue.taloolFeePercent)).thenReturn(20.0f);
		when(dealOfferProps.getAsDouble(KeyValue.taloolFeeMinumum)).thenReturn(2.5);

		DealOffer dealOffer = mock(DealOffer.class);
		when(dealOffer.getPrice()).thenReturn(20.0f);
		when(dealOffer.getMerchant()).thenReturn(publisher);
		when(dealOffer.getProperties()).thenReturn(dealOfferProps);

		PaymentReceipt expectedPaymentReceipt = new PaymentReceipt(.50f, .75f, .20f, new Money(20.00), new Money(2.50), new Money(.88),
				new Money(10.00), new Money(9.12), new Money(.63));

		PaymentReceipt paymentReceipt = PaymentCalculator.get().generatePaymentReceipt(PaymentProcessor.BRAINTREE, dealOffer, publisher,
				null);

		Assert.assertEquals(expectedPaymentReceipt.toString(), paymentReceipt.toString());

	}

	/**
	 * Testing with a smaller Talool Fee Percent (TFP) of 50% . Represenative of
	 * Bend
	 */
	@Test
	public void testPublisherFeesCaseTwo()
	{
		// mock publisher
		Properties publisherProps = mock(Properties.class);
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantId)).thenReturn("paybackbook_instant_f2k625v4");
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantStatus)).thenReturn("ACTIVE");
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantStatusTimestamp)).thenReturn("1406168391000");
		when(publisherProps.getAsFloat(KeyValue.fundraiserDistributionPercent)).thenReturn(50.0f);

		Merchant publisher = mock(Merchant.class);
		when(publisher.getName()).thenReturn("Payback Book");
		when(publisher.getProperties()).thenReturn(publisherProps);

		// mock deal offer
		Properties dealOfferProps = mock(Properties.class);

		when(dealOfferProps.getAsFloat(KeyValue.taloolFeeDiscountPercent)).thenReturn(50.0f);
		when(dealOfferProps.getAsFloat(KeyValue.taloolFeePercent)).thenReturn(20.0f);
		when(dealOfferProps.getAsDouble(KeyValue.taloolFeeMinumum)).thenReturn(2.5);

		DealOffer dealOffer = mock(DealOffer.class);
		when(dealOffer.getPrice()).thenReturn(20.0f);
		when(dealOffer.getMerchant()).thenReturn(publisher);
		when(dealOffer.getProperties()).thenReturn(dealOfferProps);

		PaymentReceipt expectedPaymentReceipt = new PaymentReceipt(.50f, .50f, .20f, new Money(2.50), new Money(2.50), new Money(.88),
				new Money(10.00), new Money(9.12), new Money(1.25));

		PaymentReceipt paymentReceipt = PaymentCalculator.get().generatePaymentReceipt(PaymentProcessor.BRAINTREE, dealOffer, publisher,
				null);

		Assert.assertEquals(expectedPaymentReceipt.getFundraiserDistributionPercent(), paymentReceipt.getFundraiserDistributionPercent(),
				0);

		Assert.assertEquals(expectedPaymentReceipt.getTaloolFeeDiscountPercent(), paymentReceipt.getTaloolFeeDiscountPercent(), 0);

		Assert.assertEquals(expectedPaymentReceipt.getTaloolFeePercent(), paymentReceipt.getTaloolFeePercent(), 0);

		Assert.assertEquals(expectedPaymentReceipt.getNetRevenue().getValue().doubleValue(), paymentReceipt.getNetRevenue().getValue()
				.doubleValue(), 0);

		Assert.assertEquals(expectedPaymentReceipt.getPaymentProcessingFee().getValue().doubleValue(), paymentReceipt
				.getPaymentProcessingFee().getValue().doubleValue(), 0);

		Assert.assertEquals("Talool Fee Min does not match", expectedPaymentReceipt.getTaloolFeeMinimum().getValue().doubleValue(),
				paymentReceipt.getTaloolFeeMinimum().getValue().doubleValue(), 0);

	}

	/**
	 * Testing the case of every where else where the Talool Fee Discount Percent
	 * is zero
	 * 
	 */
	@Test
	public void testPublisherFeesCaseThree()
	{
		// mock publisher
		Properties publisherProps = mock(Properties.class);
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantId)).thenReturn("paybackbook_instant_f2k625v4");
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantStatus)).thenReturn("ACTIVE");
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantStatusTimestamp)).thenReturn("1406168391000");
		when(publisherProps.getAsFloat(KeyValue.fundraiserDistributionPercent)).thenReturn(50.0f);

		Merchant publisher = mock(Merchant.class);
		when(publisher.getName()).thenReturn("Payback Book");
		when(publisher.getProperties()).thenReturn(publisherProps);

		// mock deal offer
		Properties dealOfferProps = mock(Properties.class);

		when(dealOfferProps.getAsFloat(KeyValue.taloolFeeDiscountPercent)).thenReturn(0.0f);
		when(dealOfferProps.getAsFloat(KeyValue.taloolFeePercent)).thenReturn(20.0f);
		when(dealOfferProps.getAsDouble(KeyValue.taloolFeeMinumum)).thenReturn(2.5);

		DealOffer dealOffer = mock(DealOffer.class);
		when(dealOffer.getPrice()).thenReturn(20.0f);
		when(dealOffer.getMerchant()).thenReturn(publisher);
		when(dealOffer.getProperties()).thenReturn(dealOfferProps);

		PaymentReceipt expectedPaymentReceipt = new PaymentReceipt(.50f, 0.0f, .20f, new Money(20.00), new Money(2.50), new Money(.88),
				new Money(10.00), new Money(9.12), new Money(2.50));

		PaymentReceipt paymentReceipt = PaymentCalculator.get().generatePaymentReceipt(PaymentProcessor.BRAINTREE, dealOffer, publisher,
				null);

		Assert.assertEquals(expectedPaymentReceipt.toString(), paymentReceipt.toString());

	}

	/**
	 * Defaults to Talool default values when missing from properties
	 */
	@Test
	public void testPublisherFeesWhereDefaultsUsed()
	{
		// mock publisher
		Properties publisherProps = mock(Properties.class);
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantId)).thenReturn("paybackbook_instant_f2k625v4");
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantStatus)).thenReturn("ACTIVE");
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantStatusTimestamp)).thenReturn("1406168391000");
		when(publisherProps.getAsFloat(KeyValue.fundraiserDistributionPercent)).thenReturn(null);

		Merchant publisher = mock(Merchant.class);
		when(publisher.getName()).thenReturn("Payback Book");
		when(publisher.getProperties()).thenReturn(publisherProps);

		// mock deal offer
		Properties dealOfferProps = mock(Properties.class);

		DealOffer dealOffer = mock(DealOffer.class);
		when(dealOffer.getPrice()).thenReturn(20.0f);
		when(dealOffer.getMerchant()).thenReturn(publisher);

		when(dealOfferProps.getAsFloat(KeyValue.taloolFeeDiscountPercent)).thenReturn(null);
		when(dealOfferProps.getAsFloat(KeyValue.taloolFeePercent)).thenReturn(null);
		when(dealOfferProps.getAsDouble(KeyValue.taloolFeeMinumum)).thenReturn(null);
		when(dealOffer.getProperties()).thenReturn(dealOfferProps);

		PaymentReceipt expectedPaymentReceipt = new PaymentReceipt(.0f, 0.0f, .20f, new Money(20.00), new Money(2.50), new Money(.88),
				new Money(0.00), new Money(19.12), new Money(3.83));

		PaymentReceipt paymentReceipt = PaymentCalculator.get().generatePaymentReceipt(PaymentProcessor.BRAINTREE, dealOffer, publisher,
				null);

		Assert.assertEquals(expectedPaymentReceipt.toString(), paymentReceipt.toString());

	}

	/**
	 * Tests override the Publisher fundraiserDistributionPercent by the
	 * Fundraiser
	 */
	@Test
	public void testOverridefundraiserDistributionPercent()
	{
		// mock fundraiser
		Merchant fundraiser = mock(Merchant.class);
		when(fundraiser.getName()).thenReturn("Payback Book");

		Properties fundRaiserProps = mock(Properties.class);
		// overriding publisher fundraiserDistributionPercent
		when(fundRaiserProps.getAsFloat(KeyValue.fundraiserDistributionPercent)).thenReturn(50.0f);
		when(fundraiser.getProperties()).thenReturn(fundRaiserProps);

		// mock publisher
		Properties publisherProps = mock(Properties.class);
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantId)).thenReturn("paybackbook_instant_f2k625v4");
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantStatus)).thenReturn("ACTIVE");
		when(publisherProps.getAsString(KeyValue.braintreeSubmerchantStatusTimestamp)).thenReturn("1406168391000");
		when(publisherProps.getAsString((KeyValue.braintreeSubmerchantStatusTimestamp))).thenReturn("true");

		Merchant publisher = mock(Merchant.class);
		when(publisher.getName()).thenReturn("Payback Book");
		when(publisher.getProperties()).thenReturn(publisherProps);

		// mock deal offer
		Properties dealOfferProps = mock(Properties.class);
		when(dealOfferProps.getAsFloat(KeyValue.taloolFeeDiscountPercent)).thenReturn(75.0f);
		when(dealOfferProps.getAsFloat(KeyValue.taloolFeePercent)).thenReturn(20.0f);
		when(dealOfferProps.getAsDouble(KeyValue.taloolFeeMinumum)).thenReturn(2.5);

		DealOffer dealOffer = mock(DealOffer.class);
		when(dealOffer.getPrice()).thenReturn(20.0f);
		when(dealOffer.getMerchant()).thenReturn(publisher);
		when(dealOffer.getProperties()).thenReturn(dealOfferProps);

		PaymentReceipt expectedPaymentReceipt = new PaymentReceipt(.50f, .75f, .20f, new Money(20.00), new Money(2.50), new Money(.88),
				new Money(10.00), new Money(9.12), new Money(.63));

		PaymentReceipt paymentReceipt = PaymentCalculator.get().generatePaymentReceipt(PaymentProcessor.BRAINTREE, dealOffer, publisher,
				fundraiser);

		Assert.assertEquals(expectedPaymentReceipt.toString(), paymentReceipt.toString());

	}
}