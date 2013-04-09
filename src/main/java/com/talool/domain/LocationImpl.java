package com.talool.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.talool.core.Location;

/**
 * 
 * @author clintz
 * 
 */
@Embeddable
public class LocationImpl implements Location
{
	private static final long serialVersionUID = 6198741717898788920L;

	@Column(name = "longitude", unique = false, nullable = true)
	@Access(AccessType.FIELD)
	private Double longitude;

	@Column(name = "latitude", unique = false, nullable = true)
	@Access(AccessType.FIELD)
	private Double latitude;

	public LocationImpl()
	{}

	public LocationImpl(final Double longitude, final Double latitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public Double getLongitude()
	{
		return longitude;
	}

	@Override
	public void setLongitude(Double longitude)
	{
		this.longitude = longitude;
	}

	@Override
	public Double getLatitude()
	{
		return latitude;
	}

	@Override
	public void setLatitude(Double latitude)
	{
		this.latitude = latitude;
	}

}
