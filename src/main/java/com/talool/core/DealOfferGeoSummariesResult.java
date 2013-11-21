package com.talool.core;

import java.util.List;

/**
 * Simple wrapper for result
 * 
 * @author clintz
 * 
 */
public final class DealOfferGeoSummariesResult
{
	private List<DealOfferGeoSummary> summaries;
	private boolean usedFallback = false;

	public DealOfferGeoSummariesResult(List<DealOfferGeoSummary> summaries, boolean usedFallback)
	{
		super();
		this.summaries = summaries;
		this.usedFallback = usedFallback;
	}

	public List<DealOfferGeoSummary> getSummaries()
	{
		return summaries;
	}

	public boolean usedFallback()
	{
		return usedFallback;
	}

}
