package com.talool.core;

import java.io.Serializable;

/**
 * Location
 * 
 * @author clintz
 * 
 */
public interface Location extends Serializable
{
	public Double getLongitude();

	public void setLongitude(Double longitude);

	public Double getLatitude();

	public void setLatitude(Double latitude);
}
