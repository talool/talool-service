package com.talool.core.service;

import com.talool.core.AccountType;
import com.talool.core.Address;
import com.talool.core.SocialAccount;
import com.talool.core.SocialNetwork;

/**
 * 
 * @author clintz
 */
public interface TaloolService extends MerchantService, CustomerService
{
	// Talool Customer
	public Address newAddress();

	public SocialNetwork getSocialNetwork(final String name) throws ServiceException;

	public SocialAccount newSocialAccount(final String socialNetworkName,
			final AccountType accountType);

}
