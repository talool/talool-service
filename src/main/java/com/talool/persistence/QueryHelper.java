package com.talool.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.text.StrSubstitutor;
import org.hibernate.Query;

import com.google.common.collect.ImmutableMap;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;
import com.talool.core.SearchOptions;

/**
 * Query Helper for HQL and native SQL
 * 
 * @author clintz
 * 
 */
public final class QueryHelper
{
	public static final String ORDER_BY = " ORDER BY ";
	public static final String ASC = " ASC ";
	public static final String DESC = " DESC ";
	public static final String LIMIT = " LIMIT ";
	public static final String OFFSET = " OFFSET ";

	private static final ImmutableMap<String, String> EMPTY_IMMUTABLE_PROPS = ImmutableMap
			.<String, String> of();

	private static final String DEAL_OFFER_BASIC_STATS =
			"select dof.deal_offer_id as dealOfferId,count(distinct d.merchant_id) as totalMerchants,count(d.deal_id) as totalDeals " +
					"from merchant as m,deal as d,deal_offer as dof " +
					"where d.deal_offer_id in (select deal_offer_id from deal_offer ) " +
					"and d.deal_offer_id = dof.deal_offer_id and m.merchant_id=d.merchant_id " +
					"group by dof.deal_offer_id ";
	// "order by totalMerchants desc";

	private static final String CUSTOMER_SUMMARY =
			"select c.customer_id as customerId,c.email as email,c.first_name as firstName,c.last_name as lastName,"
					+ "c.create_dt as registrationDate,(select count(*) "
					+ "from deal_acquire as d "
					+ "where d.customer_id = c.customer_id "
					+ "and d.acquire_status='REDEEMED' "
					+ ") as redemptions,"
					+ "(select array_to_string(array(select distinct d.title from deal_offer as d,customer as cust,"
					+ "deal_offer_purchase as dof WHERE d.deal_offer_id=dof.deal_offer_id and cust.customer_id=c.customer_id and cust.customer_id=dof.customer_id), ', ')) "
					+ "as commaSeperatedDealOfferTitles from customer as c ";

	private static final String PUBLISHER_CUSTOMER_SUMMARY =
			"select c.customer_id as customerId,c.email as email,c.first_name as firstName,c.last_name as lastName,"
					+ "c.create_dt as registrationDate,(select count(*) "
					+ "from deal_acquire as d "
					+ "where d.customer_id = c.customer_id "
					+ "and d.acquire_status='REDEEMED' "
					+ ") as redemptions,"
					+ "(select array_to_string(array(select distinct d.title from deal_offer as d,customer as cust,"
					+ "deal_offer_purchase as dof WHERE d.merchant_id=:publisherMerchantId and d.deal_offer_id=dof.deal_offer_id and cust.customer_id=c.customer_id and cust.customer_id=dof.customer_id), ', ')) "
					+ "as commaSeperatedDealOfferTitles from customer as c " +
					"where c.customer_id in (" +
					"select c.customer_id from customer as c, deal_offer_purchase as dop,deal_offer as dof " +
					"where dop.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId and c.customer_id=dop.customer_id)";

	private static final String PUBLISHER_CUSTOMER_EMAIL_SUMMARY =
			"select c.customer_id as customerId,c.email as email,c.first_name as firstName,c.last_name as lastName,"
					+ "c.create_dt as registrationDate,(select count(*) "
					+ "from deal_acquire as d "
					+ "where d.customer_id = c.customer_id "
					+ "and d.acquire_status='REDEEMED' "
					+ ") as redemptions,"
					+ "(select array_to_string(array(select distinct d.title from deal_offer as d,customer as cust,"
					+ "deal_offer_purchase as dof WHERE d.merchant_id=:publisherMerchantId and d.deal_offer_id=dof.deal_offer_id and cust.customer_id=c.customer_id and cust.customer_id=dof.customer_id), ', ')) "
					+ "as commaSeperatedDealOfferTitles from customer as c "
					+
					"where c.customer_id in ("
					+
					"select c.customer_id from customer as c, deal_offer_purchase as dop,deal_offer as dof "
					+
					"where dop.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId and c.customer_id=dop.customer_id and c.email like :email)";

	private static final String CUSTOMER_SUMMARY_CNT = "select count(*) as totalResults from customer";

	private static final String PUBLISHER_CUSTOMER_SUMMARY_CNT = "select count(distinct c.customer_id) as totalResults from customer as c, deal_offer_purchase as dop,deal_offer as dof "
			+ "where dop.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId and c.customer_id=dop.customer_id";

	private static final String PUBLISHER_CUSTOMER_EMAIL_SUMMARY_CNT = "select count(distinct c.customer_id) as totalResults from customer as c, deal_offer_purchase as dop,deal_offer as dof "
			+ "where dop.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId and c.customer_id=dop.customer_id and c.email like :email";

	private static final String ACTIVATION_SUMMARY = "select n1.doid as \"dealOfferId\",n1.title, n1.total as \"totalCodes\", n2.ta as \"totalActivations\" from  "
			+
			"(select a.deal_offer_id as doid,count(*) as ta from activation_code a where customer_id is not null group by a.deal_offer_id) n2 "
			+
			"right outer join " +
			"(select d.deal_offer_id as doid,d.title,count(*) as total from deal_offer d " +
			"LEFT OUTER JOIN activation_code a ON (d.deal_offer_id=a.deal_offer_id) " +
			"where d.merchant_id=:merchantId group by d.deal_offer_id,d.title) n1 " +
			"on (n2.doid=n1.doid)";

	public static final String MERCHANTS_WITHIN_METERS =
			"select merchant.merchant_id as merchantId,merchant.merchant_name as name, mloc.*,cat.*,ST_Distance( mloc.geom,'${point}',true) "
					+ "as distanceInMeters FROM public.merchant as merchant, public.category as cat, public.merchant_location as mloc "
					+ "where ST_DWithin(mloc.geom,'${point}',${distanceInMeters},true) "
					+ "and mloc.merchant_id=merchant.merchant_id and merchant.category_id=cat.category_id and merchant.is_discoverable=${isDiscoverable}";

	public static final String ACTIVE_DEAL_OFFER_IDS_WITHIN_METERS =
			"select dof.deal_offer_id as dealOfferId," +
					"ST_Distance( dof.geom, '${point}',true) as distanceInMeters " +
					"from public.deal_offer as dof " +
					"where ST_DWithin(dof.geom,'${point}',${distanceInMeters},true) and " +
					"dof.is_active=true and (expires is null OR expires>now())";

	public static final String ACTIVE_DEAL_OFFER_IDS =
			"select deal_offer_id as dealOfferId " +
					"from public.deal_offer " +
					"where is_active=true and (expires is null OR expires>now())";

	public static final String DEAL_ACQUIRES =
			"select distinct dealAcquire from DealAcquireImpl as dealAcquire left join fetch dealAcquire.deal as d " +
					"left join fetch d.image left join fetch d.merchant as m left join fetch m.locations as l " +
					"left join fetch l.merchantImage left join fetch l.logo " +
					"where dealAcquire.deal.merchant.id=:merchantId and dealAcquire.customer.id=:customerId";

	private static final String MERCHANT_ACQUIRES =
			"select distinct merchant from MerchantImpl merchant, DealAcquireImpl da, "
					+ "DealImpl d left join fetch merchant.locations where da.customer.id=:customerId " +
					"and da.deal.id=d.id and d.merchant.id=merchant.id";

	private static final String MERCHANT_ACQUIRES_LOCATION =
			"select merchant.merchant_id as merchantId,merchant.merchant_name as merchantName,merchant.category_id as categoryId,"
					+
					"location.merchant_location_id,location.merchant_location_name,location.email,location.website_url,location.phone,location.address1,location.address2,"
					+
					"location.city,location.state_province_county,location.zip,location.country,location.geom," +
					"merchantLogo.media_url as merchantLogo,merchantImage.media_url as merchantImage," +
					"ST_Distance( location.geom,'${point}',true) as distanceInMeters " +
					"from " +
					"(select distinct m.* from deal_acquire as da,merchant as m,deal as d " +
					"where da.customer_id=:customerId and m.merchant_id=d.merchant_id and d.deal_id=da.deal_id) as merchant, " +
					"merchant_location as location " +
					"left outer join merchant_media as merchantLogo on (merchantLogo.merchant_media_id=location.logo_url_id) " +
					"left outer join merchant_media as merchantImage on (merchantImage.merchant_media_id=location.merchant_image_id) " +
					"where merchant.merchant_id=location.merchant_id";

	private static final String MERCHANT_ACQUIRES_BY_CAT_ID =
			"select distinct merchant from MerchantImpl merchant, DealAcquireImpl da,DealImpl d"
					+ " where da.customer.id=:customerId and da.deal.id=d.id and d.merchant.id=merchant.id and merchant.category.id=:categoryId";

	private static final String MERCHANT_MEDIAS =
			"from MerchantMediaImpl as merchantMedia where merchantMedia.merchantId=:merchantId and merchantMedia.mediaType in (:mediaTypes)";

	private static final String FAVORITE_MERCHANTS = "select merchant from MerchantImpl as merchant, FavoriteMerchantImpl as f " +
			"left join fetch merchant.locations " +
			"where f.customerId=:customerId and f.merchantId=merchant.id";

	private static final String DEALS_BY_DEAL_OFFER_ID = "select d from DealImpl as d left join fetch d.image left join fetch d.merchant as merchant "
			+
			"left join fetch merchant.locations where d.dealOffer.id=:dealOfferId";

	private static final String MERCHANTS_BY_DEAL_OFFER_ID = "select distinct merchant from MerchantImpl as merchant,DealImpl as dof where dof.dealOffer.id=:dealOfferId and dof.merchant.id=merchant.id";

	public enum QueryType
	{
		MerchantsWithinMeters(MERCHANTS_WITHIN_METERS,
				ImmutableMap.<String, String> builder()
						.put("merchant.name", "name")
						.put("merchant.isDiscoverable", "isDiscoverable")
						.put("merchant.locations.distanceInMeters", "distanceInMeters").build()),

		ActiveDealOfferIDsWithinMeters(ACTIVE_DEAL_OFFER_IDS_WITHIN_METERS, EMPTY_IMMUTABLE_PROPS),

		ActiveDealOfferIDs(ACTIVE_DEAL_OFFER_IDS, EMPTY_IMMUTABLE_PROPS),

		DealAcquires(DEAL_ACQUIRES, EMPTY_IMMUTABLE_PROPS),

		MerchantsByDealOfferId(MERCHANTS_BY_DEAL_OFFER_ID, ImmutableMap.<String, String> builder()
				.put("merchant.name", "merchant.name").build()),

		DealOfferBasicStats(DEAL_OFFER_BASIC_STATS, EMPTY_IMMUTABLE_PROPS),

		CustomerSummary(CUSTOMER_SUMMARY, EMPTY_IMMUTABLE_PROPS),

		PublisherCustomerSummary(PUBLISHER_CUSTOMER_SUMMARY, EMPTY_IMMUTABLE_PROPS),

		PublisherCustomerEmailSummary(PUBLISHER_CUSTOMER_EMAIL_SUMMARY, EMPTY_IMMUTABLE_PROPS),

		PublisherCustomerEmailSummaryCnt(PUBLISHER_CUSTOMER_EMAIL_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS),

		CustomerSummaryCnt(CUSTOMER_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS),

		PublisherCustomerSummaryCnt(PUBLISHER_CUSTOMER_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS),

		MerchantAcquires(MERCHANT_ACQUIRES, EMPTY_IMMUTABLE_PROPS),

		MerchantAcquiresLocation(MERCHANT_ACQUIRES_LOCATION,
				ImmutableMap.<String, String> builder()
						.put("merchant.name", "merchant.merchant_name")
						.put("merchant.isDiscoverable", "isDiscoverable")
						.put("merchant.locations.distanceInMeters", "distanceInMeters").build()),

		DealsByDealOfferId(DEALS_BY_DEAL_OFFER_ID, EMPTY_IMMUTABLE_PROPS),

		GetMerchantMedias(MERCHANT_MEDIAS, EMPTY_IMMUTABLE_PROPS),

		ActivationSummary(ACTIVATION_SUMMARY, EMPTY_IMMUTABLE_PROPS),

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
			if (searchOpts.getFirstResult() == null)
			{
				query.setFirstResult(searchOpts.getMaxResults() * searchOpts.getPage());
			}
			else
			{
				query.setFirstResult(Integer.valueOf(searchOpts.getFirstResult().toString()));
			}
		}
	}

	// TODO NOT FINISHED - revist, this is tough building dynamic eager loaded
	// props (nested props are hard)
	public static String buildQuery(Class clazz, UUID identifier, final SearchOptions searchOpts,
			final String[] eagerlyLoadedProps)
	{
		final StringBuilder sb = new StringBuilder();

		sb.append("select a from ").append(clazz.getSimpleName()).append(" as a ");

		// image merchant.locations
		if (eagerlyLoadedProps != null)
		{
			final HashMap<String, String> eagerAliases = new HashMap<String, String>();

			for (String prop : eagerlyLoadedProps)
			{
				String[] parts = prop.split(".");

				for (String part : parts)
				{
					if (eagerAliases.containsKey(part))
					{
						continue;
					}
					else
					{
						// eagerAliases.put()
					}
				}

			}

		}

		return null;

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
				sb.append(ORDER_BY).append(sortProp);

				if (searchOpts.isAscending())
				{
					sb.append(ASC);
				}
				else
				{
					sb.append(DESC);
				}
			}

		}

		if (ignoreLimitOffset || searchOpts.getMaxResults() == null)
		{
			return sb.toString();
		}

		sb.append(LIMIT).append(searchOpts.getMaxResults());
		sb.append(OFFSET).append(searchOpts.getMaxResults() * searchOpts.getPage());

		return sb.toString();
	}

	public static void applySearchOptions(final SearchOptions searchOpts, final Search search)
	{
		if (searchOpts != null)
		{
			if (searchOpts.getMaxResults() != null)
			{
				search.setMaxResults(searchOpts.getMaxResults());
			}
			if (searchOpts.getPage() != null)
			{
				search.setPage(searchOpts.getPage());
			}
			if (searchOpts.getSortProperty() != null)
			{
				search.addSort(new Sort(searchOpts.getSortProperty(), !searchOpts.isAscending()));
			}
		}
	}

	public static String buildQuery(final QueryType queryType, final Map<String, Object> params,
			final SearchOptions searchOpts)
	{
		return buildQuery(queryType, params, searchOpts, false);
	}
}
