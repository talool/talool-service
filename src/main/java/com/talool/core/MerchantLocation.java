package com.talool.core;

import java.io.Serializable;

import com.vividsolutions.jts.geom.Geometry;

public interface MerchantLocation extends Identifiable, Serializable, TimeAware
{
	public Merchant getMerchant();

	public void setMerchant(final Merchant merchant);

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

	public Address getAddress();

	public void setAddress(Address address);

	public Geometry getGeometry();

	public void setGeometry(Geometry geometry);

	public Double getDistanceInMeters();

}
