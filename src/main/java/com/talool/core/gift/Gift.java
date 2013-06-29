package com.talool.core.gift;

import java.io.Serializable;

import com.talool.core.Customer;
import com.talool.core.DealAcquire;
import com.talool.core.IdentifiableUUID;
import com.talool.core.TimeAware;

/**
 * Gift request
 * 
 * @author clintz
 * 
 */
public interface Gift extends IdentifiableUUID, Serializable, TimeAware
{
	public Customer getFromCustomer();

	public void setFromCustomer(final Customer fromCustomer);

	public DealAcquire getDealAcquire();

	public void setDealAcquire(final DealAcquire dealAcquire);

	public String getReceipientName();

	public void setReceipientName(final String rceipientName);

	public GiftStatus getGiftStatus();

	public void setGiftStatus(final GiftStatus giftStatus);

	public Customer getToCustomer();

	public void setToCustomer(final Customer customer);

}
