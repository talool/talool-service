/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.core.social;

import java.io.Serializable;

import com.talool.core.Identifiable;

/**
 * @author clintz
 * 
 */
public interface SocialNetwork extends Identifiable, Serializable
{
	public enum NetworkName
	{
		Facebook, Twitter, Pinterest
	}

	public String getName();

	public String getWebsite();

	public String getApiUrl();

}
