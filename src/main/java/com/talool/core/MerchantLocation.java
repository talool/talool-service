package com.talool.core;

import java.io.Serializable;
import java.util.UUID;

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

	public MerchantMedia getLogo();

	public MerchantMedia getMerchantImage();

	public void setMerchantImage(MerchantMedia merchantImage);

	public void setLogo(MerchantMedia logo);

	public String getPhone();

	public void setPhone(String phone);

	public String getAddress1();

	public void setAddress1(String address1);

	public String getAddress2();

	public void setAddress2(String address2);

	public String getCity();

	public void setCity(String city);

	public String getStateProvinceCounty();

	public void setStateProvinceCounty(String stateProvinceCounty);

	public String getZip();

	public void setZip(String zip);

	public String getCountry();

	public void setCountry(String country);

	public Geometry getGeometry();

	public void setGeometry(Geometry geometry);

	public Double getDistanceInMeters();

	public String getNiceCityState();

	public MerchantAccount getCreatedByMerchantAccount();

	public void setCreatedByMerchantAccount(MerchantAccount createdByMerchantAccount);

	public Merchant getCreatedByMerchant();

	public UUID getCreatedByMerchantId();

	/**
	 * Returns true if the email has no known associated issues determined from
	 * MTA agents. For example, if the email address bounced false would be
	 * returned. Othwerwise it is believed to be avalid address
	 * 
	 * @return
	 */
	public boolean isEmailValid();

	public void setIsEmailValid(final boolean isValid);

}
