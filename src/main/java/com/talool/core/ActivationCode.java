package com.talool.core;

import java.util.Date;
import java.util.UUID;

/**
 * 
 * @author clintz
 * 
 */
public interface ActivationCode extends IdentifiableUUID
{
	public UUID getDealOfferId();

	public void setDealOfferId(final UUID dealOfferId);

	public String getCode();

	public void setCode(final String code);

	public UUID getCustomerId();

	public void setCustomerId(final UUID customerId);

	public Date getActivatedDate();

	public void setActivatedDate(final Date date);

}
