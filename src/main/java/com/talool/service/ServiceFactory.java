package com.talool.service;

import com.talool.core.service.CustomerService;
import com.talool.core.service.EmailService;
import com.talool.core.service.TaloolService;

/**
 * 
 * 
 * 
 * @author clintz
 */
public final class ServiceFactory
{
	private static ServiceFactory instance;
	private TaloolService taloolService;
	private CustomerService customerService;
	private EmailService emailService;

	private ServiceFactory()
	{}

	public static ServiceFactory get()
	{
		return instance;
	}

	public static synchronized ServiceFactory createInstance(final TaloolService taloolService,
			final CustomerService customerService, final EmailService emailService)
	{
		if (instance == null)
		{
			instance = new ServiceFactory();
			instance.taloolService = taloolService;
			instance.customerService = customerService;
			instance.emailService = emailService;
		}

		return instance;
	}

	public TaloolService getTaloolService()
	{
		return taloolService;
	}

	public CustomerService getCustomerService()
	{
		return customerService;
	}

	public EmailService getEmailService()
	{
		return emailService;
	}

}
