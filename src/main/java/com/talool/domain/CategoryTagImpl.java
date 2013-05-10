package com.talool.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.talool.core.Category;
import com.talool.core.CategoryTag;
import com.talool.core.Tag;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "category_tag", catalog = "public")
public class CategoryTagImpl implements CategoryTag
{
	private static final long serialVersionUID = -6607975046888363358L;

	@Embeddable
	public static class CategoryTagPK implements Serializable
	{
		private static final long serialVersionUID = 5154400944254037286L;

		@ManyToOne(fetch = FetchType.EAGER, targetEntity = TagImpl.class)
		@Fetch(FetchMode.JOIN)
		@JoinColumn(name = "tag_id")
		private Tag categoryTag;

		@ManyToOne(fetch = FetchType.EAGER, targetEntity = CategoryImpl.class)
		@Fetch(FetchMode.JOIN)
		@JoinColumn(name = "category_id")
		private Category category;

		public Tag getCategoryTag()
		{
			return categoryTag;
		}

		public void setCategoryTag(Tag categoryTag)
		{
			this.categoryTag = categoryTag;
		}

		public Category getCategory()
		{
			return category;
		}

		public void setCategory(Category category)
		{
			this.category = category;
		}
	}

	@EmbeddedId
	private final CategoryTagPK primaryKey;

	public CategoryTagImpl(final Category category, Tag tag)
	{
		primaryKey = new CategoryTagPK();
		primaryKey.setCategory(category);
		primaryKey.setCategoryTag(tag);
	}

	public CategoryTagImpl()
	{
		primaryKey = new CategoryTagPK();
	}

	@Override
	public Tag getCategoryTag()
	{
		return primaryKey.getCategoryTag();
	}

	@Override
	public Category getCategory()
	{
		return primaryKey.getCategory();
	}

	public void setCategoryTag(Tag categoryTag)
	{
		this.primaryKey.categoryTag = categoryTag;
	}

	public void setCategory(Category category)
	{
		this.primaryKey.category = category;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
