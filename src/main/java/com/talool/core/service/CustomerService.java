package com.talool.core.service;

import com.talool.core.AccountType;
import com.talool.core.Customer;

/**
 * Customer Service
 * 
 * @author clintz
 * 
 */
public interface CustomerService
{
	public Customer newCustomer();

	public void createAccount(final Customer customer, final String password) throws ServiceException;

	public void deleteCustomer(final Long id) throws ServiceException;

	public Customer authenticateCustomer(final String email, final String password)
			throws ServiceException;

	public void save(final Customer customer) throws ServiceException;

	public Customer getCustomerById(final Long id) throws ServiceException;

	public Customer getCustomerByEmail(final String email) throws ServiceException;

	public boolean emailExists(final AccountType accountType, final String email)
			throws ServiceException;

}
