package com.talool.core;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * 
 * @author clintz
 */
public interface Address extends Identifiable, Serializable
{
	public String getAddress1();

	public void setAddress1(String address1);

	public String getAddress2();

	public void setAddress2(String address2);

	public String getCity();

	public void setCity(String city);

	public String getStateProvinceCounty();

	public void setStateProvinceCounty(String stateProvinceCounty);

	public String getZip();

	public void setZip(String zip);

	public String getCountry();

	public void setCountry(String country);

	public Date getCreated();

	public Date getUpdated();

}
