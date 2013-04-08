package com.talool.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
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
import org.hibernate.annotations.Target;

import com.talool.core.Address;
import com.talool.core.Location;
import com.talool.core.MerchantLocation;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "merchant_location", catalog = "public")
public class MerchantLocationImpl implements MerchantLocation
{
	private static final long serialVersionUID = 3716227130006204917L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_loc_seq")
	@SequenceGenerator(name = "my_loc_seq", sequenceName = "merchant_location_merchant_location_id_seq")
	@Column(name = "merchant_location_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "merchant_location_name", unique = false, nullable = false, length = 64)
	private String locationName;

	@Column(name = "email", unique = true, nullable = true, length = 64)
	private String email;

	@Column(name = "website_url", unique = false, nullable = true, length = 128)
	private String websiteUrl;

	@Column(name = "logo_url", unique = false, nullable = true, length = 64)
	private String logoUrl;

	@Column(name = "phone", unique = true, nullable = true, length = 48)
	private String phone;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = AddressImpl.class)
	@JoinColumn(name = "address_id")
	private Address address;

	@Embedded
	@Target(LocationImpl.class)
	private Location location;

	@Embedded
	private CreatedUpdated createdUpdated;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	@Override
	public String getLocationName()
	{
		return locationName;
	}

	@Override
	public void setLocationName(String name)
	{
		this.locationName = name;
	}

	@Override
	public String getWebsiteUrl()
	{
		return websiteUrl;
	}

	@Override
	public void setWebsiteUrl(String websiteUrl)
	{
		this.websiteUrl = websiteUrl;
	}

	@Override
	public String getLogoUrl()
	{
		return logoUrl;
	}

	@Override
	public void setLogoUrl(String logoUrl)
	{
		this.logoUrl = logoUrl;
	}

	@Override
	public String getPhone()
	{
		return phone;
	}

	@Override
	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	@Override
	public Address getAddress()
	{
		return address;
	}

	@Override
	public void setAddress(Address address)
	{
		this.address = address;
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
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
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

		if (!(obj instanceof MerchantLocationImpl))
		{
			return false;
		}

		final MerchantLocationImpl other = (MerchantLocationImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getLocationName(), other.getLocationName())
				.append(getAddress(), other.getAddress()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getLocationName()).append(getAddress()).hashCode();
	}

	@Override
	public String getEmail()
	{
		return email;
	}

	@Override
	public void setEmail(final String email)
	{
		this.email = email;
	}

	@Override
	public Location getLocation()
	{
		return location;
	}

	@Override
	public void setLocation(Location location)
	{
		this.location = location;
	}
}
