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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Address;
import com.talool.core.Location;
import com.talool.core.Merchant;

/**
 * 
 * TODO Verify hashcode/equals makes sense
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "merchant", catalog = "public")
public class MerchantImpl implements Merchant
{
	private static final Logger LOG = LoggerFactory.getLogger(MerchantImpl.class);
	private static final long serialVersionUID = -4505114813841857043L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_merchant_seq")
	@SequenceGenerator(name = "my_merchant_seq", sequenceName = "merchant_merchant_id_seq")
	@Column(name = "merchant_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = MerchantImpl.class)
	@JoinColumn(name = "merchant_parent_id")
	private Merchant parent;

	@Column(name = "merchant_name", unique = false, nullable = false, length = 64)
	private String name;

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

	@Column(name = "password", unique = false, nullable = false, length = 64)
	private String password;

	@Column(name = "is_active", unique = false, nullable = true)
	private boolean isActive = true;

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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getWebsiteUrl()
	{
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl)
	{
		this.websiteUrl = websiteUrl;
	}

	public String getLogoUrl()
	{
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl)
	{
		this.logoUrl = logoUrl;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
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

	public Merchant getParent()
	{
		return parent;
	}

	public void setParent(Merchant parent)
	{
		this.parent = parent;
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

		if (!(obj instanceof MerchantImpl))
		{
			return false;
		}

		final MerchantImpl other = (MerchantImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getName(), other.getName())
				.append(getAddress(), other.getAddress()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getName()).append(getAddress()).hashCode();
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	@Override
	public void setPassword(String password)
	{
		this.password = password;

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
