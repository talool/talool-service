/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.core;

import java.io.Serializable;

/**
 * @author clintz
 * 
 */
public interface SocialNetwork extends Identifiable, Serializable
{
	public String getName();

	public String getWebsite();

	public String getApiUrl();

}
