package com.talool.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
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

import com.talool.core.AquireStatus;

/**
 * Aquire Status Impl
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "aquire_status", catalog = "public")
public class AquireStatusImpl implements AquireStatus
{
	private static final long serialVersionUID = 3720511015708695704L;
	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_aq_seq")
	@SequenceGenerator(name = "my_aq_seq", sequenceName = "aquire_status_aquire_status_id_seq")
	@Column(name = "aquire_status_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "status", unique = true, nullable = false, length = 64)
	private String status;

	@Embedded
	private CreatedUpdated createdUpdated;

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
	public Integer getId()
	{
		return id;
	}

	@Override
	public String getStatus()
	{
		return status;
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

		if (!(obj instanceof AquireStatusImpl))
		{
			return false;
		}

		final AquireStatusImpl other = (AquireStatusImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getStatus(), other.getStatus()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getStatus()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
