package com.talool.utils;

import com.talool.service.ServiceConfig;
import com.timgroup.statsd.NonBlockingStatsDClient;


public class TaloolStatsDClient {

	private static NonBlockingStatsDClient client;
	private static TaloolStatsDClient instance;

	private TaloolStatsDClient() 
	{	
		client = new NonBlockingStatsDClient("talool", "graphite.talool.com", 8125);
	}
	
	public static TaloolStatsDClient get()
	{
		if (instance == null)
		{
			instance = new TaloolStatsDClient();
		}
		return instance;
	}
	
	
	/**
	 * Builds the tracking string and sends it to StatsD as a counter
	 * 
	 * @param String action - something like redeem or purchase or gift
	 * @param String subaction - something like facebook or email
	 * @param String object - something like the offer id or the deal id
	 * 
	 * Tracking string format is something like this...
	 * talool.<env>.apps.<app>.<whitelabel>.users.<user>.actions.<action>.<subaction>.<object>
	 */
	public void count(String action, String subaction, String object)
	{
		if (action == null) return;
		
		StringBuilder sb = new StringBuilder();
		if (ServiceConfig.get().isStatsDEnvironmentProduction())
		{
			sb.append("production");
		}
		else
		{
			sb.append("development");
		} 
		sb.append(".apps");
		
		boolean hasHeaders = false;
		if (hasHeaders)
		{
			// TODO parse the headers to get the app and white label id and user id.  
			// Chris will put this in thread local
			//sb.append(".<app>.<whitelabel>.<user>");
		}
		else
		{
			sb.append(".mobile.users");
		}
		
		sb.append(".actions.").append(action);
		
		if (subaction != null)
		{
			sb.append(".").append(subaction); 
		}
		
		if (object != null)
		{
			sb.append(".").append(object);
		}
		
		client.incrementCounter(sb.toString());
	}
}
