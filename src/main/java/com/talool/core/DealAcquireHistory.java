package com.talool.core;

import java.io.Serializable;
import java.util.Date;

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

	public Merchant getSharedByMerchant();

	public Customer getSharedByCustomer();

	public Integer getShareCount();

	public void setShareCount(Integer shareCount);

	public Date getUpdated();

}
