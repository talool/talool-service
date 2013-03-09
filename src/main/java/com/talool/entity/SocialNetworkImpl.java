/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
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

import com.talool.core.SocialNetwork;

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
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_social_network_seq")
	@SequenceGenerator(name = "my_social_network_seq", sequenceName = "social_network_id_seq")
	@Column(name = "social_network_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "name", unique = true, nullable = false, length = 32)
	private String name;

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

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getName(), other.getName()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getName()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
