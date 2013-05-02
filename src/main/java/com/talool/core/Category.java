package com.talool.core;

import java.io.Serializable;

/**
 * 
 * @author clintz
 * 
 */
public interface Category extends Serializable
{
	public Integer getId();

	public String getName();

	public void setName(String name);

}
