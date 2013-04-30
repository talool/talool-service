package com.talool.service;

import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.hibernate.Query;

import com.google.common.collect.ImmutableMap;
import com.talool.core.SearchOptions;

/**
 * Query Helper for HQL and native SQL
 * 
 * @author clintz
 * 
 */
public final class QueryHelper
{
	public static final String MERCHANTS_WITHIN_METERS =
			"select merchant.merchant_id as merchantId,merchant.merchant_name as name, mloc.*, addr.*,ST_Distance( mloc.geom,'${point}',true) "
					+
					"as distanceInMeters FROM public.merchant as merchant, public.merchant_managed_location as mmloc, "
					+
					"public.merchant_location as mloc, public.address as addr "
					+
					"WHERE merchant.merchant_id=mmloc.merchant_id AND mmloc.merchant_location_id=mloc.merchant_location_id "
					+
					"AND ST_DWithin(mloc.geom,'${point}',${distanceInMeters},true) and addr.address_id=mloc.address_id";

	public static final String GET_DEAL_ACQUIRES =
			"select dealAcquire from DealAcquireImpl dealAcquire, " +
					"DealImpl d where d.merchant.id=:merchantId " +
					"and dealAcquire.deal.id=d.id and dealAcquire.customer.id=:customerId";

	private static final String GET_MERCHANT_ACQUIRES =
			"select distinct merchant from MerchantImpl merchant, DealAcquireImpl da,"
					+
					"DealImpl d where da.customer.id=:customerId and da.deal.id=d.id and d.merchant.id=merchant.id";

	public enum QueryType
	{
		MerchantsWithinMeters(MERCHANTS_WITHIN_METERS,
				ImmutableMap.<String, String> builder()
						.put("merchant.name", "name")
						.put("merchant.locations.distanceInMeters", "distanceInMeters").build()),

		GetDealAcquires(GET_DEAL_ACQUIRES, ImmutableMap.<String, String> of()),

		GetMerchantAcquires(GET_MERCHANT_ACQUIRES, ImmutableMap.<String, String> of());

		private String query;
		private ImmutableMap<String, String> propertyColumnMap;

		private QueryType(final String query, final ImmutableMap<String, String> propertyColumnMap)
		{
			this.query = query;
			this.propertyColumnMap = propertyColumnMap;
		}

		public String getQuery()
		{
			return query;
		}

		public Map<String, String> getPropertyColumnMap()
		{
			return propertyColumnMap;
		}

	}

	public static void applyOffsetLimit(final Query query, final SearchOptions searchOpts)
	{
		if (searchOpts != null)
		{
			query.setMaxResults(searchOpts.getMaxResults());
			query.setFirstResult(searchOpts.getMaxResults() * searchOpts.getPage());
		}
	}

	/**
	 * Generates SQL String
	 * 
	 * @param queryType
	 * @param params
	 * @param searchOpts
	 * @param ignoreLimitOffset
	 *          - HQL seems to throw away any LIMIT/OFFSET set, so we can
	 *          optionally ignore for HQL
	 * @return
	 */
	public static String buildQuery(final QueryType queryType, final Map<String, Object> params,
			final SearchOptions searchOpts, final boolean ignoreLimitOffset)
	{
		if (params == null && searchOpts == null)
		{
			return queryType.getQuery();
		}

		final String newSql = StrSubstitutor.replace(queryType.getQuery(), params);

		if (searchOpts == null)
		{
			return newSql;
		}

		final StringBuilder sb = new StringBuilder();
		sb.append(newSql);

		if (searchOpts.getSortProperty() != null)
		{
			String sortProp = queryType.getPropertyColumnMap().get(searchOpts.getSortProperty());
			if (sortProp == null)
			{
				// try and use what is passed in
				sortProp = searchOpts.getSortProperty();
			}

			// only build sorts if the property is part of the column map!
			if (sortProp != null)
			{
				sb.append(" ORDER BY ");
				sb.append(sortProp);

				if (searchOpts.isAscending())
				{
					sb.append(" ASC");
				}
				else
				{
					sb.append(" DESC");
				}
			}

		}

		if (ignoreLimitOffset || searchOpts.getMaxResults() == null)
		{
			return sb.toString();
		}

		sb.append(" LIMIT ").append(searchOpts.getMaxResults());
		sb.append(" OFFSET ")
				.append(searchOpts.getMaxResults() * searchOpts.getPage());

		return sb.toString();
	}

	public static String buildQuery(final QueryType queryType, final Map<String, Object> params,
			final SearchOptions searchOpts)
	{
		return buildQuery(queryType, params, searchOpts, false);
	}
}
