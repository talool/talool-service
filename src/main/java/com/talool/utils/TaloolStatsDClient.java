package com.talool.utils;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.service.ServiceConfig;
import com.timgroup.statsd.NonBlockingStatsDClient;


public class TaloolStatsDClient {

	private static NonBlockingStatsDClient client;
	private static TaloolStatsDClient instance;
	
	private static final Logger LOG = LoggerFactory.getLogger(TaloolStatsDClient.class);

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
	 * talool.<env>.apps.<app>.<whitelabel>.<platform>.users.<user>.actions.<action>.<subaction>.<object>
	 */
	public void count(String action, String subaction, String object, Map<String, String> requestHeaders)
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
		
		if (requestHeaders != null)
		{	
			String ua = requestHeaders.get("user-agent");
			String app = "mobile"; // this is the only one we have, but there could be more in the future
			String platform = null;
			if (ua != null)
			{
				platform = (StringUtils.contains(ua, "iPhone"))?"iphone":"android";
			}
			
			String whitelabelId = requestHeaders.get("white-label-id");
			String userId = requestHeaders.get("user-id");
			
			sb.append(".").append(app);
			if (whitelabelId != null)
			{
				sb.append(".").append(whitelabelId);
			}
			if (platform != null)
			{
				sb.append(".").append(platform);
			}
			sb.append(".users");
			if (userId != null)
			{
				sb.append(".").append(userId);
			}
			
		}
		else
		{
			LOG.debug("no headers in thread local");
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
		
		LOG.info("count: "+sb.toString());
		client.incrementCounter(sb.toString());
	}
}
