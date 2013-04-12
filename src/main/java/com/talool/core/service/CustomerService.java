package com.talool.core.service;

import java.util.List;

import com.talool.core.Customer;
import com.talool.core.Relationship;

/**
 * Customer Service
 * 
 * @author clintz
 * 
 */
public interface CustomerService
{
	public void createAccount(final Customer customer, final String password) throws ServiceException;

	public void deleteCustomer(final Long id) throws ServiceException;

	public Customer authenticateCustomer(final String email, final String password)
			throws ServiceException;

	public void save(final Customer customer) throws ServiceException;

	public Customer getCustomerById(final Long id) throws ServiceException;

	public Customer getCustomerByEmail(final String email) throws ServiceException;

	public List<Customer> getCustomers() throws ServiceException;

	public List<Customer> getFriends(final Long id) throws ServiceException;

	public void save(final Relationship relationship) throws ServiceException;

	public List<Relationship> getRelationshipsFrom(final Long customerId) throws ServiceException;

	public List<Relationship> getRelationshipsTo(final Long customerId) throws ServiceException;

}
