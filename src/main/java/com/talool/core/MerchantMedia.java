package com.talool.core;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 
 * @author clintz
 * 
 */
public interface MerchantMedia extends IdentifiableUUID, Serializable
{
	public UUID getMerchantId();

	public void setMerchantId(final UUID merchantId);

	public String getMediaUrl();
	
	public String getMediaName();

	public void setMediaUrl(final String mediaUrl);

	public MediaType getMediaType();

	public void setMediaType(final MediaType mediaType);

	public Date getCreated();
	
	public void clearTags();

	public Set<Tag> getTags();

	public void setTags(Set<Tag> tags);

	public void addTag(Tag tag);

	public void addTags(List<Tag> tag);
	
	public Category getCategory();

	public void setCategory(final Category category);

}
