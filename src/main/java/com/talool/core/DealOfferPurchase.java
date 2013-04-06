package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author clintz
 * 
 */
public interface DealOfferPurchase extends Identifiable, Serializable
{
	public DealOffer getDealOffer();

	public void setDealOffer(DealOffer dealOffer);

	public Customer getCustomer();

	public void setCustomer(Customer customer);

	public Location getLocation();

	public void setLocation(Location location);

	public Date getCreated();

}
