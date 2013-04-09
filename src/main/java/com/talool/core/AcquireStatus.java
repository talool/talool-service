package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * Acquire Status Interface
 * 
 * @author clintz
 * 
 */
public interface AcquireStatus extends Serializable
{
	public Integer getId();

	public String getStatus();

	public Date getCreated();

}
