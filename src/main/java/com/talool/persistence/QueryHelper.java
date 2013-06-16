package com.talool.persistence;

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
	private static final ImmutableMap<String, String> EMPTY_IMMUTABLE_PROPS = ImmutableMap
			.<String, String> of();

	public static final String MERCHANTS_WITHIN_METERS =
			"select merchant.merchant_id as merchantId,merchant.merchant_name as name, mloc.*,cat.*,ST_Distance( mloc.geom,'${point}',true) "
					+ "as distanceInMeters FROM public.merchant as merchant, public.category as cat, public.merchant_location as mloc "
					+ "where ST_DWithin(mloc.geom,'${point}',${distanceInMeters},true) "
					+ "and mloc.merchant_id=merchant.merchant_id and merchant.category_id=cat.category_id and merchant.is_discoverable=${isDiscoverable}";

	public static final String DEAL_ACQUIRES =
			"select dealAcquire from DealAcquireImpl as dealAcquire left join fetch dealAcquire.deal as d " +
					"left join fetch d.image left join fetch d.merchant as m left join fetch m.locations as l " +
					"left join fetch l.merchantImage left join fetch l.logo " +
					"where dealAcquire.deal.merchant.id=:merchantId and dealAcquire.customer.id=:customerId";

	private static final String MERCHANT_ACQUIRES =
			"select distinct merchant from MerchantImpl merchant, DealAcquireImpl da," +
					"DealImpl d where da.customer.id=:customerId and da.deal.id=d.id and d.merchant.id=merchant.id";

	private static final String MERCHANT_ACQUIRES_BY_CAT_ID =
			"select distinct merchant from MerchantImpl merchant, DealAcquireImpl da,DealImpl d"
					+ " where da.customer.id=:customerId and da.deal.id=d.id and d.merchant.id=merchant.id and merchant.category.id=:categoryId";

	private static final String MERCHANT_MEDIAS =
			"from MerchantMediaImpl as merchantMedia where merchantMedia.merchantId=:merchantId and merchantMedia.mediaType in (:mediaTypes)";

	private static final String FAVORITE_MERCHANTS = "select merchant from MerchantImpl as merchant, FavoriteMerchantImpl as f where f.customerId=:customerId and f.merchantId=merchant.id";

	public enum QueryType
	{
		MerchantsWithinMeters(MERCHANTS_WITHIN_METERS,
				ImmutableMap.<String, String> builder()
						.put("merchant.name", "name")
						.put("merchant.isDiscoverable", "isDiscoverable")
						.put("merchant.locations.distanceInMeters", "distanceInMeters").build()),

		DealAcquires(DEAL_ACQUIRES, EMPTY_IMMUTABLE_PROPS),

		MerchantAcquires(MERCHANT_ACQUIRES, EMPTY_IMMUTABLE_PROPS),

		GetMerchantMedias(MERCHANT_MEDIAS, EMPTY_IMMUTABLE_PROPS),

		FavoriteMerchants(FAVORITE_MERCHANTS, ImmutableMap.<String, String> builder()
				.put("merchant.created", "merchant.createdUpdated.created").
				put("merchant.updated", "merchant.createdUpdated.updated").build()),

		MerchantAcquiresByCatId(MERCHANT_ACQUIRES_BY_CAT_ID, EMPTY_IMMUTABLE_PROPS);

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
