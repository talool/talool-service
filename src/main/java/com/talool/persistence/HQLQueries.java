package com.talool.persistence;

import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@MappedSuperclass
@NamedQueries({
		@NamedQuery(
				name = "allRelatedDeals",
				query = "select distinct d from DealImpl d " +
						"where d.merchant.id=:merchantId OR " +
						"d.dealOffer.createdByMerchantAccount.merchant.id=:merchantId " +
						"OR d.dealOffer.merchant.id=:merchantId order by d.createdUpdated.created desc") })
public class HQLQueries
{

}
