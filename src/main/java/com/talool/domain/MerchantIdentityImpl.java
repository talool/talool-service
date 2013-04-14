package com.talool.domain;

import com.talool.core.MerchantIdentity;

/**
 * 
 * @author clintz
 * 
 */
public class MerchantIdentityImpl implements MerchantIdentity
{
	private static final long serialVersionUID = 9042040637750195480L;
	private Long id;
	private String name;

	public MerchantIdentityImpl(Long id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public MerchantIdentityImpl()
	{}

	public Long getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	void setId(Long id)
	{
		this.id = id;
	}

	void setName(String name)
	{
		this.name = name;
	}

}
