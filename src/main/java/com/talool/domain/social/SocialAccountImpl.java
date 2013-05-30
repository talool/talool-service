/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.domain.social;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.talool.core.social.SocialAccount;
import com.talool.core.social.SocialNetwork;

/**
 * @author clintz
 * 
 */
@Entity
@Table(name = "social_account", catalog = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.CHAR, length = 1)
@DiscriminatorValue("S")
public abstract class SocialAccountImpl implements SocialAccount
{
	private static final long serialVersionUID = -2070704416429180263L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_sac_seq")
	@SequenceGenerator(name = "my_sac_seq", sequenceName = "social_account_social_account_id_seq")
	@Column(name = "social_account_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = SocialNetworkImpl.class)
	@JoinColumn(name = "social_network_id", nullable = false)
	private SocialNetwork socialNetwork;

	@Column(name = "create_dt", unique = false, insertable = false, updatable = false)
	private Date created;

	@Column(name = "login_id", unique = false, nullable = false, length = 32)
	private String loginId;

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public SocialNetwork getSocialNetwork()
	{
		return socialNetwork;
	}

	public String getSocialNetworkName()
	{
		return socialNetwork.getName();
	}

	@Override
	public void setSocialNetwork(final SocialNetwork socialNetwork)
	{
		this.socialNetwork = socialNetwork;
	}

	@Override
	public String getLoginId()
	{
		return loginId;
	}

	@Override
	public void setLoginId(String loginId)
	{
		this.loginId = loginId;
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

		if (!(obj instanceof SocialAccountImpl))
		{
			return false;
		}

		final SocialAccountImpl other = (SocialAccountImpl) obj;

		return new EqualsBuilder().append(getSocialNetwork(),
				other.getSocialNetwork()).append(getLoginId(), other.getLoginId()).isEquals();

	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getSocialNetwork()).append(getLoginId()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public Date getCreated()
	{
		return created;
	}

}
