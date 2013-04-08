package com.talool.core;

import java.io.Serializable;
import java.util.Set;

/**
 * Merchant interface
 * 
 * @author clintz
 * 
 */
public interface Merchant extends Identifiable, Serializable, TimeAware
{
	public String getName();

	public void setName(String name);

	public MerchantLocation getPrimaryLocation();

	public void setPrimaryLocation(MerchantLocation merchantLocation);

	public Merchant getParent();

	public Set<MerchantAccount> getMerchantAccounts();

	public void setParent(Merchant merchant);

	public Set<Tag> getTags();

	public void addTag(Tag tag);

	/**
	 * Get number of merchant accounts (without loading all objects to count them)
	 * 
	 * Nice optimization on the persistent store
	 * 
	 * @return
	 */
	public Long getNumberOfMerchantAccounts();

}
