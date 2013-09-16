package com.talool.core;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.SocialNetwork;

/**
 * 
 * @author clintz
 * 
 */
public interface Customer extends IdentifiableUUID, Serializable, TimeAware
{
	public String getFullName();

	public String getFirstName();

	public void setFirstName(String firstName);

	public String getLastName();

	public void setLastName(String lastName);

	public String getEmail();

	public void setEmail(String email);

	/**
	 * Gets the encrypted password
	 * 
	 * @return
	 */
	public String getPassword();

	/**
	 * Encrptys and sets the password
	 * 
	 * @param password
	 */
	public void setPassword(String password);

	public void setResetPasswordCode(String code);

	public String getResetPasswordCode();

	public void setResetPasswordExpires(Date expires);

	public Date getResetPasswordExpires();

	public Sex getSex();

	public void setSex(Sex sex);

	public Date getBirthDate();

	public void setBirthDate(Date birthDate);

	public Map<SocialNetwork, CustomerSocialAccount> getSocialAccounts();

	public void addSocialAccount(CustomerSocialAccount socialAccount);

	public void removeSocialAccount(CustomerSocialAccount socialAccount);

}
