/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.core;

import java.util.Date;

/**
 * @author clintz
 * 
 */
public interface TimeAware
{
	public Date getCreated();

	public Date getUpdated();

}
