package com.talool.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class AddressImpl implements Address
{
	private static final long serialVersionUID = 958137187646742761L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_address_seq")
	@SequenceGenerator(name = "my_address_seq", sequenceName = "address_address_id_seq")
	@Column(name = "address_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "address1", unique = false, nullable = true, length = 64)
	private String address1;

	@Column(name = "address2", unique = false, nullable = true, length = 64)
	private String address2;

	@Column(name = "city", unique = false, nullable = false, length = 64)
	private String city;

	@Column(name = "state_province_county", unique = false, nullable = true, length = 64)
	private String stateProvinceCounty;

	@Column(name = "zip", unique = false, nullable = true, length = 64)
	private String zip;

	@Column(name = "country", unique = false, nullable = false, length = 4)
	private String country;

	@Embedded
	private CreatedUpdated createdUpdated;

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	@Override
	public String getAddress2()
	{
		return address2;
	}

	@Override
	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	@Override
	public String getCity()
	{
		return city;
	}

	@Override
	public void setCity(String city)
	{
		this.city = city;
	}

	@Override
	public String getStateProvinceCounty()
	{
		return stateProvinceCounty;
	}

	@Override
	public void setStateProvinceCounty(String stateProvinceCounty)
	{
		this.stateProvinceCounty = stateProvinceCounty;
	}

	@Override
	public String getZip()
	{
		return zip;
	}

	@Override
	public void setZip(String zip)
	{
		this.zip = zip;
	}

	@Override
	public String getCountry()
	{
		return country;
	}

	@Override
	public void setCountry(String country)
	{
		this.country = country;
	}

	@Override
	public Date getUpdated()
	{
		return createdUpdated.getUpdated();
	}

	@Override
	public Date getCreated()
	{
		return createdUpdated.getUpdated();
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

	@Override
	public String getAddress1()
	{
		return address1;
	}

	@Override
	public String getNiceCityState()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(city).append(", ").append(stateProvinceCounty);
		return sb.toString();
	}

}
