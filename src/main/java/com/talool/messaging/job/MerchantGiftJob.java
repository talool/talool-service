package com.talool.messaging.job;

import com.talool.core.Deal;

/**
 * Merchant Gift Job Interface
 * 
 * @author clintz
 * 
 */
public interface MerchantGiftJob extends MessagingJob
{
	/**
	 * Sets the deal
	 * 
	 * @param deal
	 */
	public void setDeal(final Deal deal);

	/**
	 * Gets the deal
	 * 
	 * @return
	 */
	public Deal getDeal();

}
