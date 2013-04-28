package com.talool.core;

import java.io.Serializable;

import com.vividsolutions.jts.geom.Point;

public interface MerchantLocation extends Identifiable, Serializable, TimeAware
{
	public String getLocationName();

	public void setLocationName(String name);

	public String getEmail();

	public void setEmail(String email);

	public String getWebsiteUrl();

	public void setWebsiteUrl(String websiteUrl);

	public String getLogoUrl();

	public void setLogoUrl(String logoUrl);

	public String getPhone();

	public void setPhone(String phone);

	public void setLocation(Point location);

	public Address getAddress();

	public void setAddress(Address address);

	public Point getLocation();

	public Double getDistanceInMeters();

}
