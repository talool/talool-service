package com.talool.core.service;

import com.talool.entity.AddressImpl;
import com.talool.entity.CustomerImpl;

/**
 * 
 * 
 * 
 * @author clintz
 */
public interface TaloolService
{
	public void registerCustomer(final CustomerImpl customer, final String password)
			throws ServiceException;

	public void saveCustomer(final CustomerImpl customer) throws ServiceException;

	public CustomerImpl newCustomer();

	public CustomerImpl newCustomer(com.talool.thrift.Customer customer);

	public AddressImpl newAddress();

	public AddressImpl newAddress(com.talool.thrift.Address address);
}
