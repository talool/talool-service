package com.talool.core;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.talool.core.gift.Gift;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * @author clintz
 * 
 */
public interface DealAcquire extends IdentifiableUUID, Serializable, TimeAware
{
	public Deal getDeal();

	/**
	 * If the DealAcquire was acquired via Gifting, Gift will not be null
	 * 
	 * @return
	 */
	public Gift getGift();

	public UUID getGiftId();

	public void setGift(final Gift gift);

	public AcquireStatus getAcquireStatus();

	public void setAcquireStatus(final AcquireStatus status);

	public Customer getCustomer();

	public void setCustomer(final Customer customer);

	public String getRedemptionCode();

	public Date getRedemptionDate();

	public Geometry getRedeemedAtGeometry();

	public void setRedeemedAtGeometry(final Geometry geometry);

}
