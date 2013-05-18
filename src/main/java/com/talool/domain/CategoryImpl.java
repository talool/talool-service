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

import com.talool.core.Category;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "category", catalog = "public")
public class CategoryImpl implements Category
{
	private static final long serialVersionUID = 2955686897079229757L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_cat_seq")
	@SequenceGenerator(name = "my_cat_seq", sequenceName = "category_category_id_seq")
	@Column(name = "category_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "category_name", unique = true, nullable = false, length = 32)
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

		if (!(obj instanceof CategoryImpl))
		{
			return false;
		}

		final CategoryImpl other = (CategoryImpl) obj;

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
