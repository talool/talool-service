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
						"OR d.dealOffer.merchant.id=:merchantId order by d.createdUpdated.created desc") })
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
