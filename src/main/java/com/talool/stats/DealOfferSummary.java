package com.talool.stats;

import java.io.Serializable;
import java.util.Calendar;
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
	private String locationName;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String offerType;
	private Double price;
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
	private Date scheduledStartDate;
	private Date scheduledEndDate;
	private String properties;

	public UUID getOfferId()
	{
		return offerId;
	}

	public void setOfferId(UUID offerId)
	{
		this.offerId = offerId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Double getPrice()
	{
		return price;
	}

	public void setPrice(Double price)
	{
		this.price = price;
	}

	public Boolean getIsActive()
	{
		return isActive;
	}

	public void setIsActive(Boolean isActive)
	{
		this.isActive = isActive;
	}

	public String getBackgroundUrl()
	{
		return backgroundUrl;
	}

	public void setBackgroundUrl(String backgroundUrl)
	{
		this.backgroundUrl = backgroundUrl;
	}

	public String getIconUrl()
	{
		return iconUrl;
	}

	public void setIconUrl(String iconUrl)
	{
		this.iconUrl = iconUrl;
	}

	public String getLogoUrl()
	{
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl)
	{
		this.logoUrl = logoUrl;
	}

	public String getSummary()
	{
		return summary;
	}

	public void setSummary(String summary)
	{
		this.summary = summary;
	}

	public String getOfferType()
	{
		return offerType;
	}

	public void setOfferType(String offerType)
	{
		this.offerType = offerType;
	}

	public String getMerchantName()
	{
		return merchantName;
	}

	public void setMerchantName(String merchantName)
	{
		this.merchantName = merchantName;
	}

	public String getCreatedByMerchantName()
	{
		return createdByMerchantName;
	}

	public void setCreatedByMerchantName(String createdByMerchantName)
	{
		this.createdByMerchantName = createdByMerchantName;
	}

	public UUID getMerchantId()
	{
		return merchantId;
	}

	public void setMerchantId(UUID merchantId)
	{
		this.merchantId = merchantId;
	}

	public Long getMerchantCount()
	{
		return merchantCount;
	}

	public void setMerchantCount(Long merchantCount)
	{
		this.merchantCount = merchantCount;
	}

	public Long getDealCount()
	{
		return dealCount;
	}

	public void setDealCount(Long dealCount)
	{
		this.dealCount = dealCount;
	}

	public Long getAcquiresCount()
	{
		return acquiresCount;
	}

	public void setAcquiresCount(Long acquiresCount)
	{
		this.acquiresCount = acquiresCount;
	}

	public Long getRedemptionCount()
	{
		return redemptionCount;
	}

	public void setRedemptionCount(Long redemptionCount)
	{
		this.redemptionCount = redemptionCount;
	}

	public String getLocationName()
	{
		return locationName;
	}

	public void setLocationName(String locationName)
	{
		this.locationName = locationName;
	}

	public String getAddress1()
	{
		return address1;
	}

	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	public String getAddress2()
	{
		return address2;
	}

	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public Date getScheduledStartDate()
	{
		return scheduledStartDate;
	}

	public void setScheduledStartDate(Date scheduledStartDate)
	{
		this.scheduledStartDate = scheduledStartDate;
	}

	public Date getScheduledEndDate()
	{
		return scheduledEndDate;
	}

	public void setScheduledEndDate(Date scheduledEndDate)
	{
		this.scheduledEndDate = scheduledEndDate;
	}

	public boolean isCurrentlyScheduled()
	{
		boolean scheduled = false;

		if (scheduledStartDate != null && scheduledEndDate != null)
		{
			final long now = Calendar.getInstance().getTime().getTime();
			scheduled = (now >= scheduledStartDate.getTime()) && (now < scheduledEndDate.getTime());
		}

		return scheduled;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

}
