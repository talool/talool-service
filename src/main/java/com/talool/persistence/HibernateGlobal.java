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
				query =
				"select gr from GiftRequestImpl as gr, CustomerImpl as cust,CustomerSocialAccountImpl as csa "
						+
						"left join fetch gr.fromCustomer left join fetch gr.dealAcquire as da left join fetch da.deal as d " +
						"left join fetch d.merchant left join fetch d.image " +
						"where gr.requestStatus in (:requestStatus) AND " +
						"(csa.customer.id=:customerId AND (gr.toFacebookId=csa.loginId OR gr.toCustomer.id=:customerId) " +
						" and cust.id=:customerId) OR " +
						"(cust.id=:customerId AND cust.email=gr.toEmail AND csa.customer.id=cust.id) " +
						"order by gr.created asc)"
		),
})
@TypeDefs({
		@TypeDef(name = "sexType", typeClass = GenericEnumUserType.class, parameters = {
				@Parameter(name = "enumClass", value = "com.talool.core.Sex"),
				@Parameter(name = "identifierMethod", value = "getLetter"),
				@Parameter(name = "valueOfMethod", value = "valueByLetter") }),

		@TypeDef(name = "dealType", typeClass = GenericEnumUserType.class, parameters =
		{ @Parameter(name = "enumClass", value = "com.talool.core.DealType") }),

		@TypeDef(name = "requestStatus", typeClass = GenericEnumUserType.class, parameters =
		{ @Parameter(name = "enumClass", value = "com.talool.core.RequestStatus") }),

		@TypeDef(name = "mediaType", typeClass = GenericEnumUserType.class, parameters =
		{ @Parameter(name = "enumClass", value = "com.talool.core.MediaType") })

})
public class HibernateGlobal
{}
