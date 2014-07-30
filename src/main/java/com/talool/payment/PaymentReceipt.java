package com.talool.payment;

import com.talool.core.Money;

/**
 * A payment receipt supporting funraiser, publisher
 * 
 * @author clintz
 * 
 */
public class PaymentReceipt
{
	private final float fundraiserDistributionPercent;
	private final float taloolFeeDiscountPercent;
	private final float taloolFeePercent;
	private final Money taloolFeeMinimum;
	private final Money paymentProcessingFee;
	private final Money fundraiserDistribution;
	private final Money netRevenue;
	private final Money taloolProcessingFee;

	public PaymentReceipt(float fundraiserDistributionPercent, float taloolFeeDiscountPercent, float taloolFeePercent,
			Money taloolFeeMinimum, Money paymentProcessingFee, Money fundraiserDistribution, Money netRevenue, Money taloolProcessingFee)
	{
		super();
		this.fundraiserDistributionPercent = fundraiserDistributionPercent;
		this.taloolFeeDiscountPercent = taloolFeeDiscountPercent;
		this.taloolFeePercent = taloolFeePercent;
		this.taloolFeeMinimum = taloolFeeMinimum;
		this.paymentProcessingFee = paymentProcessingFee;
		this.fundraiserDistribution = fundraiserDistribution;
		this.netRevenue = netRevenue;
		this.taloolProcessingFee = taloolProcessingFee;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("fundraiserDistributionPercent: ").append(fundraiserDistributionPercent).append(" taloolFeeDiscountPercent: ")
				.append(taloolFeeDiscountPercent).append(" taloolFeePercent: ").append(taloolFeePercent).append(" taloolFeeMinimum: ")
				.append(taloolFeeMinimum.toString()).append(" paymentProcessingFee: ").append(paymentProcessingFee.toString())
				.append(" fundraiserDistribution: ").append(fundraiserDistribution.toString()).append(" netRevenue: ").append(netRevenue)
				.append(" taloolProcessingFee: ").append(taloolProcessingFee);

		return sb.toString();

	}

	public float getFundraiserDistributionPercent()
	{
		return fundraiserDistributionPercent;
	}

	public float getTaloolFeeDiscountPercent()
	{
		return taloolFeeDiscountPercent;
	}

	public float getTaloolFeePercent()
	{
		return taloolFeePercent;
	}

	public Money getTaloolFeeMinimum()
	{
		return taloolFeeMinimum;
	}

	public Money getPaymentProcessingFee()
	{
		return paymentProcessingFee;
	}

	public Money getFundraiserDistribution()
	{
		return fundraiserDistribution;
	}

	public Money getNetRevenue()
	{
		return netRevenue;
	}

	public Money getTaloolProcessingFee()
	{
		return taloolProcessingFee;
	}

}
