package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author clintz
 * 
 */
public interface FriendRequest extends Identifiable, Serializable
{
	public Customer getCustomer();

	public String getFriendFacebookId();

	public String getFriendEmail();

	public Deal getDeal();

	public Date getCreated();

}
