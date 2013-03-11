/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author clintz
 * 
 */
@Embeddable
public class CreatedUpdated
{
	@Access(AccessType.FIELD)
	@Column(name = "update_dt", unique = false, insertable = false, updatable = false)
	private Date updated;

	@Access(AccessType.FIELD)
	@Column(name = "update_dt", unique = false, insertable = false, updatable = false)
	private Date created;

	public Date getUpdated()
	{
		return updated;
	}

	public Date getCreated()
	{
		return created;
	}

}
