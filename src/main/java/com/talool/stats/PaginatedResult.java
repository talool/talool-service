package com.talool.stats;

import java.util.List;

import com.talool.core.SearchOptions;

/**
 * A generic PaginatedResult object containing the result generated from the
 * SearchOptions object.
 * 
 * @author clintz
 * 
 */
public class PaginatedResult<T>
{
	private SearchOptions searchOptions;
	private Long totalResults;
	private List<T> results;

	public PaginatedResult(final SearchOptions searchOpts, final Long totalResults, final List<T> result)
	{
		this.searchOptions = searchOpts;
		this.totalResults = totalResults;
		this.results = result;
	}

	public SearchOptions getSearchOptions()
	{
		return searchOptions;
	}

	public Long getTotalResults()
	{
		return totalResults;
	}

	public List<T> getResults()
	{
		return results;
	}

}
