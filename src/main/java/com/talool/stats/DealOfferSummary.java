package com.talool.stats;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 
 * @author dmccuen
 * 
 */
public final class DealOfferSummary implements Serializable
{
	private static final long serialVersionUID = 1856781471468977494L;
	private UUID offerId;
	private UUID merchantId;
	private String title;
	private String summary;
	private String location;
	private String offerType;
	private Double price;
	private Date expires;
	private Boolean isActive;
	private String backgroundUrl;
	private String iconUrl;
	private String logoUrl;
	private String merchantName;
	private String createdByMerchantName;
	private Long merchantCount;
	private Long dealCount;
	private Long acquiresCount;
	private Long redemptionCount;
	
	public UUID getOfferId() {
		return offerId;
	}
	public void setOfferId(UUID offerId) {
		this.offerId = offerId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Date getExpires() {
		return expires;
	}
	public void setExpires(Date expires) {
		this.expires = expires;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public String getBackgroundUrl() {
		return backgroundUrl;
	}
	public void setBackgroundUrl(String backgroundUrl) {
		this.backgroundUrl = backgroundUrl;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getOfferType() {
		return offerType;
	}
	public void setOfferType(String offerType) {
		this.offerType = offerType;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getCreatedByMerchantName() {
		return createdByMerchantName;
	}
	public void setCreatedByMerchantName(String createdByMerchantName) {
		this.createdByMerchantName = createdByMerchantName;
	}
	public UUID getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(UUID merchantId) {
		this.merchantId = merchantId;
	}
	public Long getMerchantCount() {
		return merchantCount;
	}
	public void setMerchantCount(Long merchantCount) {
		this.merchantCount = merchantCount;
	}
	public Long getDealCount() {
		return dealCount;
	}
	public void setDealCount(Long dealCount) {
		this.dealCount = dealCount;
	}
	public Long getAcquiresCount() {
		return acquiresCount;
	}
	public void setAcquiresCount(Long acquiresCount) {
		this.acquiresCount = acquiresCount;
	}
	public Long getRedemptionCount() {
		return redemptionCount;
	}
	public void setRedemptionCount(Long redemptionCount) {
		this.redemptionCount = redemptionCount;
	}
	
	
}
