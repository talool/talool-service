package com.talool.core;

import java.io.Serializable;

public interface MerchantLocation extends Identifiable, Serializable, TimeAware
{
	public String getLocationName();

	public void setLocationName(String name);

	public String getEmail();

	public void setEmail(String email);

	public String getPassword();

	public void setPassword(String password);

	public String getWebsiteUrl();

	public void setWebsiteUrl(String websiteUrl);

	public String getLogoUrl();

	public void setLogoUrl(String logoUrl);

	public String getPhone();

	public void setPhone(String phone);

	public Location getLocation();

	public void setLocation(Location location);

	public Address getAddress();

	public void setAddress(Address address);

}
