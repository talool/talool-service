package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author clintz
 * 
 */
public interface DealAquire extends Identifiable, Serializable, TimeAware
{
	public Deal getDeal();

	public void setDeal(Deal deal);

	public AquireStatus getAquireStatus();

	public void setAquireStatus(AquireStatus aquireStatus);

	public Customer getCustomer();

	public void setCustomer(Customer customer);

	public Merchant getSharedByMerchant();

	public void setSharedByMerchant(Merchant merchant);

	public Customer getSharedByCustomer();

	public void setSharedByCusomer(Customer customer);

	public Integer getShareCount();

	public void setShareCount(Integer shareCount);

	public Location getLocation();

	public void setLocation(Location location);

	public Date getRedemptionDate();

}
