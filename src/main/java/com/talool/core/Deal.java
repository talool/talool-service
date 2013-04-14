package com.talool.core;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Deal Book
 * 
 * @author clintz
 * 
 */
public interface Deal extends Identifiable, Serializable, TimeAware
{
	public DealOffer getDealOffer();

	public void setDealOffer(DealOffer dealOffer);

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

	public void setImageUrl(String imageUrl);

	public String getImageUrl();

	public void setExpires(Date expires);

	public Date getExpires();

	public boolean isActive();

	public void setActive(boolean isActive);

	public void clearTags();

	public Set<Tag> getTags();

	public void setTags(Set<Tag> tags);

	public void addTag(Tag tag);

	public void addTags(List<Tag> tag);

	public String getCreatedByEmail();

	public String getCreatedByMerchantName();

}
