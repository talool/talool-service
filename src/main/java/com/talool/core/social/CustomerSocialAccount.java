package com.talool.core.social;

import com.talool.core.Customer;

/**
 * @author clintz
 * 
 */
public interface CustomerSocialAccount extends SocialAccount
{
	public Customer getCustomer();

	public void setCustomer(Customer customer);
}
