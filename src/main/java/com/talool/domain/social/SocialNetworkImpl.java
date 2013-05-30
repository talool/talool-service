/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.domain.social;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.talool.core.social.SocialNetwork;

/**
 * @author clintz
 * 
 */
@Entity
@Table(name = "social_network", catalog = "public")
public class SocialNetworkImpl implements SocialNetwork
{
	private static final long serialVersionUID = 3927081856250957166L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_social_network_seq")
	@SequenceGenerator(name = "my_social_network_seq", sequenceName = "social_network_id_seq")
	@Column(name = "social_network_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "name", unique = true, nullable = false, length = 64)
	private String name;

	@Column(name = "website", unique = true, nullable = false, length = 64)
	private String website;

	@Column(name = "api_url", unique = true, nullable = false, length = 64)
	private String apiUrl;

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj == null)
		{
			return false;
		}

		if (!(obj instanceof SocialNetworkImpl))
		{
			return false;
		}

		final SocialNetworkImpl other = (SocialNetworkImpl) obj;

		return new EqualsBuilder().append(getName(), other.getName()).isEquals();
	}

	public String getWebsite()
	{
		return website;
	}

	public void setWebsite(String website)
	{
		this.website = website;
	}

	public String getApiUrl()
	{
		return apiUrl;
	}

	public void setApiUrl(String apiUrl)
	{
		this.apiUrl = apiUrl;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(name).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
