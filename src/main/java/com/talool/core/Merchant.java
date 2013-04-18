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
public interface Merchant extends IdentifiableS, Serializable, TimeAware
{
	public String getName();

	public void setName(String name);

	public MerchantLocation getPrimaryLocation();

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
