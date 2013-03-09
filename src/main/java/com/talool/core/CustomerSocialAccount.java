package com.talool.core;

import java.io.Serializable;
import java.util.Date;

public interface CustomerSocialAccount extends Identifiable, Serializable
{
	public String getFirstName();

	public void setFirstName(String firstName);

	public String getLastName();

	public void setLastName(String lastName);

	public String getEmail();

	public void setEmail(String email);

	public String getPassword();

	public void setPassword(String password);

	public Sex getSexType();

	public void setSexType(Sex sex);

	public Date getBirthDate();

	public void setBirthDate(Date birthDate);

	public Date getCreated();

	public Date getUpdated();
}
