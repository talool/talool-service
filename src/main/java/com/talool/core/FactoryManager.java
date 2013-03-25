package com.talool.core;

import com.talool.service.ServiceFactory;

/**
 * 
 * @author clintz
 * 
 */
public final class FactoryManager
{
	private static FactoryManager instance;
	private ServiceFactory serviceFactory;
	private DomainFactory domainFactory;

	private FactoryManager()
	{}

	public static FactoryManager get()
	{
		return instance;
	}

	public static synchronized FactoryManager createInstance(final ServiceFactory serviceFactory,
			final DomainFactory domainFactory)
	{
		if (instance == null)
		{
			instance = new FactoryManager();
			instance.serviceFactory = serviceFactory;
			instance.domainFactory = domainFactory;
		}

		return instance;
	}

	public ServiceFactory getServiceFactory()
	{
		return serviceFactory;
	}

	public DomainFactory getDomainFactory()
	{
		return domainFactory;
	}

}
