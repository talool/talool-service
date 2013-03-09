package com.talool.persistence;

import com.talool.core.Address;
import com.talool.core.Customer;

/**
 * 
 * @author clintz
 */
public interface TaloolDao
{
	public void saveCustomer(final Customer customer) throws DaoException;

	public void saveAddress(final Address address) throws DaoException;

	public Customer getCustomer(final String email) throws DaoException;

	public Customer authCustomer(final String email, final String password) throws DaoException;

}
