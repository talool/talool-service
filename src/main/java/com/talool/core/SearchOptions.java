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

	private Boolean ascending = true;
	private String sortProperty;
	private Integer maxResults;
	private Integer page;

	private SearchOptions(final Builder builder)
	{
		this.sortProperty = builder.sortProperty;
		this.maxResults = builder.maxResults;
		this.page = builder.page;
		this.ascending = builder.ascending;
	}

	public static class Builder
	{
		public SearchOptions build()
		{
			Preconditions.checkArgument((sortProperty == null && ascending == null)
					| (sortProperty != null && ascending != null),
					"Sort property and ascending both must be set");

			return new SearchOptions(this);

		}

		public Builder ascending(Boolean ascending)
		{
			this.ascending = ascending;
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

		private Boolean ascending = true;
		private String sortProperty;
		private int maxResults;
		private int page;

	}

	public boolean isAscending()
	{
		return ascending;
	}

	public String getSortProperty()
	{
		return sortProperty;
	}

	public Integer getMaxResults()
	{
		return maxResults;
	}

	public Integer getPage()
	{
		return page;
	}

}
