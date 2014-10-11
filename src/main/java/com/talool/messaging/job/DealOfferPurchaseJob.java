package com.talool.messaging.job;

import com.talool.core.DealOffer;

/**
 * DealOffer Job Interface
 * 
 * @author dmccuen
 * 
 */
public interface DealOfferPurchaseJob extends MessagingJob
{
	/**
	 * Sets the deal offer
	 * 
	 * @param deal
	 */
	public void setDealOffer(final DealOffer offer);

	/**
	 * Gets the deal offer
	 * 
	 * @return
	 */
	public DealOffer getDealOffer();

}
