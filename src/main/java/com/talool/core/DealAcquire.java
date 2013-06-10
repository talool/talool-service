package com.talool.core;

import java.io.Serializable;
import java.util.Date;

import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * @author clintz
 * 
 */
public interface DealAcquire extends IdentifiableUUID, Serializable, TimeAware
{
	public Deal getDeal();

	public AcquireStatus getAcquireStatus();

	public void setAcquireStatus(AcquireStatus status);

	public Customer getCustomer();

	public void setCustomer(Customer customer);

	public Customer getSharedByCustomer();

	public void setSharedByCustomer(Customer customer);

	public Integer getShareCount();

	public Integer incrementShareCount();

	public String getRedemptionCode();

	public Date getRedemptionDate();

	public Geometry getGeometry();

	public void setGeometry(Geometry geometry);

}
