package com.talool.core;

import java.io.Serializable;

/**
 * 
 * @author clintz
 * 
 */
public interface Tag extends Serializable
{
	public Integer getId();

	public String getName();

	public void setName(String tagName);
}
