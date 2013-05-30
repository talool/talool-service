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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.talool.core.Customer;
import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.SocialNetwork;
import com.talool.domain.CustomerImpl;

/**
 * @author clintz
 * 
 */
@Entity
@Table(name = "customer_social_account", catalog = "public")
public class CustomerSocialAccountImpl implements CustomerSocialAccount
{
	private static final long serialVersionUID = -2070704416429180263L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_csa_seq")
	@SequenceGenerator(name = "my_csa_seq", sequenceName = "customer_social_account_customer_social_account_id_seq")
	@Column(name = "customer_social_account_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = SocialNetworkImpl.class)
	@JoinColumn(name = "social_network_id", nullable = false)
	private SocialNetwork socialNetwork;

	@Column(name = "login_id", unique = false, nullable = false, length = 32)
	private String loginId;

	@ManyToOne(targetEntity = CustomerImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Column(name = "create_dt", unique = false, insertable = false, updatable = false)
	private Date created;

	@Override
	public Customer getCustomer()
	{
		return customer;
	}

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

		if (!(obj instanceof CustomerSocialAccountImpl))
		{
			return false;
		}

		final CustomerSocialAccountImpl other = (CustomerSocialAccountImpl) obj;

		return super.equals(obj) && new EqualsBuilder().append(getCustomer(), other.getCustomer()).
				append(getLoginId(), other.getLoginId()).
				append(getSocialNetwork(), other.getSocialNetwork()).
				isEquals();

	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getCustomer()).append(getLoginId()).append(getSocialNetwork()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public void setCustomer(final Customer customer)
	{
		this.customer = customer;
	}

	@Override
	public Date getCreated()
	{
		return created;
	}

}
