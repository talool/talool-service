package com.talool.core.service;

import com.talool.core.Customer;
import com.talool.core.gift.EmailGift;
import com.talool.service.mail.EmailRequestParams;

/**
 * Talool Email Service
 * 
 * @author clintz
 * 
 */
public interface EmailService
{
	public void sendCustomerRegistrationEmail(final EmailRequestParams<Customer> emailRequestParams) throws ServiceException;

	public void sendPasswordRecoveryEmail(final EmailRequestParams<Customer> emailRequestParams) throws ServiceException;

	public void sendGiftEmail(final EmailRequestParams<EmailGift> emailRequestParams) throws ServiceException;

}
