package com.talool.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.talool.core.FavoriteMerchant;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "favorite_merchant", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class FavoriteMerchantImpl implements FavoriteMerchant
{
	private static final long serialVersionUID = 3633534358845067561L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_fvmer_seq")
	@SequenceGenerator(name = "my_fvmer_seq", sequenceName = "favorite_merchant_favorite_merchant_id_seq")
	@Column(name = "favorite_merchant_id", unique = true, nullable = false)
	private Long id;

	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "merchant_id", unique = true, nullable = false)
	private UUID merchantId;

	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "customer_id", unique = true, nullable = false)
	private UUID customerId;

	@Column(name = "create_dt", unique = false, insertable = false, updatable = false)
	private Date created;

	public FavoriteMerchantImpl()
	{}

	public FavoriteMerchantImpl(final UUID customerId, final UUID merchantId)
	{
		this.customerId = customerId;
		this.merchantId = merchantId;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public UUID getMerchantId()
	{
		return merchantId;
	}

	@Override
	public UUID getCustomerId()
	{
		return customerId;
	}

	@Override
	public Date getCreated()
	{
		return created;
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

		if (!(obj instanceof FavoriteMerchantImpl))
		{
			return false;
		}

		final FavoriteMerchantImpl other = (FavoriteMerchantImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getMerchantId(), other.getMerchantId())
				.append(getCustomerId(), other.getCustomerId()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getMerchantId()).append(getCustomerId())
				.hashCode();
	}

}
