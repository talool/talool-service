package com.talool.core;

import java.io.Serializable;
import java.util.Date;

import com.talool.domain.Properties;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Deal Offer interface
 * 
 * @author clintz
 * 
 */
public interface DealOffer extends IdentifiableUUID, Serializable, TimeAware, PropertyEntity
{
	public MerchantAccount getCreatedByMerchantAccount();

	public String getCreatedByEmail();

	public String getCreatedByMerchantName();

	public MerchantAccount getUpdatedByMerchantAccount();

	public void setUpdatedByMerchantAccount(MerchantAccount merchantAccount);

	public String getUpdatedByEmail();

	public String getUpdatedByMerchantName();

	public Merchant getMerchant();

	public void setMerchant(final Merchant merchant);

	public void setDealType(DealType dealType);

	public String getLocationName();

	public void setLocationName(String name);

	public DealType getType();

	public void setTitle(String title);

	public String getTitle();

	public void setSummary(String summary);

	public String getSummary();

	public void setCode(String code);

	public String getCode();

	public void setDealOfferLogo(MerchantMedia imageUrl);

	public MerchantMedia getDealOfferLogo();

	public void setDealOfferBackground(MerchantMedia imageUrl);

	public MerchantMedia getDealOfferBackground();

	public void setDealOfferIcon(MerchantMedia imageUrl);

	public MerchantMedia getDealOfferIcon();

	public void setPrice(Float price);

	public Float getPrice();

	public boolean isActive();

	public void setActive(boolean isActive);

	public Geometry getGeometry();

	public void setGeometry(Geometry geometry);

	public Properties getProperties();

	/**
	 * Gets the start date of a published scheduling
	 * 
	 * @return
	 */
	public Date getScheduledStartDate();

	/**
	 * Gets the end date of a published scheduling
	 * 
	 * @return
	 */
	public Date getScheduledEndDate();

	/**
	 * Sets the scheduled start date
	 * 
	 * @param date
	 */
	public void setScheduledStartDate(final Date date);

	/**
	 * Sets the scheduled end date
	 * 
	 * @param date
	 */
	public void setScheduledEndDate(final Date date);

	/**
	 * Returns true of the Deal offer is within the scheduled time (start/end
	 * date) false otherwise.
	 * 
	 * @return
	 */
	public boolean isCurrentlyScheduled();

}
