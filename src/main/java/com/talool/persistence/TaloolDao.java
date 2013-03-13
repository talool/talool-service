package com.talool.persistence;

import com.talool.core.Address;
import com.talool.core.Customer;
import com.talool.core.SocialNetwork;

/**
 * 
 * @author clintz
 */
public interface TaloolDao
{
	public void save(final Customer customer) throws DaoException;

	public void save(final Address address) throws DaoException;

	public Customer getCustomerByEmail(final String email) throws DaoException;

	public Customer getCustomerById(final Long id) throws DaoException;

	public Customer authenticateCustomer(final String email, final String password) throws DaoException;

	public SocialNetwork getSocialNetwork(final String name) throws DaoException;

}
