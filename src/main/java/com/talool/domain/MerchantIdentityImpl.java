package com.talool.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.talool.core.MerchantIdentity;

/**
 * 
 * @author clintz
 * 
 */
public class MerchantIdentityImpl implements MerchantIdentity
{
	private static final long serialVersionUID = 9042040637750195480L;
	private String id;
	private String name;

	public MerchantIdentityImpl(String id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public MerchantIdentityImpl()
	{}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	void setId(String id)
	{
		this.id = id;
	}

	void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
