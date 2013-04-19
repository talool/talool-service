package com.talool.domain;

import java.util.UUID;

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
	private UUID id;
	private String name;

	public MerchantIdentityImpl(final UUID id, final String name)
	{
		this.id = id;
		this.name = name;
	}

	public MerchantIdentityImpl()
	{}

	@Override
	public UUID getId()
	{
		return id;
	}

	@Override
	public String getName()
	{
		return name;
	}

	void setId(UUID id)
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
