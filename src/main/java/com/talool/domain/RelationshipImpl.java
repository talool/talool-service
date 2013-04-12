package com.talool.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.talool.core.Customer;
import com.talool.core.Relationship;
import com.talool.core.RelationshipStatus;
import com.talool.persistence.GenericEnumUserType;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "relationship", catalog = "public")
@TypeDef(name = "relationshipStatus", typeClass = GenericEnumUserType.class, parameters = { @Parameter(name = "enumClass", value = "com.talool.core.RelationshipStatus") })
public class RelationshipImpl implements Relationship
{
	private static final long serialVersionUID = -8998925353726298712L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_rel_seq")
	@SequenceGenerator(name = "my_rel_seq", sequenceName = "relationship_relationship_id_seq")
	@Column(name = "relationship_id", unique = true, nullable = false)
	private Long id;

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "from_customer_id")
	private Customer fromCustomer;

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "to_customer_id")
	private Customer toCustomer;

	@Type(type = "relationshipStatus")
	@Column(name = "status", nullable = false, columnDefinition = "relationship_status")
	private RelationshipStatus relationshipStatus;

	@Embedded
	private CreatedUpdated createdUpdated;

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
	public Customer getFromCustomer()
	{
		return fromCustomer;
	}

	@Override
	public void setFromCustomer(Customer customer)
	{
		this.fromCustomer = customer;
	}

	@Override
	public Customer getToCustomer()
	{
		return toCustomer;
	}

	@Override
	public void setToCustomer(Customer friend)
	{
		this.toCustomer = friend;
	}

	@Override
	public RelationshipStatus getRelationshipStatus()
	{
		return relationshipStatus;
	}

	@Override
	public void setRelationshipStatus(RelationshipStatus relationShipStatus)
	{
		this.relationshipStatus = relationShipStatus;
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

		if (!(obj instanceof RelationshipImpl))
		{
			return false;
		}

		final RelationshipImpl other = (RelationshipImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getFromCustomer(), other.getFromCustomer())
				.append(getToCustomer(), other.getToCustomer()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getFromCustomer()).append(getToCustomer()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
