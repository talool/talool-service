package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * Aquire Status Interface
 * 
 * @author clintz
 * 
 */
public interface AquireStatus extends Serializable
{
	public Integer getId();

	public String getStatus();

	public Date getCreated();

}
