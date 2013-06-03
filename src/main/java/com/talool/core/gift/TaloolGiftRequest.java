package com.talool.core.gift;

import com.talool.core.Customer;

/**
 * Gift request
 * 
 * @author clintz
 * 
 */
public interface TaloolGiftRequest extends GiftRequest
{
	public Customer getToCustomer();

	public void setToCustomer(final Customer toCustomer);

}
