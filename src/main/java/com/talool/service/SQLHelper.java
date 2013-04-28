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
			"SELECT merchant.merchant_id as merchantId,merchant.merchant_name as merchantName, mloc.*, addr.*, "
					+ "ST_Distance( mloc.geom,'%point%',true) "
					+ "as distanceInMeters "
					+ "FROM public.merchant as merchant, public.merchant_managed_location as mmloc, "
					+ "public.merchant_location as mloc, public.address as addr "
					+ "WHERE merchant.merchant_id=mmloc.merchant_id AND mmloc.merchant_location_id=mloc.merchant_location_id "
					+ " AND ST_DWithin(mloc.geom,'%point%',%distanceInMeters%,true) and addr.address_id=mloc.address_id ORDER BY distanceInMeters ASC;";

	public static String getSQL(String sql, Map<String, Object> params)
	{
		// TODO - optimize the replaceAll to something way better!
		String newSql = sql;

		for (final Entry<String, Object> entry : params.entrySet())
		{
			newSql = newSql.replaceAll("%" + entry.getKey() + "%", entry.getValue().toString());
		}

		return newSql;

	}
}
