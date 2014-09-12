package com.talool.payment;

import java.math.BigDecimal;

import com.talool.core.DealOffer;
import com.talool.core.Merchant;
import com.talool.core.Money;
import com.talool.domain.Properties;
import com.talool.utils.KeyValue;

/**
 * Provides the business logic for calculating the Talool fee to charge sub merchants at the time of purchase. Please
 * see the Google doc https://docs.google.com/a/talool.com/spreadsheets/d/1
 * UFWrvLZwaPGF7WWz6jiT8K9UOQehwvPVcXEDuMw2AK4/edit#gid=0 for details on the logic.
 * 
 * @author clintz
 * 
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
	 * TODO fundraiserDistributionPercent is set on publisher but can be override by fundraiser . Any nulls go to defaults
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

		if (fundraiser != null)
		{
			Properties fundraiserProps = fundraiser.getProperties();
			if (fundraiserProps != null)
			{
				fdpPercent = fundraiser.getProperties().getAsFloat(KeyValue.fundraiserDistributionPercent);
			}
			if (fdpPercent == null)
			{
				fdpPercent = publisher.getProperties().getAsFloat(KeyValue.fundraiserDistributionPercent);
			}
		}

		if (fdpPercent == null)
		{
			fdpPercent = DEFAULT_FUNDRAISER_DIST_PERC;
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
		netRevenue.setRoundingMode(BigDecimal.ROUND_UP);

		final Double taloolProcessingFee = Math.max(netRevenue.multiply(tfp).multiply(1 - tfdp).getValue().doubleValue(),
				tfm.multiply(1 - tfdp).getValue().doubleValue());

		// rounding round_half_even - the "bankers' rounding"
		Money fee = new Money(taloolProcessingFee).setRoundingMode(BigDecimal.ROUND_HALF_EVEN).add(paymentProcessingFee);

		return new PaymentReceipt(fdp, tfdp, tfp, gross, tfm, paymentProcessingFee, fundraiserDistribution, netRevenue, fee);

	}
}
