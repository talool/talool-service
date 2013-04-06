package com.talool.core;

import java.io.Serializable;

/**
 * Aquire Status Interface
 * 
 * @author clintz
 * 
 */
public interface AquireStatus extends Serializable, TimeAware
{
	public Integer getId();

	public String getStatus();

}
