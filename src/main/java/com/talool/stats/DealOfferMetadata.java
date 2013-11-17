package com.talool.stats;

import com.talool.core.DealOffer;

/**
 * 
 * @author clintz
 * 
 */
public final class DealOfferMetadata
{
	private final DealOffer dealOffer;
	private DealOfferMetrics dealOfferMetrics;

	public DealOfferMetadata(final DealOffer dealOffer, final DealOfferMetrics dealOfferMetrics)
	{
		super();
		this.dealOffer = dealOffer;
		this.dealOfferMetrics = dealOfferMetrics;
	}

	public DealOfferMetadata(final DealOffer dealOffer)
	{
		super();
		this.dealOffer = dealOffer;
	}

	public DealOffer getDealOffer()
	{
		return dealOffer;
	}

	public DealOfferMetrics getDealOfferMetrics()
	{
		return dealOfferMetrics;
	}

}
