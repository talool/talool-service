package com.talool.core;

import java.io.Serializable;

/**
 * 
 * @author clintz
 * 
 */
public interface Relationship extends Identifiable, Serializable, TimeAware
{
	public Customer getFromCustomer();

	public void setFromCustomer(final Customer customer);

	public Customer getToCustomer();

	public void setToCustomer(final Customer cutomer);

	public RelationshipStatus getRelationshipStatus();

	public void setRelationshipStatus(final RelationshipStatus relationShipStatus);

}
