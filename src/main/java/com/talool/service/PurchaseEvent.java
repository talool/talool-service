package com.talool.service;

import java.util.Map;

import com.talool.core.DealOfferPurchase;
import com.talool.core.Merchant;

/**
 * 
 * @author clintz
 * 
 */
class PurchaseEvent
{
	private DealOfferPurchase dealOfferPurchase;
	private Map<String, String> paymentProperties;
	private Merchant fundraiser;

	PurchaseEvent(final DealOfferPurchase dealOfferPurchase,
			final Map<String, String> paymentProperties, final Merchant fundraiser)
	{
		this.dealOfferPurchase = dealOfferPurchase;
		this.paymentProperties = paymentProperties;
		this.fundraiser = fundraiser;
	}

	public Merchant getFundraiser()
	{
		return fundraiser;
	}

	public DealOfferPurchase getDealOfferPurchase()
	{
		return dealOfferPurchase;
	}

	public Map<String, String> getPaymentProperties()
	{
		return paymentProperties;
	}

}
