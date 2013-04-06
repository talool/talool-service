package com.talool.core;

import java.io.Serializable;

/**
 * 
 * @author clintz
 * 
 */
public interface MerchantAccount extends Identifiable, Serializable, TimeAware
{
	public Merchant getMerchant();

	public String getEmail();

	public void setEmail(String email);

	public String getPassword();

	public void setPassword(String password);

	public String getRoleTitle();

	public void setRoleTitle(String roleTitle);

	public boolean allowDealCreation();

	public void setAllowDealCreation(boolean allowDealCreation);

}
