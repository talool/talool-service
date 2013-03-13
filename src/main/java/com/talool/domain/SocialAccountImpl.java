/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.talool.core.AccountType;
import com.talool.core.SocialAccount;
import com.talool.core.SocialNetwork;
import com.talool.persistence.GenericEnumUserType;

/**
 * @author clintz
 * 
 */
@Entity
@Table(name = "social_account", catalog = "public")
@TypeDef(name = "accountType", typeClass = GenericEnumUserType.class, parameters = { @Parameter(name = "enumClass", value = "com.talool.core.AccountType") })
public class SocialAccountImpl implements SocialAccount
{
	private static final long serialVersionUID = -2070704416429180263L;

	public SocialAccountImpl()
	{
		primaryKey = new SocialAccountPK();
	}

	@Embeddable
	public static class SocialAccountPK implements Serializable
	{
		public SocialNetwork getSocialNetwork()
		{
			return socialNetwork;
		}

		public void setSocialNetwork(SocialNetwork socialNetwork)
		{
			this.socialNetwork = socialNetwork;
		}

		public Long getUserId()
		{
			return userId;
		}

		public void setUserId(Long userId)
		{
			this.userId = userId;
		}

		public AccountType getAccountType()
		{
			return accountType;
		}

		public void setAccountType(AccountType accountType)
		{
			this.accountType = accountType;
		}

		private static final long serialVersionUID = 3294959164705341195L;

		@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = SocialNetworkImpl.class)
		@Access(AccessType.PROPERTY)
		@JoinColumn(name = "social_network_id", nullable = false)
		private SocialNetwork socialNetwork;

		@Column(name = "user_id", unique = false, nullable = false)
		private Long userId;

		@Type(type = "accountType")
		@Column(name = "account_t", unique = true, nullable = false, columnDefinition = "account_type")
		@Enumerated(EnumType.STRING)
		private AccountType accountType;
	}

	@EmbeddedId
	private final SocialAccountPK primaryKey;

	@Column(name = "token", unique = false, nullable = false)
	private String token;

	@Embedded
	private CreatedUpdated createdUpdated;

	@Column(name = "login_id", unique = false, nullable = false, length = 32)
	private String loginId;

	public SocialAccountPK getPrimaryKey()
	{
		return primaryKey;
	}

	@Override
	public SocialNetwork getSocialNetwork()
	{
		return primaryKey.socialNetwork;
	}

	public String getSocialNetworkName()
	{
		return primaryKey.socialNetwork.getName();
	}

	@Override
	public void setSocialNetwork(SocialNetwork socialNetwork)
	{
		primaryKey.socialNetwork = socialNetwork;
	}

	@Override
	public Long getUserId()
	{
		return primaryKey.userId;
	}

	@Override
	public void setUserId(Long userId)
	{
		primaryKey.userId = userId;
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
	public AccountType getAccountType()
	{
		return primaryKey.accountType;
	}

	@Override
	public void setAccountType(AccountType accountType)
	{
		primaryKey.accountType = accountType;
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

		return primaryKey.equals(other);

	}

	@Override
	public int hashCode()
	{
		return primaryKey.hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public Date getCreated()
	{
		return createdUpdated.getCreated();
	}

	@Override
	public Date getUpdated()
	{
		return createdUpdated.getCreated();
	}

	@Override
	public String getToken()
	{
		return token;
	}

	@Override
	public void setToken(String token)
	{
		this.token = token;
	}
}
