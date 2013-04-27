package com.talool.core;

import java.text.DecimalFormat;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.talool.utils.SpatialUtils;

/**
 * 
 * @author clintz
 * 
 */
public class DistanceEntity<T>
{
	private T entity;
	private Double distanceInMeters;

	private static final DecimalFormat TWO_SIGS_FORMAT = new DecimalFormat("0.##");

	public DistanceEntity(final T entity, final Double distanceInMeters)
	{
		super();
		this.entity = entity;
		this.distanceInMeters = distanceInMeters;
	}

	public T getEntity()
	{
		return entity;
	}

	public Double getDistanceInMeters()
	{
		return distanceInMeters;
	}

	public Double getDistanceInMiles()
	{
		return SpatialUtils.metersToMiles((distanceInMeters));
	}

	public String getFriendlyDistance()
	{
		if (distanceInMeters < SpatialUtils.METERS_PER_QTR_MILE)
		{
			return TWO_SIGS_FORMAT.format(SpatialUtils.metersToFeet(distanceInMeters)) + " feet away";
		}
		if (distanceInMeters < SpatialUtils.METERS_PER_MILE)
		{
			return "less than 1 mile away";
		}

		return TWO_SIGS_FORMAT.format(SpatialUtils.metersToMiles(distanceInMeters)) + " miles away";
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

		if (!(obj instanceof DistanceEntity))
		{
			return false;
		}

		final DistanceEntity<T> other = (DistanceEntity<T>) obj;

		return new EqualsBuilder().append(getEntity(), other.getEntity())
				.append(getDistanceInMeters(), other.getDistanceInMeters()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getEntity()).append(getDistanceInMeters()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
