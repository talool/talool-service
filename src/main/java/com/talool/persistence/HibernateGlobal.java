package com.talool.persistence;

import javax.persistence.MappedSuperclass;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.talool.domain.DealAcquireImpl;

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

})
@NamedNativeQueries({
		@NamedNativeQuery(
				resultClass = DealAcquireImpl.class,
				name = "giftedDealAcquires",
				query = "select dac.* from deal_acquire as dac " +
						"where (deal_acquire_id) in " +
						"(select gr.deal_acquire_id from gift_request as gr, customer as cust," +
						"customer_social_account as csa " +
						"where gr.customer_id=:customerId OR " +
						"(cust.customer_id=:customerId AND gr.to_email=cust.email) OR " +
						"(csa.customer_id=:customerId AND gr.to_facebook_id=csa.login_id) " +
						"order by gr.create_dt asc)")
})
@TypeDefs({
		@TypeDef(name = "sexType", typeClass = GenericEnumUserType.class, parameters = {
				@Parameter(name = "enumClass", value = "com.talool.core.Sex"),
				@Parameter(name = "identifierMethod", value = "getLetter"),
				@Parameter(name = "valueOfMethod", value = "valueByLetter") }),

		@TypeDef(name = "dealType", typeClass = GenericEnumUserType.class, parameters =
		{ @Parameter(name = "enumClass", value = "com.talool.core.DealType") }),

		@TypeDef(name = "mediaType", typeClass = GenericEnumUserType.class, parameters =
		{ @Parameter(name = "enumClass", value = "com.talool.core.MediaType") })

})
public class HibernateGlobal
{}
