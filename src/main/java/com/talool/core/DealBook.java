package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * Deal Book
 * 
 * @author clintz
 * 
 */
public interface DealBook extends Identifiable, Serializable, TimeAware
{
	public Merchant getMerchant();

	public void setMerchant(Merchant merchant);

	public void setTitle(String title);

	public String getTitle();

	public void setSummary(String summary);

	public String getSummary();

	public void setDetails(String details);

	public String geDetails();

	public void setCode(String code);

	public String getCode();

	public Location getLocation();

	public void setLocation(Location location);

	public void setCost(Float cost);

	public Float getCost();

	public void setExpires(Date expires);

	public Date getExpires();

	public boolean isActive();

	public void setActive(boolean isActive);

}
