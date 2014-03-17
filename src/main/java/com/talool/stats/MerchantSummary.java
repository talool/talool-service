package com.talool.stats;

import java.io.Serializable;
import java.util.UUID;

/**
 * 
 * @author dmccuen
 * 
 */
public final class MerchantSummary implements Serializable
{

	private static final long serialVersionUID = -8399734482095031455L;
	private UUID merchantId;
	private String name;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private String phone;
	private String website;
	private String category;
	private String logoUrl;
	private String imageUrl;
	private int locationCount;
	private int dealCount;
	private int merchantAccountCount;
	private String properties;
	
	public UUID getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(UUID merchantId) {
		this.merchantId = merchantId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getLocationCount() {
		return locationCount;
	}
	public void setLocationCount(int locationCount) {
		this.locationCount = locationCount;
	}
	public int getDealCount() {
		return dealCount;
	}
	public void setDealCount(int dealCount) {
		this.dealCount = dealCount;
	}
	public int getMerchantAccountCount() {
		return merchantAccountCount;
	}
	public void setMerchantAccountCount(int merchantAccountCount) {
		this.merchantAccountCount = merchantAccountCount;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}

	

}
