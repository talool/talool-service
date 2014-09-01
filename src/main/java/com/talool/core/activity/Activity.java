package com.talool.core.activity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.talool.core.IdentifiableUUID;
import com.talool.core.PropertyEntity;

/**
 * 
 * @author clintz
 * 
 */
public interface Activity extends IdentifiableUUID, Serializable, PropertyEntity
{
	public ActivityEvent getActivityEvent();

	public void setActivityEvent(final ActivityEvent type);

	public UUID getCustomerId();

	public UUID getGiftId();

	public void setGiftId(final UUID giftId);

	public void setCustomerId(final UUID customerId);

	public void setActivityData(final byte[] activityData);

	public boolean getIsOpened();

	public void setIsOpened(final boolean isOpened);

	public byte[] getActivityData();

	public Date getActivityDate();
}
