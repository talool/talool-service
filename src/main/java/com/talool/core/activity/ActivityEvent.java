package com.talool.core.activity;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity types
 * 
 * @author clintz
 * 
 */
public enum ActivityEvent
{
	// Be very careful of these and do not change!
	UNKNOWN((short) 0, "unknown activity"),
	WELCOME((short) 1, "welcome new customer"),
	PURCHASE((short) 2, "customer purchased of deal offer"),
	REDEEM((short) 3, "customer redeemed deal"),
	REJECT_GIFT((short) 4, "customer rejected gift"),
	FACEBOOK_RECV_GIFT((short) 5, "customer received Facebook gift"),
	FACEBOOK_SEND_GIFT((short) 6, "customer sent Facebook gift"),
	EMAIL_RECV_GIFT((short) 7, "customer received email gift"),
	EMAIL_SEND_GIFT((short) 8, "customer send email gift"),
	FRIEND_GIFT_ACCEPT((short) 9, "friend accepted gift"),
	FRIEND_GIFT_REJECT((short) 10, "friend rejected gift"),
	FRIEND_GIFT_REDEEM((short) 11, "friend redeemed gift"),
	MERCHANT_REACH((short) 12, "merchant message"),
	TALOOL_REACH((short) 13, "Talool message"),
	AD((short) 14, "Ad");

	private short id;
	private String description;

	private static final Map<Short, ActivityEvent> activityMap = new HashMap<Short, ActivityEvent>();

	static
	{
		for (ActivityEvent type : ActivityEvent.values())
		{
			if (activityMap.get(type.id) != null)
			{
				throw new RuntimeException("Duplicate id key for activity " + type);
			}
			activityMap.put(type.id, type);
		}
	}

	private ActivityEvent(final short id, final String description)
	{
		this.id = id;
		this.description = description;
	}

	public short getId()
	{
		return id;
	}

	public String getDescription()
	{
		return description;
	}

	public static ActivityEvent valueById(final short id)
	{
		ActivityEvent type = activityMap.get(id);
		return type == null ? UNKNOWN : type;
	}
}
