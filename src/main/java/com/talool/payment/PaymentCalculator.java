package com.talool.payment;

import java.math.BigDecimal;

import com.talool.core.DealOffer;
import com.talool.core.Merchant;
import com.talool.core.Money;
import com.talool.utils.KeyValue;

/**
 * Provides convenient methods and objects for payments split between Talool,
 * publishers, and fundraisers
 * 
 * @author clintz
 * 
 */
public final class PaymentCalculator
{
	private static PaymentCalculator instance = new PaymentCalculator();

	// TODO Move to properties or new talool default props table
	private static final float DEFAULT_FUNDRAISER_DIST_PERC = 0;
	private static final float DEFAULT_TALOOL_FEE_DISCOUNT_PERC = 0.0f;
	private static final float DEFAULT_TALOOL_FEE_PERC = 20.0f;
	private static final double DEFAULT_TALOOL_FEE_MIN = 2.5f;

	public static PaymentCalculator get()
	{
		return instance;
	}

	/**
	 * TODO fundraiserDistributionPercent is set on publisher but can be override
	 * by fundraiser . Any nulls go to defaults
	 * 
	 * @param paymentProcessor
	 * @param dealOffer
	 * @param publisher
	 * @return
	 */
	public PaymentReceipt generatePaymentReceipt(final PaymentProcessor paymentProcessor, final DealOffer dealOffer,
			final Merchant publisher, final Merchant fundraiser)
	{
		final Money gross = new Money(dealOffer.getPrice().doubleValue());
		Float fdpPercent = null;
		Float tfdPercent = null;
		Float tfPercent = null;
		Double tfMin = null;

		// fundraiserDistributionPercent on publisher can be override by fundraisers
		if (fundraiser == null || (fdpPercent = fundraiser.getProperties().getAsFloat(KeyValue.fundraiserDistributionPercent)) == null)
		{
			fdpPercent = publisher.getProperties().getAsFloat(KeyValue.fundraiserDistributionPercent);
			if (fdpPercent == null)
			{
				fdpPercent = DEFAULT_FUNDRAISER_DIST_PERC;
			}
		}

		final float fdp = fdpPercent / 100;

		tfdPercent = dealOffer.getProperties().getAsFloat(KeyValue.taloolFeeDiscountPercent);
		if (tfdPercent == null)
		{
			tfdPercent = DEFAULT_TALOOL_FEE_DISCOUNT_PERC;
		}
		final float tfdp = tfdPercent / 100;

		tfPercent = dealOffer.getProperties().getAsFloat(KeyValue.taloolFeePercent);
		if (tfPercent == null)
		{
			tfPercent = DEFAULT_TALOOL_FEE_PERC;
		}

		final float tfp = tfPercent / 100;

		tfMin = dealOffer.getProperties().getAsDouble(KeyValue.taloolFeeMinumum);
		if (tfMin == null)
		{
			tfMin = DEFAULT_TALOOL_FEE_MIN;
		}
		final Money tfm = new Money(tfMin);

		final Money paymentProcessingFee = gross.multiply(paymentProcessor.getFeeRatio()).add(
				(paymentProcessor.getPerTransactionFeeCents()));

		final Money fundraiserDistribution = gross.multiply(fdp);

		Money netRevenue = gross.subtract(paymentProcessingFee).subtract(fundraiserDistribution);

		final Double taloolProcessingFee = Math.max(netRevenue.multiply(tfp).multiply(1 - tfdp).getValue().doubleValue(),
				tfm.multiply(1 - tfdp).getValue().doubleValue());

		Money fee = new Money(taloolProcessingFee).setRoundingMode(BigDecimal.ROUND_UP);

		return new PaymentReceipt(fdp, tfdp, tfp, tfm, paymentProcessingFee, fundraiserDistribution, netRevenue, fee);

	}
}
