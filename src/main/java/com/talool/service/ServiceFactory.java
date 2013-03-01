package com.talool.service;

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

	private ServiceFactory()
	{}

	public static ServiceFactory get()
	{
		return instance;
	}

	public static synchronized ServiceFactory createInstance(final TaloolService taloolService)
	{
		if (instance == null)
		{
			instance = new ServiceFactory();
			instance.taloolService = taloolService;
		}

		return instance;
	}

	public TaloolService getTaloolService()
	{
		return taloolService;
	}

}
