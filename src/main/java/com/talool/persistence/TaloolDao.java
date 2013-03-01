package com.talool.persistence;

import com.talool.core.Customer;
import com.talool.entity.AddressImpl;
import com.talool.entity.CustomerImpl;

/**
 * 
 * @author clintz
 */
public interface TaloolDao
{
	public void saveCustomer(final CustomerImpl customer) throws DaoException;

	public void saveAddress(final AddressImpl address) throws DaoException;

	public Customer getCustomer(final String email) throws DaoException;

	public Customer getCustomer(final String email, final String password) throws DaoException;
}
