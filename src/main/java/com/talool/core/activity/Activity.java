package com.talool.core.activity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.talool.core.IdentifiableUUID;

/**
 * 
 * @author clintz
 * 
 */
public interface Activity extends IdentifiableUUID, Serializable
{
	public ActivityEvent getActivityEvent();

	public void setActivityType(final ActivityEvent type);

	public UUID getCustomerId();

	public void setCustomerId(final UUID customerId);

	public void setActivityData(final byte[] activityData);

	public byte[] getActivityData();

	public Date getActivityDate();
}
