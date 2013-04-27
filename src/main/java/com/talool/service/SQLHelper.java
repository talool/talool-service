package com.talool.service;

import java.util.Map;
import java.util.Map.Entry;

/**
 * SQL Helper for native SQL
 * 
 * @author clintz
 * 
 */
public final class SQLHelper
{
	public static final String MERCHANTS_WITHIN_METERS =
			"SELECT entity.* ,ST_Distance( mloc.geom,'%point%',true) "
					+ "as distanceInMeters FROM public.merchant as entity, public.merchant_managed_location as mmloc, "
					+ "public.merchant_location as mloc "
					+ "WHERE entity.merchant_id=mmloc.merchant_id AND mmloc.merchant_location_id=mloc.merchant_location_id "
					+ " AND ST_DWithin(mloc.geom,'%point%',%distanceInMeters%,true) ORDER BY distanceInMeters ASC;";

	public static String getSQL(String sql, Map<String, Object> params)
	{
		// TODO - optimize the replaceAll to something way better!
		String newSql = MERCHANTS_WITHIN_METERS;

		for (final Entry<String, Object> entry : params.entrySet())
		{
			newSql = newSql.replaceAll("%" + entry.getKey() + "%", entry.getValue().toString());
		}

		return newSql;

	}
}
