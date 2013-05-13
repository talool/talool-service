package com.talool.core;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 
 * @author clintz
 * 
 */
public interface FavoriteMerchant extends Serializable
{
	public Long getId();

	public UUID getMerchantId();

	public UUID getCustomerId();

	public Date getCreated();

}
