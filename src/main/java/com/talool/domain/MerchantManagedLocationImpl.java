package com.talool.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
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

import com.talool.core.Merchant;
import com.talool.core.MerchantLocation;
import com.talool.core.MerchantManagedLocation;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "merchant_managed_location", catalog = "public")
public class MerchantManagedLocationImpl implements MerchantManagedLocation
{
	private static final long serialVersionUID = -6837320768906950989L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_mml_seq")
	@SequenceGenerator(name = "my_mml_seq", sequenceName = "merchant_managed_location_merchant_managed_location_id_seq")
	@Column(name = "merchant_managed_location_id", unique = true, nullable = false)
	private Long id;

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = MerchantImpl.class)
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;

	@Access(AccessType.FIELD)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = MerchantLocationImpl.class)
	@JoinColumn(name = "merchant_location_id")
	private MerchantLocation merchantLocation;

	@Column(name = "create_dt", unique = false, insertable = false, updatable = false)
	private Date created;

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public Merchant getMerchant()
	{
		return merchant;
	}

	@Override
	public void setMerchant(Merchant merchant)
	{
		this.merchant = merchant;
	}

	@Override
	public MerchantLocation getMerchantLocation()
	{
		return merchantLocation;
	}

	@Override
	public void setMerchantLocation(MerchantLocation merchantLocation)
	{
		this.merchantLocation = merchantLocation;
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

		if (!(obj instanceof MerchantManagedLocationImpl))
		{
			return false;
		}

		final MerchantManagedLocationImpl other = (MerchantManagedLocationImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getMerchant(), other.getMerchant())
				.append(getMerchantLocation(), other.getMerchantLocation()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getMerchant()).append(getMerchantLocation())
				.hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
