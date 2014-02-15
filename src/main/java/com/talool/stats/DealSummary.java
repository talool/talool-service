package com.talool.stats;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 
 * @author dmccuen
 * 
 */
public final class DealSummary implements Serializable
{

	private static final long serialVersionUID = -8399734482095031455L;
	private UUID dealId;
	private String title;
	private String summary;
	private String details;
	private String tags;
	private String imageUrl;
	private Date expires;
	private Boolean isActive;
	private UUID merchantId;
	private String merchantName;
	private UUID createdByMerchantId;
	private String createdByMerchantName;
	private UUID offerId;
	private String offerTitle;
	private int redemptionCount;
	private int acquireCount;
	private int giftCount;
	
	public UUID getDealId() {
		return dealId;
	}
	public void setDealId(UUID dealId) {
		this.dealId = dealId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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
	public UUID getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(UUID merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public UUID getCreatedByMerchantId() {
		return createdByMerchantId;
	}
	public void setCreatedByMerchantId(UUID createdByMerchantId) {
		this.createdByMerchantId = createdByMerchantId;
	}
	public String getCreatedByMerchantName() {
		return createdByMerchantName;
	}
	public void setCreatedByMerchantName(String createdByMerchantName) {
		this.createdByMerchantName = createdByMerchantName;
	}
	public UUID getOfferId() {
		return offerId;
	}
	public void setOfferId(UUID offerId) {
		this.offerId = offerId;
	}
	public String getOfferTitle() {
		return offerTitle;
	}
	public void setOfferTitle(String offerTitle) {
		this.offerTitle = offerTitle;
	}
	public int getRedemptionCount() {
		return redemptionCount;
	}
	public void setRedemptionCount(int redemptionCount) {
		this.redemptionCount = redemptionCount;
	}
	public int getDealCount() {
		return acquireCount;
	}
	public void setDealCount(int dealCount) {
		this.acquireCount = dealCount;
	}
	public int getGiftCount() {
		return giftCount;
	}
	public void setGiftCount(int giftCount) {
		this.giftCount = giftCount;
	}
	
	

}
