package com.talool.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.talool.core.ActivationCode;

/**
 * Activation code implementation
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "activation_code", catalog = "public")
public class ActivationCodeImpl implements ActivationCode
{
	@Id
	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "activation_code_id", unique = true, nullable = false)
	private UUID id;

	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "customer_id")
	private UUID customerId;

	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "deal_offer_id")
	private UUID dealOfferId;

	@Column(name = "activated_dt")
	private Date activatedDate;

	@Column(name = "code", length = 7, nullable = false)
	private String code;

	@Override
	public UUID getDealOfferId()
	{
		return dealOfferId;
	}

	@Override
	public void setDealOfferId(UUID dealOfferId)
	{
		this.dealOfferId = dealOfferId;
	}

	@Override
	public String getCode()
	{
		return code;
	}

	@Override
	public void setCode(String code)
	{
		this.code = code;
	}

	@Override
	public UUID getCustomerId()
	{
		return customerId;
	}

	@Override
	public void setCustomerId(final UUID customerId)
	{
		this.customerId = customerId;
	}

	@Override
	public Date getActivatedDate()
	{
		return activatedDate;
	}

	@Override
	public void setActivatedDate(Date date)
	{
		activatedDate = date;
	}

	@Override
	public UUID getId()
	{
		return id;
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

		if (!(obj instanceof ActivationCodeImpl))
		{
			return false;
		}

		final ActivationCodeImpl other = (ActivationCodeImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getDealOfferId(), other.getDealOfferId())
				.append(getCode(), other.getCode()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getDealOfferId()).append(getCode()).hashCode();
	}

}
