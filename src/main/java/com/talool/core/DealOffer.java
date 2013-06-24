package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * Deal Offer interface
 * 
 * @author clintz
 * 
 */
public interface DealOffer extends IdentifiableUUID, Serializable, TimeAware
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

	public void setImage(MerchantMedia imageUrl);

	public MerchantMedia getImage();

	public void setExpires(Date expires);

	public void setPrice(Float price);

	public Float getPrice();

	public Date getExpires();

	public boolean isActive();

	public void setActive(boolean isActive);

}
