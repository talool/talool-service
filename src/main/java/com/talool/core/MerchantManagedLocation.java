package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author clintz
 * 
 */
public interface MerchantManagedLocation extends Identifiable, Serializable
{
	public Merchant getMerchant();

	public void setMerchant(Merchant merchant);

	public MerchantLocation getMerchantLocation();

	public void setMerchantLocation(MerchantLocation merchantLocation);

	public Date getCreated();

}
