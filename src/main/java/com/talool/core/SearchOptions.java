package com.talool.core;

import java.io.Serializable;

import com.google.common.base.Preconditions;

/**
 * 
 * @author clintz
 * 
 */
public class SearchOptions implements Serializable
{
	private static final long serialVersionUID = -7730917442100669680L;

	public enum SortType
	{
		Asc, Desc
	};

	private SortType sortType;

	private String sortProperty;
	private int maxResults;
	private int page;

	private SearchOptions(final Builder builder)
	{
		this.sortProperty = builder.sortProperty;
		this.maxResults = builder.maxResults;
		this.page = builder.page;
		this.sortType = builder.sortType;
	}

	public static class Builder
	{
		public SearchOptions build()
		{
			Preconditions.checkArgument((sortProperty == null && sortType == null)
					|| (sortProperty != null && sortType != null),
					"Sort property and sortType work together. Both must be either null or not null");

			return new SearchOptions(this);

		}

		public Builder sortType(SortType sortType)
		{
			this.sortType = sortType;
			return this;
		}

		public Builder sortProperty(String sortProperty)
		{
			this.sortProperty = sortProperty;
			return this;
		}

		public Builder maxResults(int maxResults)
		{
			this.maxResults = maxResults;
			return this;
		}

		public Builder page(int page)
		{
			this.page = page;
			return this;
		}

		private SortType sortType;
		private String sortProperty;
		private int maxResults;
		private int page;

	}

	public SortType getSortType()
	{
		return sortType;
	}

	public String getSortProperty()
	{
		return sortProperty;
	}

	public int getMaxResults()
	{
		return maxResults;
	}

	public int getPage()
	{
		return page;
	}

}
