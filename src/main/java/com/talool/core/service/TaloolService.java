package com.talool.core.service;

import com.talool.core.Address;
import com.talool.core.Customer;

/**
 * 
 * 
 * 
 * @author clintz
 */
public interface TaloolService
{
	public void registerCustomer(final Customer customer, final String password) throws ServiceException;

	public Customer authCustomer(final String email, final String password) throws ServiceException;

	public void saveCustomer(final Customer customer) throws ServiceException;

	public Customer newCustomer();

	public Address newAddress();
}
