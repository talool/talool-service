package com.talool.utils;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderStatus;
import com.talool.core.MerchantLocation;
import com.talool.core.service.ServiceException;
import com.talool.service.ErrorCode;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * 
 * @author clintz
 * 
 */
public class HttpUtils
{
	public static Point getGeometry(final String addr1, final String addr2, final String city,
			final String state) throws IOException, ServiceException
	{
		final String address = buildAddress(addr1, addr2, city, state);

		final Geocoder geocoder = new Geocoder();
		final GeocoderRequest geoRequest = new GeocoderRequestBuilder().setAddress(address)
				.setLanguage("en").getGeocoderRequest();

		final GeocodeResponse geoResponse = geocoder.geocode(geoRequest);

		// Throw a service exception is something bad happens
		GeocoderStatus status = geoResponse.getStatus();
		if (status.equals(GeocoderStatus.ERROR) || 
			status.equals(GeocoderStatus.UNKNOWN_ERROR) ||
			status.equals(GeocoderStatus.REQUEST_DENIED))
		{
			throw new ServiceException(ErrorCode.GEOCODER_ERROR);
		}
		else if (status.equals(GeocoderStatus.INVALID_REQUEST) || 
				 status.equals(GeocoderStatus.ZERO_RESULTS))
		{
			throw new ServiceException(ErrorCode.MERCHANT_LOCATION_GEOMETRY_NULL);
		}
		else if (status.equals(GeocoderStatus.OVER_QUERY_LIMIT))
		{
			throw new ServiceException(ErrorCode.GEOCODER_OVER_QUERY_LIMIT);
		}
		
		GeocoderGeometry geometry = geoResponse.getResults().get(0).getGeometry();

		final GeometryFactory factory = new GeometryFactory(
				new PrecisionModel(PrecisionModel.FLOATING), 4326);

		final Point point = factory.createPoint(new Coordinate(geometry.getLocation().getLng()
				.doubleValue(), geometry
				.getLocation().getLat().doubleValue()));

		return point;
	}
	
	public static Point getGeometry(final MerchantLocation loc) throws IOException, ServiceException
	{
		return HttpUtils.getGeometry(loc.getAddress1(), loc.getAddress2(), loc.getCity(), loc.getStateProvinceCounty());
	}

	public static String buildAddress(final String addr1, final String addr2, final String city,
			final String state)
	{
		final StringBuilder sb = new StringBuilder();

		sb.append(addr1);
		if (StringUtils.isNotEmpty(addr2))
		{
			sb.append(" ").append(addr2);
		}

		sb.append(", ").append(city).append(", ").append(state);
		return sb.toString();
	}
	
	public static String buildAddress(final MerchantLocation loc)
	{
		return HttpUtils.buildAddress(loc.getAddress1(), loc.getAddress2(), loc.getCity(), loc.getStateProvinceCounty());
	}

}
