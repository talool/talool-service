package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * Deal Book
 * 
 * @author clintz
 * 
 */
public interface MerchantDeal extends Identifiable, Serializable, TimeAware
{
	public Merchant getMerchant();

	public void setTitle(String title);

	public String getTitle();

	public void setSummary(String summary);

	public String getSummary();

	public void setDetails(String details);

	public String geDetails();

	public void setCode(String code);

	public String getCode();

	public void setImageUrl(String imageUrl);

	public String getImageUrl();

	public void setExpires(Date expires);

	public Date getExpires();

	public boolean isActive();

	public void setActive(boolean isActive);

}
