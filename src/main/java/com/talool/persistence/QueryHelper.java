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
import com.talool.domain.PropertyCriteria;

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

	private static final ImmutableMap<String, String> EMPTY_IMMUTABLE_PROPS = ImmutableMap.<String, String> of();

	private static final String DEAL_OFFER_BASIC_STATS = "select dof.deal_offer_id as dealOfferId,count(distinct d.merchant_id) as totalMerchants,count(d.deal_id) as totalDeals "
			+ "from merchant as m,deal as d,deal_offer as dof "
			+ "where d.deal_offer_id in (select deal_offer_id from deal_offer ) "
			+ "and d.deal_offer_id = dof.deal_offer_id and m.merchant_id=d.merchant_id " + "group by dof.deal_offer_id ";
	// "order by totalMerchants desc";

	private static final String DEAL_OFFER_SUMMARY = "select DISTINCT o.deal_offer_id as offerId, o.merchant_id as merchantId, o.title as title, o.summary as summary, "
			+ "o.deal_type as offerType, o.price as price,o.scheduled_start_dt as scheduledStartDate, o.scheduled_end_dt as scheduledEndDate, o.is_active as isActive,"
			+ "l.merchant_location_name AS locationName, l.address1 AS address1, l.address2 AS address2, l.city AS city, l.state_province_county AS state, "
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_background_id) as backgroundUrl,"
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_icon_id) as iconUrl,"
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_logo_id) as logoUrl,"
			+ "(select merchant_name from merchant as m where m.merchant_id = o.merchant_id) as merchantName,"
			+ "(select m.merchant_name from merchant as m, merchant_account as ma where m.merchant_id = ma.merchant_id and ma.merchant_account_id = o.created_by_merchant_account_id) as createdByMerchantName "
			+ "from deal_offer as o "
			+ "LEFT JOIN (SELECT merchant_location_id, merchant_id, merchant_location_name, address1, address2, city, state_province_county, geom, create_dt FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.merchant_location_id = ml2.merchant_location_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (o.geom = l.geom AND o.merchant_id = l.merchant_id) ";

	private static final String DEAL_OFFER_TITLE_SUMMARY = "select DISTINCT o.deal_offer_id as offerId, o.merchant_id as merchantId, o.title as title, o.summary as summary, "
			+ "o.deal_type as offerType, o.price as price, o.scheduled_start_dt as scheduledStartDate, o.scheduled_end_dt as scheduledEndDate, o.is_active as isActive,"
			+ "l.merchant_location_name AS locationName, l.address1 AS address1, l.address2 AS address2, l.city AS city, l.state_province_county AS state, "
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_background_id) as backgroundUrl,"
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_icon_id) as iconUrl,"
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_logo_id) as logoUrl,"
			+ "(select merchant_name from merchant as m where m.merchant_id = o.merchant_id) as merchantName,"
			+ "(select m.merchant_name from merchant as m, merchant_account as ma where m.merchant_id = ma.merchant_id and ma.merchant_account_id = o.created_by_merchant_account_id) as createdByMerchantName "
			+ "from deal_offer as o "
			+ "LEFT JOIN (SELECT merchant_location_id, merchant_id, merchant_location_name, address1, address2, city, state_province_county, geom, create_dt FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.merchant_location_id = ml2.merchant_location_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (o.geom = l.geom AND o.merchant_id = l.merchant_id) " + "where o.title like :title ";

	private static final String PUBLISHER_DEAL_OFFER_SUMMARY = "select DISTINCT o.deal_offer_id as offerId, o.merchant_id as merchantId, o.title as title, o.summary as summary, "
			+ "o.deal_type as offerType, o.price as price, o.scheduled_start_dt as scheduledStartDate, o.scheduled_end_dt as scheduledEndDate, o.is_active as isActive,"
			+ "l.merchant_location_name AS locationName, l.address1 AS address1, l.address2 AS address2, l.city AS city, l.state_province_county AS state, "
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_background_id) as backgroundUrl,"
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_icon_id) as iconUrl,"
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_logo_id) as logoUrl,"
			+ "(select merchant_name from merchant as m where m.merchant_id = o.merchant_id) as merchantName,"
			+ "(select m.merchant_name from merchant as m, merchant_account as ma where m.merchant_id = ma.merchant_id and ma.merchant_account_id = o.created_by_merchant_account_id) as createdByMerchantName "
			+ "from deal_offer as o "
			+ "LEFT JOIN (SELECT merchant_location_id, merchant_id, merchant_location_name, address1, address2, city, state_province_county, geom, create_dt FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.merchant_location_id = ml2.merchant_location_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (o.geom = l.geom AND o.merchant_id = l.merchant_id) " + "where o.merchant_id=:publisherMerchantId ";

	private static final String PUBLISHER_DEAL_OFFER_TITLE_SUMMARY = "select DISTINCT o.deal_offer_id as offerId, o.merchant_id as merchantId, o.title as title, o.summary as summary, "
			+ "o.deal_type as offerType, o.price as price, o.scheduled_start_dt as scheduledStartDate, o.scheduled_end_dt as scheduledEndDate, o.is_active as isActive,"
			+ "l.merchant_location_name AS locationName, l.address1 AS address1, l.address2 AS address2, l.city AS city, l.state_province_county AS state, "
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_background_id) as backgroundUrl,"
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_icon_id) as iconUrl,"
			+ "(select media_url from merchant_media as mm where mm.merchant_media_id = o.deal_offer_logo_id) as logoUrl,"
			+ "(select merchant_name from merchant as m where m.merchant_id = o.merchant_id) as merchantName,"
			+ "(select m.merchant_name from merchant as m, merchant_account as ma where m.merchant_id = ma.merchant_id and ma.merchant_account_id = o.created_by_merchant_account_id) as createdByMerchantName "
			+ "from deal_offer as o "
			+ "LEFT JOIN (SELECT merchant_location_id, merchant_id, merchant_location_name, address1, address2, city, state_province_county, geom, create_dt FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.merchant_location_id = ml2.merchant_location_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (o.geom = l.geom AND o.merchant_id = l.merchant_id) " + "where o.title like :title and o.merchant_id=:publisherMerchantId";

	private static final String MERCHANT_CODE_SUMMARY_CNT = "SELECT count(distinct c.code) AS totalResults "
			+ "FROM merchant_code AS c, merchant_code_group AS g " + "WHERE c.merchant_code_group_id=g.merchant_code_group_id "
			+ "AND g.merchant_id = :merchantId ";

	private static final String MERCHANT_CODE_SUMMARY = "SELECT g.code_group_title AS name, g.code_group_notes AS email, c.code AS code, "
			+ "(SELECT count(distinct deal_offer_purchase_id) FROM deal_offer_purchase "
			+ "WHERE properties->'merchant_code' = c.code ) AS purchaseCount "
			+ "FROM merchant_code AS c, merchant_code_group AS g "
			+ "WHERE c.merchant_code_group_id=g.merchant_code_group_id " + "AND g.merchant_id = :merchantId ";

	private static final String MERCHANT_SUMMARY_CNT = "SELECT count(distinct m.merchant_id) AS totalResults FROM merchant AS m "
			+ "JOIN (SELECT merchant_location_id, merchant_id, address1, address2, city, state_province_county, zip, phone, website_url, logo_url_id, merchant_image_id, create_dt FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.merchant_id = ml2.merchant_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (m.merchant_id = l.merchant_id) WHERE m.is_discoverable = 't' ";

	private static final String MERCHANT_NAME_SUMMARY_CNT = "SELECT count(distinct m.merchant_id) AS totalResults FROM merchant AS m "
			+ "JOIN (SELECT merchant_location_id, merchant_id, address1, address2, city, state_province_county, zip, phone, website_url, logo_url_id, merchant_image_id, create_dt FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.merchant_id = ml2.merchant_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (m.merchant_id = l.merchant_id) WHERE m.is_discoverable = 't' AND m.merchant_name ILIKE :name ";

	private static final String PUBLISHER_MERCHANT_SUMMARY_CNT = "SELECT count(distinct m.merchant_id) AS totalResults FROM merchant AS m "
			+ "JOIN (SELECT merchant_location_id, merchant_id, address1, address2, city, state_province_county, zip, phone, website_url, logo_url_id, merchant_image_id, create_dt, created_by_merchant_id FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.created_by_merchant_id=:publisherMerchantId AND ml1.merchant_id = ml2.merchant_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (m.merchant_id = l.merchant_id) WHERE m.is_discoverable = 't' ";

	private static final String PUBLISHER_MERCHANT_NAME_SUMMARY_CNT = "SELECT count(distinct m.merchant_id) AS totalResults FROM merchant AS m "
			+ "JOIN (SELECT merchant_location_id, merchant_id, address1, address2, city, state_province_county, zip, phone, website_url, logo_url_id, merchant_image_id, create_dt, created_by_merchant_id FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.created_by_merchant_id=:publisherMerchantId AND ml1.merchant_id = ml2.merchant_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (m.merchant_id = l.merchant_id) WHERE m.is_discoverable = 't' AND m.merchant_name ILIKE :name ";

	private static final String MERCHANT_SUMMARY = "SELECT m.merchant_id AS merchantId, m.merchant_name AS name,  %#m.properties AS properties, "
			+ "l.address1 AS address1, l.address2 AS address2, l.city AS city, l.state_province_county AS state, l.zip AS zip, l.phone AS phone, l.website_url AS website, "
			+ "(SELECT category_name FROM category AS c WHERE c.category_id = m.category_id) AS category, "
			+ "(SELECT mm.media_url FROM merchant_media AS mm WHERE mm.merchant_media_id = l.logo_url_id) AS logoUrl, "
			+ "(SELECT mm.media_url FROM merchant_media AS mm WHERE mm.merchant_media_id = l.merchant_image_id) AS imageUrl, "
			+ "(SELECT count(*) FROM merchant_location AS ml WHERE ml.merchant_id = m.merchant_id) AS locationCount, "
			+ "(SELECT count(*) FROM deal AS d WHERE d.merchant_id = m.merchant_id) AS dealCount, "
			+ "(SELECT count(*) FROM merchant_account AS ma WHERE ma.merchant_id = m.merchant_id) AS merchantAccountCount "
			+ "FROM merchant AS m "
			+ "JOIN (SELECT merchant_location_id, merchant_id, address1, address2, city, state_province_county, zip, phone, website_url, logo_url_id, merchant_image_id, create_dt FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.merchant_id = ml2.merchant_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (m.merchant_id = l.merchant_id) WHERE m.is_discoverable = 't' ";

	private static final String MERCHANT_NAME_SUMMARY = "SELECT m.merchant_id AS merchantId, m.merchant_name AS name,  %#m.properties AS properties, "
			+ "l.address1 AS address1, l.address2 AS address2, l.city AS city, l.state_province_county AS state, l.zip AS zip, l.phone AS phone, l.website_url AS website, "
			+ "(SELECT category_name FROM category AS c WHERE c.category_id = m.category_id) AS category, "
			+ "(SELECT mm.media_url FROM merchant_media AS mm WHERE mm.merchant_media_id = l.logo_url_id) AS logoUrl, "
			+ "(SELECT mm.media_url FROM merchant_media AS mm WHERE mm.merchant_media_id = l.merchant_image_id) AS imageUrl, "
			+ "(SELECT count(*) FROM merchant_location AS ml WHERE ml.merchant_id = m.merchant_id) AS locationCount, "
			+ "(SELECT count(*) FROM deal AS d WHERE d.merchant_id = m.merchant_id) AS dealCount, "
			+ "(SELECT count(*) FROM merchant_account AS ma WHERE ma.merchant_id = m.merchant_id) AS merchantAccountCount "
			+ "FROM merchant AS m "
			+ "JOIN (SELECT merchant_location_id, merchant_id, address1, address2, city, state_province_county, zip, phone, website_url, logo_url_id, merchant_image_id, create_dt FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.merchant_id = ml2.merchant_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (m.merchant_id = l.merchant_id) WHERE m.is_discoverable = 't' AND m.merchant_name ILIKE :name ";

	private static final String PUBLISHER_MERCHANT_SUMMARY = "SELECT m.merchant_id AS merchantId, m.merchant_name AS name,  %#m.properties AS properties, "
			+ "l.address1 AS address1, l.address2 AS address2, l.city AS city, l.state_province_county AS state, l.zip AS zip, l.phone AS phone, l.website_url AS website, "
			+ "(SELECT category_name FROM category AS c WHERE c.category_id = m.category_id) AS category, "
			+ "(SELECT mm.media_url FROM merchant_media AS mm WHERE mm.merchant_media_id = l.logo_url_id) AS logoUrl, "
			+ "(SELECT mm.media_url FROM merchant_media AS mm WHERE mm.merchant_media_id = l.merchant_image_id) AS imageUrl, "
			+ "(SELECT count(*) FROM merchant_location AS ml WHERE ml.merchant_id = m.merchant_id) AS locationCount, "
			+ "(SELECT count(*) FROM deal AS d WHERE d.merchant_id = m.merchant_id) AS dealCount, "
			+ "(SELECT count(*) FROM merchant_account AS ma WHERE ma.merchant_id = m.merchant_id) AS merchantAccountCount "
			+ "FROM merchant AS m "
			+ "JOIN (SELECT merchant_location_id, merchant_id, address1, address2, city, state_province_county, zip, phone, website_url, logo_url_id, merchant_image_id, create_dt, created_by_merchant_id FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.created_by_merchant_id=:publisherMerchantId AND ml1.merchant_id = ml2.merchant_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (m.merchant_id = l.merchant_id) WHERE m.is_discoverable = 't' ";

	private static final String PUBLISHER_MERCHANT_NAME_SUMMARY = "SELECT m.merchant_id AS merchantId, m.merchant_name AS name, %#m.properties AS properties, "
			+ "l.address1 AS address1, l.address2 AS address2, l.city AS city, l.state_province_county AS state, l.zip AS zip, l.phone AS phone, l.website_url AS website, "
			+ "(SELECT category_name FROM category AS c WHERE c.category_id = m.category_id) AS category, "
			+ "(SELECT mm.media_url FROM merchant_media AS mm WHERE mm.merchant_media_id = l.logo_url_id) AS logoUrl, "
			+ "(SELECT mm.media_url FROM merchant_media AS mm WHERE mm.merchant_media_id = l.merchant_image_id) AS imageUrl, "
			+ "(SELECT count(*) FROM merchant_location AS ml WHERE ml.merchant_id = m.merchant_id) AS locationCount, "
			+ "(SELECT count(*) FROM deal AS d WHERE d.merchant_id = m.merchant_id) AS dealCount, "
			+ "(SELECT count(*) FROM merchant_account AS ma WHERE ma.merchant_id = m.merchant_id) AS merchantAccountCount "
			+ "FROM merchant AS m "
			+ "JOIN (SELECT merchant_location_id, merchant_id, address1, address2, city, state_province_county, zip, phone, website_url, logo_url_id, merchant_image_id, create_dt, created_by_merchant_id FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.created_by_merchant_id=:publisherMerchantId AND ml1.merchant_id = ml2.merchant_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (m.merchant_id = l.merchant_id) WHERE m.is_discoverable = 't' AND m.merchant_name ILIKE :name ";

	private static final String CUSTOMER_SUMMARY = "select c.customer_id as customerId,c.email as email,c.first_name as firstName,c.last_name as lastName,"
			+ "c.create_dt as registrationDate,(select count(*) "
			+ "from deal_acquire as d "
			+ "where d.customer_id = c.customer_id "
			+ "and d.acquire_status='REDEEMED' "
			+ ") as redemptions,"
			+ "(select array_to_string(array(select distinct d.title from deal_offer as d,customer as cust,"
			+ "deal_offer_purchase as dof WHERE d.deal_offer_id=dof.deal_offer_id and cust.customer_id=c.customer_id and cust.customer_id=dof.customer_id), ', ')) "
			+ "as commaSeperatedDealOfferTitles from customer as c ";

	private static final String PUBLISHER_CUSTOMER_SUMMARY = "select c.customer_id as customerId,c.email as email,c.first_name as firstName,c.last_name as lastName,"
			+ "c.create_dt as registrationDate,(select count(*) "
			+ "from deal_acquire as d "
			+ "where d.customer_id = c.customer_id "
			+ "and d.acquire_status='REDEEMED' "
			+ ") as redemptions,"
			+ "(select array_to_string(array(select distinct d.title from deal_offer as d,customer as cust,"
			+ "deal_offer_purchase as dof WHERE d.merchant_id=:publisherMerchantId and d.deal_offer_id=dof.deal_offer_id and cust.customer_id=c.customer_id and cust.customer_id=dof.customer_id), ', ')) "
			+ "as commaSeperatedDealOfferTitles from customer as c "
			+ "where c.customer_id in ("
			+ "select c.customer_id from customer as c, deal_offer_purchase as dop,deal_offer as dof "
			+ "where dop.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId and c.customer_id=dop.customer_id)";

	private static final String PUBLISHER_CUSTOMER_REDEMPTION_CNT = "select count(c.customer_id) as totalResults from customer as c, deal_acquire as daq,deal as d,deal_offer as dof "
			+ "where daq.customer_id = c.customer_id "
			+ "and daq.acquire_status='REDEEMED' and daq.deal_id=d.deal_id and d.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId";

	private static final String PUBLISHER_EMAIL_GIFT_CNT = "select count(*) as totalResults from gift as g, deal_acquire as daq,deal as d,deal_offer as dof "
			+ "where g.to_email is not null and g.deal_acquire_id=daq.deal_acquire_id and daq.deal_id=d.deal_id and d.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId";

	private static final String PUBLISHER_FACEBOOK_GIFT_CNT = "select count(*) as totalResults from gift as g, deal_acquire as daq,deal as d,deal_offer as dof "
			+ "where g.to_facebook_id is not null and g.deal_acquire_id=daq.deal_acquire_id and daq.deal_id=d.deal_id and d.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId";

	private static final String PUBLISHER_CUSTOMER_EMAIL_SUMMARY = "select c.customer_id as customerId,c.email as email,c.first_name as firstName,c.last_name as lastName,"
			+ "c.create_dt as registrationDate,(select count(*) "
			+ "from deal_acquire as daq,deal as d,deal_offer as dof "
			+ "where daq.customer_id = c.customer_id "
			+ "and daq.acquire_status='REDEEMED' and daq.deal_id=d.deal_id and d.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId"
			+ ") as redemptions,"
			+ "(select array_to_string(array(select distinct d.title from deal_offer as d,customer as cust,"
			+ "deal_offer_purchase as dof WHERE d.merchant_id=:publisherMerchantId and d.deal_offer_id=dof.deal_offer_id and cust.customer_id=c.customer_id and cust.customer_id=dof.customer_id), ', ')) "
			+ "as commaSeperatedDealOfferTitles from customer as c "
			+ "where c.customer_id in ("
			+ "select c.customer_id from customer as c, deal_offer_purchase as dop,deal_offer as dof "
			+ "where dop.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId and c.customer_id=dop.customer_id and c.email like :email)";

	private static final String ALL_CUSTOMER_EMAIL_SUMMARY = "select c.customer_id as customerId,c.email as email,c.first_name as firstName,c.last_name as lastName,"
			+ "c.create_dt as registrationDate,(select count(*) "
			+ "from deal_acquire as daq,deal as d,deal_offer as dof "
			+ "where daq.customer_id = c.customer_id "
			+ "and daq.acquire_status='REDEEMED' and daq.deal_id=d.deal_id and d.deal_offer_id=dof.deal_offer_id"
			+ ") as redemptions,"
			+ "(select array_to_string(array(select distinct d.title from deal_offer as d,customer as cust,"
			+ "deal_offer_purchase as dof WHERE d.deal_offer_id=dof.deal_offer_id and cust.customer_id=c.customer_id and cust.customer_id=dof.customer_id), ', ')) "
			+ "as commaSeperatedDealOfferTitles from customer as c " + "where c.email like :email";

	private static final String ALL_CUSTOMER_EMAIL_SUMMARY_CNT = "select count(*) as totalResults from customer where email like :email";

	private static final String CUSTOMER_SUMMARY_CNT = "select count(*) as totalResults from customer";

	private static final String DEAL_OFFER_SUMMARY_CNT = "select count(*) as totalResults from deal_offer";

	private static final String PUBLISHER_CUSTOMER_SUMMARY_CNT = "select count(distinct c.customer_id) as totalResults from customer as c, deal_offer_purchase as dop,deal_offer as dof "
			+ "where dop.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId and c.customer_id=dop.customer_id";

	private static final String PUBLISHER_DEAL_OFFER_SUMMARY_CNT = "select count(deal_offer_id) as totalResults from deal_offer "
			+ "where merchant_id=:publisherMerchantId";

	private static final String PUBLISHER_CUSTOMER_EMAIL_SUMMARY_CNT = "select count(distinct c.customer_id) as totalResults from customer as c, deal_offer_purchase as dop,deal_offer as dof "
			+ "where dop.deal_offer_id=dof.deal_offer_id and dof.merchant_id=:publisherMerchantId and c.customer_id=dop.customer_id and c.email like :email";

	private static final String DEAL_OFFER_TITLE_SUMMARY_CNT = "select count(*) as totalResults from deal_offer where title like :title";

	private static final String PUBLISHER_DEAL_OFFER_TITLE_SUMMARY_CNT = "select count(*) as totalResults from deal_offer "
			+ "where merchant_id=:publisherMerchantId and title like :title";

	private static final String ACTIVATION_SUMMARY = "select n1.doid as \"dealOfferId\",n1.title, n1.total as \"totalCodes\", n2.ta as \"totalActivations\" from  "
			+ "(select a.deal_offer_id as doid,count(*) as ta from activation_code a where customer_id is not null group by a.deal_offer_id) n2 "
			+ "right outer join "
			+ "(select d.deal_offer_id as doid,d.title,count(*) as total from deal_offer d "
			+ "LEFT OUTER JOIN activation_code a ON (d.deal_offer_id=a.deal_offer_id) "
			+ "where d.merchant_id=:merchantId group by d.deal_offer_id,d.title) n1 " + "on (n2.doid=n1.doid)";

	public static final String MERCHANTS_WITHIN_METERS = "select merchant.merchant_id as merchantId,merchant.merchant_name as name, mloc.*,cat.*,ST_Distance( mloc.geom,'${point}',true) "
			+ "as distanceInMeters FROM public.merchant as merchant, public.category as cat, public.merchant_location as mloc "
			+ "where ST_DWithin(mloc.geom,'${point}',${distanceInMeters},true) "
			+ "and mloc.merchant_id=merchant.merchant_id and merchant.category_id=cat.category_id and merchant.is_discoverable=${isDiscoverable}";

	public static final String ACTIVE_DEAL_OFFER_IDS_WITHIN_METERS = "select dof.deal_offer_id as dealOfferId,"
			+ "ST_Distance( dof.geom, '${point}',true) as distanceInMeters "
			+ "from public.deal_offer as dof "
			+ "where dof.is_active=true AND now() at time zone 'utc' BETWEEN scheduled_start_dt AND scheduled_end_dt AND ST_DWithin(dof.geom,'${point}',${distanceInMeters},true)";

	public static final String ACTIVE_DEAL_OFFER_IDS = "select deal_offer_id as dealOfferId " + "from public.deal_offer "
			+ "where is_active=true AND now() at time zone 'utc' BETWEEN scheduled_start_dt AND scheduled_end_dt";

	public static final String DEAL_ACQUIRES = "select distinct dealAcquire from DealAcquireImpl as dealAcquire left join fetch dealAcquire.deal as d "
			+ "left join fetch d.image left join fetch d.merchant as m left join fetch m.locations as l "
			+ "left join fetch l.merchantImage left join fetch l.logo "
			+ "where dealAcquire.deal.merchant.id=:merchantId and dealAcquire.customer.id=:customerId";

	private static final String MERCHANT_ACQUIRES = "select distinct merchant from MerchantImpl merchant, DealAcquireImpl da, "
			+ "DealImpl d left join fetch merchant.locations where da.customer.id=:customerId "
			+ "and da.deal.id=d.id and d.merchant.id=merchant.id";

	private static final String MERCHANT_ACQUIRES_LOCATION = "select merchant.merchant_id as merchantId,merchant.merchant_name as merchantName,merchant.category_id as categoryId,"
			+ "location.merchant_location_id,location.merchant_location_name,location.email,location.website_url,location.phone,location.address1,location.address2,"
			+ "location.city,location.state_province_county,location.zip,location.country,location.geom,"
			+ "merchantLogo.media_url as logo_url_id,merchantImage.media_url as merchant_image_id,"
			+ "ST_Distance( location.geom,'${point}',true) as distanceInMeters "
			+ "from "
			+ "(select distinct m.* from deal_acquire as da,merchant as m,deal as d "
			+ "where da.customer_id=:customerId and m.merchant_id=d.merchant_id and d.deal_id=da.deal_id) as merchant, "
			+ "merchant_location as location "
			+ "left outer join merchant_media as merchantLogo on (merchantLogo.merchant_media_id=location.logo_url_id) "
			+ "left outer join merchant_media as merchantImage on (merchantImage.merchant_media_id=location.merchant_image_id) "
			+ "where merchant.merchant_id=location.merchant_id";

	private static final String MERCHANT_ACQUIRES_BY_CAT_ID = "select distinct merchant from MerchantImpl merchant, DealAcquireImpl da,DealImpl d"
			+ " where da.customer.id=:customerId and da.deal.id=d.id and d.merchant.id=merchant.id and merchant.category.id=:categoryId";

	private static final String MERCHANT_MEDIAS = "from MerchantMediaImpl as merchantMedia where merchantMedia.merchantId=:merchantId and merchantMedia.mediaType in (:mediaTypes)";

	private static final String STOCK_MEDIA_BY_TAGS = "select distinct merchantMedia from MerchantMediaImpl as merchantMedia join merchantMedia.tags t "
			+ "where merchantMedia.merchantId=:merchantId and merchantMedia.mediaType in (:mediaTypes) " + "and t.name in (:tags)";

	private static final String STOCK_MEDIA_WITHOUT_TAGS = "select distinct merchantMedia from MerchantMediaImpl as merchantMedia left join merchantMedia.tags t "
			+ "where merchantMedia.merchantId=:merchantId and merchantMedia.mediaType in (:mediaTypes) "
			+ "group by merchantMedia having count(t)=0";

	private static final String FAVORITE_MERCHANTS = "select merchant from MerchantImpl as merchant, FavoriteMerchantImpl as f "
			+ "left join fetch merchant.locations " + "where f.customerId=:customerId and f.merchantId=merchant.id";

	// DEALS_BY_DEAL_OFFER_ID will return dups, need to filter via
	// setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
	private static final String DEALS_BY_DEAL_OFFER_ID = "select d from DealImpl as d left join fetch d.image left join fetch d.merchant as merchant "
			+ "left join fetch merchant.locations where d.dealOffer.id=:dealOfferId";

	// DEALS_BY_DEAL_OFFER_ID will return dups, need to filter via
	// setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
	private static final String ACTIVE_DEALS_BY_DEAL_OFFER_ID = "select d from DealImpl as d left join fetch d.image left join fetch d.merchant as merchant "
			+ "left join fetch merchant.locations where d.dealOffer.id=:dealOfferId and d.isActive=true and d.expires>:expiresDate";

	private static final String MERCHANTS_BY_DEAL_OFFER_ID = "select distinct merchant from MerchantImpl as merchant,DealImpl as dof where dof.dealOffer.id=:dealOfferId and dof.merchant.id=merchant.id";

	private static final String DEAL_SUMMARY = "SELECT d.deal_id AS dealId, d.deal_offer_id AS offerId, d.merchant_id AS merchantId, d.title AS title, d.summary AS summary, d.details AS details, "
			+ "d.expires AS expires, d.is_active AS isActive, l.city AS merchantCity, l.state_province_county AS merchantState, "
			+ "(SELECT array_to_string(array(SELECT DISTINCT t.name FROM tag AS t, deal_tag AS dt WHERE dt.deal_id = d.deal_id AND dt.tag_id = t.tag_id),', ')) AS tags, "
			+ "(SELECT mm.media_url FROM merchant_media AS mm WHERE mm.merchant_media_id = d.image_id) AS imageUrl,"
			+ "(SELECT m.merchant_name FROM merchant AS m WHERE m.merchant_id = d.merchant_id) AS merchantName,"
			+ "(SELECT m.merchant_name FROM merchant AS m, merchant_account AS ma "
			+ "WHERE ma.merchant_id = m.merchant_id AND ma.merchant_account_id = d.created_by_merchant_account_id) AS createdByMerchantName,"
			+ "(SELECT m.merchant_id FROM merchant AS m, merchant_account AS ma "
			+ "WHERE ma.merchant_id = m.merchant_id AND ma.merchant_account_id = d.created_by_merchant_account_id) AS createdByMerchantId,"
			+ "(SELECT o.title FROM deal_offer AS o WHERE o.deal_offer_id = d.deal_offer_id) AS offerTitle,"
			+ "(SELECT count(*) FROM deal_acquire AS da WHERE da.deal_id = d.deal_id) AS acquireCount,"
			+ "(SELECT count(*) FROM deal_acquire AS da WHERE da.deal_id = d.deal_id AND da.acquire_status = 'REDEEMED') AS redemptionCount,"
			+ "(SELECT count(*) FROM deal_acquire AS da WHERE da.deal_id = d.deal_id AND "
			+ "(da.acquire_status = 'PENDING_ACCEPT_CUSTOMER_SHARE' OR da.acquire_status = 'ACCEPTED_CUSTOMER_SHARE' OR da.acquire_status = 'REJECTED_CUSTOMER_SHARE')) AS giftCount "
			+ "FROM deal AS d "
			+ "JOIN (SELECT merchant_location_id, merchant_id, city, state_province_county, create_dt FROM merchant_location ml1 "
			+ "WHERE (merchant_location_id, create_dt) IN (SELECT merchant_location_id, create_dt FROM merchant_location ml2 WHERE ml1.merchant_id = ml2.merchant_id ORDER BY create_dt LIMIT 1)) l "
			+ "ON (d.merchant_id = l.merchant_id) " + "WHERE d.deal_offer_id = :offerId";

	private static final String DEAL_SUMMARY_CNT = "SELECT count(*) AS totalResults FROM deal AS d WHERE d.deal_offer_id = :offerId";

	private static final String PUBLISHER_DAILY_CODE_CNT = "select sum(total_codes) AS codeCount from merchant_code_group where publisher_id = :publisherId and create_dt > current_date";

	private static final String MOVE_DEALS = "UPDATE deal "
			+ "SET deal_offer_id = :dealOfferId, updated_by_merchant_account_id = :merchantAccountId " + "WHERE deal_id IN (:dealIds)";

	private static final String RECIPIENT_STATUSES = "from RecipientStatusImpl as recipientStatus left join fetch recipientStatus.customer where recipientStatus.deliveryStatus != 'SUCCESS' AND recipientStatus.messagingJobId=:messagingJobId";

	private static final String RECIPIENT_STATUSES_CNT = "select count(*) from RecipientStatusImpl where messagingJobId=:messagingJobId";

	public enum QueryType
	{
		MerchantsWithinMeters(MERCHANTS_WITHIN_METERS, ImmutableMap.<String, String> builder().put("merchant.name", "name")
				.put("merchant.isDiscoverable", "isDiscoverable").put("merchant.locations.distanceInMeters", "distanceInMeters").build()),

		ActiveDealOfferIDsWithinMeters(ACTIVE_DEAL_OFFER_IDS_WITHIN_METERS, EMPTY_IMMUTABLE_PROPS),

		ActiveDealOfferIDs(ACTIVE_DEAL_OFFER_IDS, EMPTY_IMMUTABLE_PROPS),

		DealAcquires(DEAL_ACQUIRES, EMPTY_IMMUTABLE_PROPS),

		MerchantsByDealOfferId(MERCHANTS_BY_DEAL_OFFER_ID, ImmutableMap.<String, String> builder().put("merchant.name", "merchant.name")
				.build()),

		DealOfferBasicStats(DEAL_OFFER_BASIC_STATS, EMPTY_IMMUTABLE_PROPS),

		CustomerSummary(CUSTOMER_SUMMARY, EMPTY_IMMUTABLE_PROPS),

		PublisherCustomerSummary(PUBLISHER_CUSTOMER_SUMMARY, EMPTY_IMMUTABLE_PROPS),

		PublisherCustomerRedemptionTotal(PUBLISHER_CUSTOMER_REDEMPTION_CNT, EMPTY_IMMUTABLE_PROPS),

		PublisherCustomerEmailSummary(PUBLISHER_CUSTOMER_EMAIL_SUMMARY, EMPTY_IMMUTABLE_PROPS),

		PublisherCustomerEmailSummaryCnt(PUBLISHER_CUSTOMER_EMAIL_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS),

		CustomerSummaryCnt(CUSTOMER_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS),

		PublisherCustomerSummaryCnt(PUBLISHER_CUSTOMER_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS),

		AllCustomerEmailSummaryCnt(ALL_CUSTOMER_EMAIL_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS),

		AllCustomerEmailSummary(ALL_CUSTOMER_EMAIL_SUMMARY, EMPTY_IMMUTABLE_PROPS),

		PublisherEmailGiftCnt(PUBLISHER_EMAIL_GIFT_CNT, EMPTY_IMMUTABLE_PROPS),

		PublisherFacebookGiftCnt(PUBLISHER_FACEBOOK_GIFT_CNT, EMPTY_IMMUTABLE_PROPS),

		MerchantAcquires(MERCHANT_ACQUIRES, EMPTY_IMMUTABLE_PROPS),

		MerchantAcquiresLocation(MERCHANT_ACQUIRES_LOCATION, ImmutableMap.<String, String> builder()
				.put("merchant.name", "merchant.merchant_name").put("merchant.isDiscoverable", "isDiscoverable")
				.put("merchant.locations.distanceInMeters", "distanceInMeters").build()),

		DealsByDealOfferId(DEALS_BY_DEAL_OFFER_ID, EMPTY_IMMUTABLE_PROPS),

		ActiveDealsByDealOfferId(ACTIVE_DEALS_BY_DEAL_OFFER_ID, EMPTY_IMMUTABLE_PROPS),

		GetMerchantMedias(MERCHANT_MEDIAS, EMPTY_IMMUTABLE_PROPS),

		GetStockMediaByTags(STOCK_MEDIA_BY_TAGS, EMPTY_IMMUTABLE_PROPS),

		GetStockMediaWithoutTags(STOCK_MEDIA_WITHOUT_TAGS, EMPTY_IMMUTABLE_PROPS),

		ActivationSummary(ACTIVATION_SUMMARY, EMPTY_IMMUTABLE_PROPS),

		FavoriteMerchants(FAVORITE_MERCHANTS, ImmutableMap.<String, String> builder()
				.put("merchant.created", "merchant.createdUpdated.created").put("merchant.updated", "merchant.createdUpdated.updated").build()),

		MerchantAcquiresByCatId(MERCHANT_ACQUIRES_BY_CAT_ID, EMPTY_IMMUTABLE_PROPS),

		DealOfferSummary(DEAL_OFFER_SUMMARY, EMPTY_IMMUTABLE_PROPS), DealOfferSummaryCnt(DEAL_OFFER_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS), DealOfferTitleSummaryCnt(
				DEAL_OFFER_TITLE_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS), PublisherDealOfferSummaryCnt(PUBLISHER_DEAL_OFFER_SUMMARY_CNT,
				EMPTY_IMMUTABLE_PROPS), PublisherDealOfferTitleSummaryCnt(PUBLISHER_DEAL_OFFER_TITLE_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS), PublisherDealOfferSummary(
				PUBLISHER_DEAL_OFFER_SUMMARY, EMPTY_IMMUTABLE_PROPS), DealOfferTitleSummary(DEAL_OFFER_TITLE_SUMMARY, EMPTY_IMMUTABLE_PROPS), PublisherDealOfferTitleSummary(
				PUBLISHER_DEAL_OFFER_TITLE_SUMMARY, EMPTY_IMMUTABLE_PROPS),

		MerchantSummaryCnt(MERCHANT_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS), MerchantNameSummaryCnt(MERCHANT_NAME_SUMMARY_CNT,
				EMPTY_IMMUTABLE_PROPS), PublisherMerchantSummaryCnt(PUBLISHER_MERCHANT_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS), PublisherMerchantNameSummaryCnt(
				PUBLISHER_MERCHANT_NAME_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS), MerchantSummary(MERCHANT_SUMMARY, EMPTY_IMMUTABLE_PROPS), MerchantNameSummary(
				MERCHANT_NAME_SUMMARY, EMPTY_IMMUTABLE_PROPS), PublisherMerchantSummary(PUBLISHER_MERCHANT_SUMMARY, EMPTY_IMMUTABLE_PROPS), PublisherMerchantNameSummary(
				PUBLISHER_MERCHANT_NAME_SUMMARY, EMPTY_IMMUTABLE_PROPS),

		MerchantCodeSummaryCnt(MERCHANT_CODE_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS), MerchantCodeSummary(MERCHANT_CODE_SUMMARY,
				EMPTY_IMMUTABLE_PROPS),

		DealSummary(DEAL_SUMMARY, EMPTY_IMMUTABLE_PROPS), DealSummaryCnt(DEAL_SUMMARY_CNT, EMPTY_IMMUTABLE_PROPS),

		PublisherDailyCodeCnt(PUBLISHER_DAILY_CODE_CNT, EMPTY_IMMUTABLE_PROPS),

		MoveDeals(MOVE_DEALS, EMPTY_IMMUTABLE_PROPS),

		RecipientStatuses(RECIPIENT_STATUSES, EMPTY_IMMUTABLE_PROPS),

		RecipientStatusesCnt(RECIPIENT_STATUSES_CNT, EMPTY_IMMUTABLE_PROPS);

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
	public static String buildQuery(Class clazz, UUID identifier, final SearchOptions searchOpts, final String[] eagerlyLoadedProps)
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
	 *          - HQL seems to throw away any LIMIT/OFFSET set, so we can optionally ignore for HQL
	 * @return
	 */
	public static String buildQuery(final QueryType queryType, final Map<String, Object> params, final SearchOptions searchOpts,
			final boolean ignoreLimitOffset)
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

	public static String buildQuery(final QueryType queryType, final Map<String, Object> params, final SearchOptions searchOpts)
	{
		return buildQuery(queryType, params, searchOpts, false);
	}

	// TODO: this is just a quick hack to get property filters into the
	// MerchantSummary queries
	public static String buildPropertyQuery(final QueryType queryType, final PropertyCriteria propertyCriteria,
			final SearchOptions searchOpts)
	{
		if (propertyCriteria == null && searchOpts == null)
		{
			return queryType.getQuery();
		}

		final StringBuilder sb = new StringBuilder();
		sb.append(queryType.getQuery());

		if (propertyCriteria != null)
		{
			sb.append(propertyCriteria.buildRawFilterClause("m.properties"));
		}

		if (searchOpts != null)
		{
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

		}
		return sb.toString();
	}
}
