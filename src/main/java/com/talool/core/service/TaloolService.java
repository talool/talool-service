package com.talool.core.service;

import com.talool.core.Address;
import com.talool.core.Customer;
import com.talool.core.SocialAccount;
import com.talool.core.SocialNetwork;

/**
 * 
 * 
 * 
 * @author clintz
 */
public interface TaloolService
{
	// Talool Customer
	public Address newAddress();

	public void createAccount(final Customer customer, final String password) throws ServiceException;

	public Customer authenticateCustomer(final String email, final String password) throws ServiceException;

	public void save(final Customer customer) throws ServiceException;

	public Customer getCustomerById(final Long id) throws ServiceException;

	public Customer getCustomerByEmail(final String email) throws ServiceException;

	// Talool Social

	public SocialNetwork getSocialNetwork(final String name) throws ServiceException;

	public SocialAccount newSocialAccount();

	public Customer newCustomer();

}
