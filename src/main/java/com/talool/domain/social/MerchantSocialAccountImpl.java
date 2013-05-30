/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.domain.social;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.talool.core.social.MerchantSocialAccount;

/**
 * @author clintz
 * 
 */
@Entity
@Table(name = "social_account", catalog = "public")
@DiscriminatorValue("M")
public class MerchantSocialAccountImpl extends SocialAccountImpl implements MerchantSocialAccount
{
	private static final long serialVersionUID = -2070704416429180263L;

	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "merchant_id", unique = true, nullable = false)
	private UUID merchantId;

	@Override
	public UUID getMerchantId()
	{
		return merchantId;
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

		if (!(obj instanceof MerchantSocialAccountImpl))
		{
			return false;
		}

		final MerchantSocialAccountImpl other = (MerchantSocialAccountImpl) obj;

		return super.equals(obj) && new EqualsBuilder().append(getMerchantId(), other.getMerchantId()).isEquals();

	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getMerchantId()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
