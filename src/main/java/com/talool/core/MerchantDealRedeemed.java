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

	public MerchantDeal getMerchantDeal();

	public Customer getCustomer();

}
