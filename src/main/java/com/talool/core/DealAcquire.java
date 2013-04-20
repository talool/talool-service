package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author clintz
 * 
 */
public interface DealAcquire extends IdentifiableUUID, Serializable, TimeAware
{
	public Deal getDeal();

	public AcquireStatus getAcquireStatus();

	public Customer getCustomer();

	public void setCustomer(Customer customer);

	public Merchant getSharedByMerchant();

	public Customer getSharedByCustomer();

	public Integer getShareCount();

	public Integer incrementShareCount();

	public Location getLocation();

	public Date getRedemptionDate();

}
