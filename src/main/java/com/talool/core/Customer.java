package com.talool.core;

import java.io.Serializable;
import java.util.Date;

public interface Customer extends Identifiable, Serializable
{
	public String getFirstName();

	public void setFirstName(String firstName);

	public String getLastName();

	public void setLastName(String lastName);

	public String getEmail();

	public void setEmail(String email);

	public String getPassword();

	public void setPassword(String password);

	public Address getAddress();

	public void setAddress(Address address);

	public Date getCreated();

	public Date getUpdated();
}
