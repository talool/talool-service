package com.talool.core;

import java.io.Serializable;

/**
 * MerchantDealRedeemed
 * 
 * @author clintz
 * 
 */
public interface MerchantDealRedeemed extends Identifiable, Serializable, TimeAware
{

	public Deal getMerchantDeal();

	public Customer getCustomer();

}
