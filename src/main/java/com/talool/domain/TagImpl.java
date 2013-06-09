package com.talool.domain;

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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.talool.core.Tag;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "tag", catalog = "public")
@Cache(region = "TagCache", usage = CacheConcurrencyStrategy.READ_WRITE)
public class TagImpl implements Tag
{

	private static final long serialVersionUID = 2955686897079229757L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_tag_seq")
	@SequenceGenerator(name = "my_tag_seq", sequenceName = "tag_tag_id_seq")
	@Column(name = "tag_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "name", unique = true, nullable = false, length = 32)
	private String name;

	@Override
	public Integer getId()
	{
		return id;
	}

	@Override
	public String getName()
	{
		return name;
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

		if (!(obj instanceof TagImpl))
		{
			return false;
		}

		final TagImpl other = (TagImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getName(), other.getName()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getName()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public void setName(String tagName)
	{
		this.name = tagName;
	}
}
