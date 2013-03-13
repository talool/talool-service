package com.talool.core;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface Customer extends Identifiable, Serializable, TimeAware
{
	public String getFirstName();

	public void setFirstName(String firstName);

	public String getLastName();

	public void setLastName(String lastName);

	public String getEmail();

	public void setEmail(String email);

	public String getPassword();

	public void setPassword(String password);

	public Sex getSex();

	public void setSex(Sex sex);

	public Date getBirthDate();

	public void setBirthDate(Date birthDate);

	public Map<SocialNetwork, SocialAccount> getSocialAccounts();

	public void addSocialAccount(SocialAccount socialAccount);

}
