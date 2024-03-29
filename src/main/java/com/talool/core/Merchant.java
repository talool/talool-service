package com.talool.core;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.talool.domain.Properties;

/**
 * Merchant interface
 * 
 * @author clintz
 * 
 */
public interface Merchant extends IdentifiableUUID, Serializable, TimeAware, PropertyEntity
{
	public String getName();

	public void setName(String name);

	public MerchantLocation getPrimaryLocation();

	public MerchantLocation getCurrentLocation();

	public void setCurrentLocation(MerchantLocation location);

	/**
	 * Temporary - returns Immutable Set . This simply adds primary to all managed
	 * locations
	 * 
	 * TODO replace with a single call, drop primary location
	 * 
	 * @return
	 */
	public Set<MerchantLocation> getLocations();

	public Merchant getParent();

	public Set<MerchantAccount> getMerchantAccounts();

	public void setParent(Merchant merchant);

	public void clearTags();

	public Set<Tag> getTags();

	public void setTags(Set<Tag> tags);

	public void addTag(Tag tag);

	public void addTags(List<Tag> tag);

	public void addLocation(MerchantLocation mloc);

	public Category getCategory();

	public void setCategory(final Category category);

	public boolean isDiscoverable();

	public void setIsDiscoverable(boolean isDiscoverable);

	public Properties getProperties();

}
