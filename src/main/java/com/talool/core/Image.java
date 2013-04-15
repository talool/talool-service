package com.talool.core;

import java.io.Serializable;

/**
 * 
 * @author clintz
 * 
 */
public interface Image extends Serializable
{
	public Integer getId();

	public String getLabel();

	public void setLabel(String label);
	
	public String getUrl();
	
	public void setUrl(String url);
}
