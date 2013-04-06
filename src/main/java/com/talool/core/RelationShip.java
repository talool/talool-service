package com.talool.core;

import java.io.Serializable;

/**
 * 
 * @author clintz
 * 
 */
public interface RelationShip extends Identifiable, Serializable, TimeAware
{
	public Customer getCustomer();

	public void setCustomer(Customer customer);

	public Customer getFriend();

	public void setFriend(Customer cutomer);

	public RelationshipStatus getRelationshipStatus();

	public void setRelationshipStatus(RelationshipStatus relationShipStatus);

}
