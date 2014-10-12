package com.talool.messaging.job;

import java.util.UUID;

/**
 * DealOffer Job Interface
 * 
 * @author dmccuen
 * 
 */
public interface DealOfferPurchaseJob extends MessagingJob
{
	public static final String dealOfferIdKey = "DealOfferId";
	public static final String dealOfferPurchaseJobTitleKey = "dealOfferPurchaseJobTitle";
	/**
	 * Sets the deal offer id
	 * 
	 * @param deal
	 */
	public void setDealOfferId(final UUID offerId);

	/**
	 * Gets the deal offer id
	 * 
	 * @return
	 */
	public UUID getDealOfferId();

}
