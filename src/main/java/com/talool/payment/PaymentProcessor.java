package com.talool.payment;

import com.talool.core.Money;

/**
 * Available Payment Processors
 * 
 * @author clintz
 * 
 */
public enum PaymentProcessor
{
	BRAINTREE(2.9f, .029, new Money(.30));

	private Float feePercent;
	private Double feeRatio;
	private Money perTransactionFeeCents;

	private PaymentProcessor(Float feePercent, Double feeRatio, Money perTransactionFeeCents)
	{
		this.feePercent = feePercent;
		this.feeRatio = feeRatio;
		this.perTransactionFeeCents = perTransactionFeeCents;
	}

	public Float getFeePercent()
	{
		return feePercent;
	}

	public Double getFeeRatio()
	{
		return feeRatio;
	}

	public Money getPerTransactionFeeCents()
	{
		return perTransactionFeeCents;
	}
}
