package com.talool.core.service;

import com.talool.core.AccountType;
import com.talool.core.SocialNetwork;

/**
 * 
 * @author clintz
 */
public interface TaloolService extends MerchantService, CustomerService
{
	public SocialNetwork getSocialNetwork(final String name) throws ServiceException;

	public boolean emailExists(final AccountType accountType, final String email)
			throws ServiceException;

}
