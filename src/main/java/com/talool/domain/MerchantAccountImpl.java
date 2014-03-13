package com.talool.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.service.EncryptService;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "merchant_account", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class MerchantAccountImpl implements MerchantAccount
{
	private static final Logger LOG = LoggerFactory.getLogger(MerchantAccountImpl.class);
	private static final long serialVersionUID = -5479442982443424394L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_ma_seq")
	@SequenceGenerator(name = "my_ma_seq", sequenceName = "merchant_account_merchant_account_id_seq")
	@Column(name = "merchant_account_id", unique = true, nullable = false)
	private Long id;

	@OneToOne(targetEntity = MerchantImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;

	@Column(name = "email", unique = false, nullable = false, length = 128)
	private String email;

	@Column(name = "password", unique = false, nullable = false, length = 32)
	private String password;

	@Column(name = "role_title", unique = false, nullable = false, length = 64)
	private String roleTile;

	@Column(name = "allow_deal_creation", unique = false, nullable = false)
	private boolean allowDealCreation;

	@Embedded
	private Properties props;

	@Embedded
	private CreatedUpdated createdUpdated;

	public MerchantAccountImpl()
	{}

	public MerchantAccountImpl(Merchant merchant)
	{
		this.merchant = merchant;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public Date getCreated()
	{
		return createdUpdated.getCreated();
	}

	@Override
	public Date getUpdated()
	{
		return createdUpdated.getUpdated();
	}

	@Override
	public Merchant getMerchant()
	{
		return merchant;
	}

	@Override
	public String getEmail()
	{
		return email;
	}

	@Override
	public void setEmail(String email)
	{
		this.email = email;

	}

	@Override
	public String getPassword()
	{
		return password;
	}

	@Override
	public void setPassword(String password)
	{
		String md5pass = null;

		try
		{
			md5pass = EncryptService.MD5(password);
		}
		catch (Exception e)
		{
			LOG.error("Problem encrypting set password", e);
		}

		this.password = md5pass;
	}

	@Override
	public String getRoleTitle()
	{
		return roleTile;
	}

	@Override
	public void setRoleTitle(String roleTitle)
	{
		this.roleTile = roleTitle;
	}

	@Override
	public boolean allowDealCreation()
	{
		return allowDealCreation;
	}

	@Override
	public void setAllowDealCreation(boolean allowDealCreation)
	{
		this.allowDealCreation = allowDealCreation;
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

		if (!(obj instanceof MerchantAccountImpl))
		{
			return false;
		}

		final MerchantAccountImpl other = (MerchantAccountImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getEmail(), other.getEmail())
				.append(getMerchant(), other.getMerchant()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getEmail()).append(getMerchant()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public Properties getProperties()
	{
		return props;
	}

}
