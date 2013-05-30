package com.talool.core.gift;

import java.io.Serializable;
import java.util.UUID;

import com.talool.core.IdentifiableUUID;
import com.talool.core.TimeAware;

/**
 * Gift request
 * 
 * @author clintz
 * 
 */
public interface GiftRequest extends IdentifiableUUID, Serializable, TimeAware
{
	public UUID getCustomerId();

	public void setCustomerId(final UUID customerId);

	public UUID getDealAcquireId();

	public void setDealAcquireId(final UUID dealAcquireId);
}
