package com.talool.core;

import java.io.Serializable;
import java.util.Date;
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

	public void setMediaUrl(final String mediaUrl);

	public MediaType getMediaType();

	public void setMediaType(final MediaType mediaType);

	public Date getCreated();

}
