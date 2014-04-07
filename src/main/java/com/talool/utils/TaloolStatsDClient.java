package com.talool.utils;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.service.ServiceConfig;
import com.talool.utils.GraphiteConstants.Action;
import com.talool.utils.GraphiteConstants.Apps;
import com.talool.utils.GraphiteConstants.DeviceType;
import com.talool.utils.GraphiteConstants.Environment;
import com.talool.utils.GraphiteConstants.SubAction;
import com.talool.utils.GraphiteConstants.WhiteLabel;
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
	public void count(Action action, SubAction subaction, UUID object, Map<String, String> requestHeaders)
	{
		if (action == null) return;

		// Set some default values
		String env = (ServiceConfig.get().isStatsDEnvironmentProduction())?Environment.production.toString():Environment.development.toString();
		String app = Apps.mobile.toString(); // this is the only one we have, but there could be more in the future
		String whitelabelId = WhiteLabel.core.toString();
		String platform = GraphiteConstants.any;
		String userId = GraphiteConstants.any;
		String subactionString = (subaction==null) ?GraphiteConstants.any:subaction.toString();
		String objString = (object==null) ? GraphiteConstants.any:object.toString();
		
		// Try to get specific values from the headers
		if (requestHeaders != null)
		{	
			String ua = requestHeaders.get("user-agent");
			if (ua != null)
			{
				platform = (StringUtils.contains(ua, "iPhone"))?DeviceType.iphone.toString():DeviceType.android.toString();
			}
			
			// TODO get the whitelabelid and userid
			//whitelabelId = requestHeaders.get("white-label-id");
			//userId = requestHeaders.get("user-id");
		}
		else
		{
			LOG.debug("no headers in thread local");
		}
		
		// Build the key
		StringBuilder sb = new StringBuilder(env);
		sb.append(".apps").append(".").append(app).append(".").append(whitelabelId).append(".").append(platform)
		  .append(".actions.").append(action).append(".").append(subactionString).append(".").append(objString)
		  .append(".users").append(".").append(userId);
		
		LOG.info("count: "+sb.toString());
		client.incrementCounter(sb.toString());
	}
}
