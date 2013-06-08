package com.talool.persistence;

import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@MappedSuperclass
@NamedQueries({
		@NamedQuery(
				name = "allRelatedDeals",
				query = "select distinct d from DealImpl d " +
						"where d.merchant.id=:merchantId OR " +
						"d.dealOffer.createdByMerchantAccount.merchant.id=:merchantId " +
						"OR d.dealOffer.merchant.id=:merchantId order by d.createdUpdated.created desc"),

		@NamedQuery(
				name = "getDealRedemptionCode",
				query = "select d.id from DealAcquireImpl as d where d.deal.id=:dealId and d.redemptionCode=:redemptionCode"),

		@NamedQuery(
				name = "allCategoryTags",
				query = "select ct.primaryKey.category, ct.primaryKey.categoryTag from CategoryTagImpl as ct"),

		@NamedQuery(
				name = "deleteCustomerSocialAccount",
				query = "delete from CustomerSocialAccountImpl where customer.id=:customerId and socialNetwork.id=:socialNetworkId"),

		/*
		 * giftedDealAcquires is a great example of force fetching eagerly in HQL on
		 * entities we know we need to pull in. Avoids many select statements from
		 * lazy objects. Remember, FetchMode.LAZY/EAGER is for Criteria API
		 */
		@NamedQuery(
				name = "getGifts",
				query = "select gift from GiftImpl as gift left join fetch gift.fromCustomer " +
						"left join fetch gift.dealAcquire as da left join fetch da.deal as d " +
						"left join fetch d.image left join fetch d.merchant as m left join fetch m.locations " +
						"where gift.giftStatus in (:giftStatus) AND (gift.toCustomer.id=:customerId OR " +
						"gift.id in ( " +
						" select g.id from GiftImpl as g,CustomerSocialAccountImpl as cs " +
						" where cs.customer.id=:customerId and g.toFacebookId=cs.loginId) " +
						"OR gift.id in ( " +
						"   select g.id from GiftImpl as g,CustomerImpl as c " +
						"   where c.id=:customerId and c.email=g.toEmail))"
		),
})
@TypeDefs({
		@TypeDef(name = "sexType", typeClass = GenericEnumUserType.class, parameters = {
				@Parameter(name = "enumClass", value = "com.talool.core.Sex"),
				@Parameter(name = "identifierMethod", value = "getLetter"),
				@Parameter(name = "valueOfMethod", value = "valueByLetter") }),

		@TypeDef(name = "dealType", typeClass = GenericEnumUserType.class, parameters =
		{ @Parameter(name = "enumClass", value = "com.talool.core.DealType") }),

		@TypeDef(name = "giftStatus", typeClass = GenericEnumUserType.class, parameters =
		{ @Parameter(name = "enumClass", value = "com.talool.core.gift.GiftStatus") }),

		@TypeDef(name = "acquireStatus", typeClass = GenericEnumUserType.class, parameters =
		{ @Parameter(name = "enumClass", value = "com.talool.core.AcquireStatus") }),

		@TypeDef(name = "mediaType", typeClass = GenericEnumUserType.class, parameters =
		{ @Parameter(name = "enumClass", value = "com.talool.core.MediaType") })

})
public class HibernateGlobal
{}
