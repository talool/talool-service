package com.talool.core;

import java.io.Serializable;
import java.util.UUID;

/**
 * 
 * @author clintz
 * 
 */
public interface MerchantIdentity extends Serializable
{
	public UUID getId();

	public String getName();
}
