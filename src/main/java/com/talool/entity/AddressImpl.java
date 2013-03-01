package com.talool.entity;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.talool.core.Address;

/**
 * 
 * @author clintz
 */
@Entity
@Table(name = "address", catalog = "public")
public class AddressImpl implements Address
{
	private static final long serialVersionUID = 958137187646742761L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_address_seq")
	@SequenceGenerator(name = "my_address_seq", sequenceName = "address_address_id_seq")
	@Column(name = "address_id", unique = true, nullable = false)
	private Long id;

	@Transient
	private com.talool.thrift.Address thriftAddress;

	public AddressImpl()
	{
		thriftAddress = new com.talool.thrift.Address();
	}

	public AddressImpl(com.talool.thrift.Address address)
	{
		this.thriftAddress = address;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "address1", unique = false, nullable = true, length = 64)
	@Override
	public String getAddress1()
	{
		return thriftAddress.getAddress1();
	}

	@Override
	public void setAddress1(String address1)
	{
		thriftAddress.setAddress1(address1);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "address2", unique = false, nullable = true, length = 64)
	@Override
	public String getAddress2()
	{
		return thriftAddress.getAddress2();
	}

	@Override
	public void setAddress2(String address2)
	{
		thriftAddress.setAddress2(address2);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "city", unique = false, nullable = false, length = 64)
	@Override
	public String getCity()
	{
		return thriftAddress.getCity();
	}

	@Override
	public void setCity(String city)
	{
		thriftAddress.city = city;
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "state_province_county", unique = false, nullable = true, length = 64)
	public String getStateProvinceCounty()
	{
		return thriftAddress.stateProvinceCounty;
	}

	@Override
	public void setStateProvinceCounty(String stateProvinceCounty)
	{
		thriftAddress.stateProvinceCounty = stateProvinceCounty;
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "zip", unique = false, nullable = true, length = 64)
	public String getZip()
	{
		return thriftAddress.zip;
	}

	@Override
	public void setZip(String zip)
	{
		thriftAddress.zip = zip;
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "country", unique = false, nullable = false, length = 4)
	public String getCountry()
	{
		return thriftAddress.country;
	}

	@Override
	public void setCountry(String country)
	{
		thriftAddress.country = country;
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "create_dt", insertable = false, updatable = false)
	public Date getCreated()
	{
		return new Date(thriftAddress.created);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "update_dt", insertable = false, updatable = false)
	public Date getUpdated()
	{
		return new Date(thriftAddress.updated);
	}

	void setUpdated(final Date updated)
	{
		thriftAddress.updated = updated.getTime();
	}

	void setCreated(final Date updated)
	{
		thriftAddress.created = updated.getTime();
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

		if (!(obj instanceof AddressImpl))
		{
			return false;
		}

		final AddressImpl other = (AddressImpl) obj;
		if (getId() == null)
		{
			if (other.getId() != null)
			{
				return false;
			}
		}
		else if (!getId().equals(other.getId()))
		{
			return false;
		}

		return new EqualsBuilder().append(getAddress1(), other.getAddress1())
				.append(getAddress2(), other.getAddress2()).append(getCity(), other.getCity())
				.append(getStateProvinceCounty(), other.getStateProvinceCounty())
				.append(getCountry(), other.getCountry()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getAddress1()).append(getAddress2())
				.append(getCity()).append(getStateProvinceCounty()).append(getCountry()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}
