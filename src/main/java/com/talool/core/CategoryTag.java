package com.talool.core;

import java.io.Serializable;

/**
 * 
 * @author clintz
 * 
 */
public interface CategoryTag extends Serializable
{
	public Tag getCategoryTag();

	public Category getCategory();

}
