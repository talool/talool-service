/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;

/**
 * @author clintz
 * 
 */
@Embeddable
public class Properties implements Serializable
{
	private static final long serialVersionUID = -4835611475842607243L;

	@Type(type = "hstore")
	@Column(columnDefinition = "hstore")
	public Map<String, String> properties = new HashMap<String, String>();

	public Map<String, String> getAllProperties()
	{
		return properties;
	}

	public String getAsString(final String key)
	{
		return properties.get(key);
	}

	public int getAsInt(final String key)
	{
		return Integer.valueOf(properties.get(key));
	}

	public float getAsFloat(final String key)
	{
		return Float.valueOf(properties.get(key));
	}

	public boolean getAsBool(final String key)
	{
		return Boolean.valueOf(properties.get(key));
	}

	public boolean getAsDouble(final String key)
	{
		return Boolean.valueOf(properties.get(key));
	}

	public short getAsShort(final String key)
	{
		return Short.valueOf(properties.get(key));
	}

	public void createOrReplace(final String key, final int value)
	{
		properties.put(key, String.valueOf(value));
	}

	public void createOrReplace(final String key, final String value)
	{
		properties.put(key, value);
	}

	public void createOrReplace(final String key, final float value)
	{
		properties.put(key, String.valueOf(value));
	}

	public void createOrReplace(final String key, final boolean value)
	{
		properties.put(key, String.valueOf(value));
	}

	public void createOrReplace(final String key, final double value)
	{
		properties.put(key, String.valueOf(value));
	}

	public void remove(final String key)
	{
		properties.remove(key);
	}

	public String dumpProperties()
	{
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : properties.entrySet())
		{
			sb.append(entry.getKey()).append(" -> ").append(entry.getValue());
		}
		return sb.toString();

	}

}
