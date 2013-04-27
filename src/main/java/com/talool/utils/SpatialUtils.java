package com.talool.utils;


/**
 * Converion Utils
 * 
 * @author clintz
 * 
 */
public class SpatialUtils
{
	public static final float MILES_PER_METER = 0.00062137273f;
	public static final float METERS_PER_MILE = 1609.34f;
	public static final float METERS_PER_QTR_MILE = 402.336f;
	public static final float METERS_PER_FOOT = 3.28084f;

	public static double milesToMeters(final double miles)
	{
		return METERS_PER_MILE * miles;
	}

	public static double metersToMiles(final double meters)
	{
		return MILES_PER_METER * meters;
	}

	public static double metersToFeet(final double meters)
	{
		return METERS_PER_FOOT * meters;
	}

}
