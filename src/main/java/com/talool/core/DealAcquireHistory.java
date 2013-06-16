package com.talool.core;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.talool.core.gift.Gift;

/**
 * 
 * @author clintz
 * 
 */
public interface DealAcquireHistory extends Serializable
{
	public DealAcquire getDealAcquire();

	public AcquireStatus getAcquireStatus();

	public Customer getCustomer();

	/**
	 * If the DealAcquire was acquired via Gifting, Gift will not be null
	 * 
	 * @return
	 */
	public Gift getGift();

	public UUID getGiftId();

	public Date getUpdated();

}
