package com.talool.core;

import com.talool.payment.braintree.BraintreeUtil.RefundType;

/**
 * 
 * @author clintz
 * 
 */
public class RefundResult
{
	private RefundType refundType;
	private int totalDealAcquiresRemoved;

	public RefundResult(final RefundType refundType, int totalDealAcquiresRemoved)
	{
		this.refundType = refundType;
		this.totalDealAcquiresRemoved = totalDealAcquiresRemoved;
	}

	public RefundType getRefundType()
	{
		return refundType;
	}

	public int getTotalDealAcquiresRemoved()
	{
		return totalDealAcquiresRemoved;
	}
}
