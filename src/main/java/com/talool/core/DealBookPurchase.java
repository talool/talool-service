package com.talool.core;

import java.io.Serializable;

/**
 * 
 * @author clintz
 * 
 */
public interface DealBookPurchase extends Identifiable, Serializable, TimeAware
{
	public DealBook getDealBook();

	public Customer getCustomer();

}
