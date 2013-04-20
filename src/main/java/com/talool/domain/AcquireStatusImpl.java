package com.talool.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.talool.core.AcquireStatus;

/**
 * Acquire Status Impl
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "acquire_status", catalog = "public")
public class AcquireStatusImpl implements AcquireStatus
{
	private static final long serialVersionUID = 3720511015708695704L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_aq_seq")
	@SequenceGenerator(name = "my_aq_seq", sequenceName = "acquire_status_acquire_status_id_seq")
	@Column(name = "acquire_status_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "status", unique = true, nullable = false, length = 64)
	private String status;

	@Column(name = "create_dt", unique = false, insertable = false, updatable = false)
	private Date created;

	@Override
	public Date getCreated()
	{
		return created;
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

		if (!(obj instanceof AcquireStatusImpl))
		{
			return false;
		}

		final AcquireStatusImpl other = (AcquireStatusImpl) obj;

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
