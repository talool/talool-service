package com.talool.core;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Merchant interface
 * 
 * @author clintz
 * 
 */
public interface Merchant extends IdentifiableUUID, Serializable, TimeAware
{
	public String getName();

	public void setName(String name);

	public MerchantLocation getPrimaryLocation();

	public Set<MerchantLocation> getLocations();

	/**
	 * Temporary - returns Immutable Set . This simply adds primary to all managed
	 * locations
	 * 
	 * TODO replace with a single call, drop primary location
	 * 
	 * @return
	 */
	public Set<MerchantLocation> getAllLocations();

	public void setPrimaryLocation(MerchantLocation merchantLocation);

	public Merchant getParent();

	public Set<MerchantAccount> getMerchantAccounts();

	public void setParent(Merchant merchant);

	public void clearTags();

	public Set<Tag> getTags();

	public void setTags(Set<Tag> tags);

	public void addTag(Tag tag);

	public void addTags(List<Tag> tag);

	/**
	 * Get number of merchant accounts (without loading all objects to count them)
	 * 
	 * Nice optimization on the persistent store
	 * 
	 * @return
	 */
	public Long getNumberOfMerchantAccounts();

}
