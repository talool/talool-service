package com.talool.payment;

import com.talool.core.Money;

/**
 * A payment receipt which breaks down fees
 * 
 * https://docs.google.com/a/talool.com/spreadsheets/d/1
 * UFWrvLZwaPGF7WWz6jiT8K9UOQehwvPVcXEDuMw2AK4/edit#gid=0
 * 
 * @author clintz
 * 
 */
public class PaymentReceipt
{
	private final float fundraiserDistributionPercent;
	private final float taloolFeeDiscountPercent;
	private final float taloolFeePercent;
	private final Money cost;
	private final Money taloolFeeMinimum;
	private final Money paymentProcessingFee;
	private final Money fundraiserDistribution;
	private final Money netRevenue;
	private final Money taloolProcessingFee;

	public PaymentReceipt(float fundraiserDistributionPercent, float taloolFeeDiscountPercent, float taloolFeePercent, Money cost,
			Money taloolFeeMinimum, Money paymentProcessingFee, Money fundraiserDistribution, Money netRevenue, Money taloolProcessingFee)
	{
		super();
		this.cost = cost;
		this.fundraiserDistributionPercent = fundraiserDistributionPercent;
		this.taloolFeeDiscountPercent = taloolFeeDiscountPercent;
		this.taloolFeePercent = taloolFeePercent;
		this.taloolFeeMinimum = taloolFeeMinimum;
		this.paymentProcessingFee = paymentProcessingFee;
		this.fundraiserDistribution = fundraiserDistribution;
		this.netRevenue = netRevenue;
		this.taloolProcessingFee = taloolProcessingFee;
	}

	public String getDisplay()
	{
		StringBuilder sb = new StringBuilder(64);

		sb.append("cost: ").append(cost).append(" fundraiserDistPerc: ").append(fundraiserDistributionPercent)
				.append(" taloolFeeDiscPerc: ").append(taloolFeeDiscountPercent).append(" taloolFeePerc: ").append(taloolFeePercent)
				.append(" taloolFeeMin: ").append(taloolFeeMinimum.toString()).append(" processingFee: ")
				.append(paymentProcessingFee.toString()).append(" fundraiserDist: ").append(fundraiserDistribution.toString())
				.append(" netRev: ").append(netRevenue).append(" taloolFee: ").append(taloolProcessingFee);

		return sb.toString();
	}

	@Override
	public String toString()
	{
		return getDisplay();
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
