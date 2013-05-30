package com.talool.core.service;

import com.talool.core.Customer;

/**
 * Talool Email Service
 * 
 * @author clintz
 * 
 */
public interface EmailService
{
	public void sendCustomerRegistrationEmail(final Customer customer) throws ServiceException;

	public void sendEmail(final String subject, final String recipient, final String from, final String messageBody)
			throws ServiceException;

}
