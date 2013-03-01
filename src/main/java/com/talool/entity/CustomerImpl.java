package com.talool.entity;

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
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.talool.core.Address;
import com.talool.core.Customer;

/**
 * 
 * 
 * @author clintz
 */
@Entity
@Table(name = "customer", catalog = "public")
public class CustomerImpl implements Customer
{
	private static final long serialVersionUID = 2498058366640693644L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_customer_seq")
	@SequenceGenerator(name = "my_customer_seq", sequenceName = "customer_customer_id_seq")
	@Column(name = "customer_id", unique = true, nullable = false)
	private Long id;

	private Address address;

	@Transient
	private com.talool.thrift.Customer thriftCustomer;

	public CustomerImpl()
	{
		thriftCustomer = new com.talool.thrift.Customer();
	}

	public CustomerImpl(com.talool.thrift.Customer customer)
	{
		this.thriftCustomer = customer;
		this.address = new AddressImpl(customer.getAddress());
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "first_name", unique = false, nullable = false, length = 64)
	public String getFirstName()
	{
		return thriftCustomer.firstName;
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "last_name", unique = false, nullable = false, length = 64)
	public String getLastName()
	{
		return thriftCustomer.lastName;
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "email", unique = true, nullable = false, length = 64)
	public String getEmail()
	{
		return thriftCustomer.email;
	}

	@Override
	@Access(AccessType.PROPERTY)
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = AddressImpl.class)
	@JoinColumn(name = "address_id")
	public Address getAddress()
	{
		return address;
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "create_dt", insertable = false, updatable = false)
	public Date getCreated()
	{
		return new Date(thriftCustomer.created);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "update_dt", insertable = false, updatable = false)
	public Date getUpdated()
	{
		return new Date(thriftCustomer.updated);
	}

	void setUpdated(final Date updated)
	{
		thriftCustomer.updated = updated.getTime();
	}

	void setCreated(final Date updated)
	{
		thriftCustomer.created = updated.getTime();
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "password", unique = false, nullable = false, length = 64)
	public String getPassword()
	{
		return thriftCustomer.password;
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

		if (!(obj instanceof CustomerImpl))
		{
			return false;
		}

		final CustomerImpl other = (CustomerImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getFirstName(), other.getFirstName())
				.append(getLastName(), other.getLastName()).append(getEmail(), other.getEmail()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getFirstName()).append(getLastName())
				.append(getEmail()).hashCode();
	}

	@Override
	public void setFirstName(String firstName)
	{
		thriftCustomer.firstName = firstName;
	}

	@Override
	public void setLastName(String lastName)
	{
		thriftCustomer.lastName = lastName;
	}

	@Override
	public void setEmail(String email)
	{
		thriftCustomer.email = email;
	}

	@Override
	public void setPassword(String password)
	{
		thriftCustomer.password = password;
	}

	@Override
	public void setAddress(Address address)
	{
		this.address = address;
	}

}