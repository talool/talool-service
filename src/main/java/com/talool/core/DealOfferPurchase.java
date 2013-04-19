package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author clintz
 * 
 */
public interface DealOfferPurchase extends IdentifiableUUID, Serializable
{
	public DealOffer getDealOffer();

	public Customer getCustomer();

	public Location getLocation();

	public void setLocation(Location location);

	public Date getCreated();

}
