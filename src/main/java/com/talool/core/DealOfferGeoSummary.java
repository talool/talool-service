package com.talool.core;

import java.util.Map;

/**
 * A summary object containing geo (distance from deal offer) and metadata for
 * the dealoffer
 * 
 * @author clintz
 * 
 */
public final class DealOfferGeoSummary
{
	private final DealOffer dealOffer;
	// base location of the deal offer
	private Double distanceInMeters;

	// closest merchant within the deal offer
	private Double closestMerchantInMeters;

	private Map<String, Long> longMetrics;
	private Map<String, Double> doubleMetrics;

	public DealOfferGeoSummary(final DealOffer dealOffer, final Double distanceInMeters, final Double closestMerchantInMeters,
			final Map<String, Long> longMetrics, final Map<String, Double> doubleMetrics)
	{
		super();
		this.distanceInMeters = distanceInMeters;
		this.closestMerchantInMeters = closestMerchantInMeters;
		this.dealOffer = dealOffer;
		this.longMetrics = longMetrics;
		this.doubleMetrics = doubleMetrics;
	}

	public Double getDistanceInMeters()
	{
		return distanceInMeters;
	}

	public Double getClosestMerchantInMeters()
	{
		return closestMerchantInMeters;
	}

	public DealOffer getDealOffer()
	{
		return dealOffer;
	}

	public Map<String, Long> getLongMetrics()
	{
		return longMetrics;
	}

	public Map<String, Double> getDoubleMetrics()
	{
		return doubleMetrics;
	}

}
