package com.talool.core.gift;

import com.talool.core.Customer;

/**
 * Gift request
 * 
 * @author clintz
 * 
 */
public interface TaloolGift extends Gift
{
	public Customer getToCustomer();

	public void setToCustomer(final Customer toCustomer);

}
