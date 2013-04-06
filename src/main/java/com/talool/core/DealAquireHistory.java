package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author clintz
 * 
 */
public interface DealAquireHistory extends Identifiable, Serializable
{
	public DealAquire getDealAquire();

	public AquireStatus getAquireStatus();

	public Customer getCustomer();

	public Merchant getSharedByMerchant();

	public Customer getSharedByCustomer();

	public Integer getShareCount();

	public void setShareCount(Integer shareCount);

	public Date getUpdated();

}
