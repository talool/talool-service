package com.talool.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A basic class containing deal offer metrics
 * 
 * @author clintz
 * 
 */
public final class DealOfferMetrics
{
	private UUID dealOfferId;
	private Map<String, Long> longMetrics;
	private Map<String, Double> doubleMetrics;

	public enum MetricType
	{
		TotalMerchants, TotalRedemptions, TotalDeals, TotalAcquires
	};

	public DealOfferMetrics(final UUID dealOfferId)
	{
		this.dealOfferId = dealOfferId;
	}

	public UUID getDealOfferId()
	{
		return dealOfferId;
	}

	public Map<String, Long> getLongMetrics()
	{
		return longMetrics;
	}

	public DealOfferMetrics setLongMetrics(final Map<String, Long> longMetrics)
	{
		this.longMetrics = longMetrics;
		return this;
	}

	public DealOfferMetrics addLongMetric(final String metric, final Long value)
	{
		if (longMetrics == null)
		{
			longMetrics = new HashMap<String, Long>();
		}

		longMetrics.put(metric, value);

		return this;
	}

	public DealOfferMetrics addDoubleMetric(final String metric, final Double value)
	{
		if (doubleMetrics == null)
		{
			doubleMetrics = new HashMap<String, Double>();
		}

		doubleMetrics.put(metric, value);

		return this;
	}

	public DealOfferMetrics addDoubleMetric(final MetricType metricType, final Double value)
	{
		if (doubleMetrics == null)
		{
			doubleMetrics = new HashMap<String, Double>();
		}

		doubleMetrics.put(metricType.toString(), value);

		return this;
	}

	public DealOfferMetrics addLongMetric(final MetricType metricType, final Long value)
	{
		if (longMetrics == null)
		{
			longMetrics = new HashMap<String, Long>();
		}

		longMetrics.put(metricType.toString(), value);

		return this;
	}

	public Map<String, Double> getDoubleMetrics()
	{
		return doubleMetrics;
	}

}
