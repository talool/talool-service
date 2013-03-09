/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.core;

import java.io.Serializable;

/**
 * @author clintz
 * 
 */
public interface SocialAccount extends TimeAware, Serializable
{
	public SocialNetwork getSocialNetwork();

	public void setSocialNetwork(SocialNetwork socialNetwork);

	/**
	 * gets merchant or customer Id (based on AccountType)
	 */
	public Long getUserId();

	/**
	 * sets merchant or customer Id (based on AccountType)
	 */
	public void setUserId(Long userId);

	public String getLoginId();

	public void setLoginId(String loginId);

	public AccountType getAccountType();

	public void setAccountType(AccountType accountType);

	public String getToken();

	public void setToken(String token);
}
