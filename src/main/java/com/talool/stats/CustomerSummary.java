package com.talool.stats;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 
 * @author clintz
 * 
 */
public final class CustomerSummary implements Serializable
{
	private static final long serialVersionUID = 1856781471468977494L;
	private UUID customerId;
	private String email;
	private Date registrationDate;
	private String firstName;
	private String lastName;
	private int redemptions;
	private int giftGives;
	private String commaSeperatedDealOfferTitles;

	public UUID getCustomerId()
	{
		return customerId;
	}

	public Date getRegistrationDate()
	{
		return registrationDate;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public int getRedemptions()
	{
		return redemptions;
	}

	public int getGiftGives()
	{
		return giftGives;
	}

	public String getCommaSeperatedDealOfferTitles()
	{
		return commaSeperatedDealOfferTitles;
	}

	public String getEmail()
	{
		return email;
	}

	public void setCustomerId(UUID customerId)
	{
		this.customerId = customerId;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public void setRegistrationDate(Date registrationDate)
	{
		this.registrationDate = registrationDate;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public void setRedemptions(int redemptions)
	{
		this.redemptions = redemptions;
	}

	public void setGiftGives(int giftGives)
	{
		this.giftGives = giftGives;
	}

	public void setCommaSeperatedDealOfferTitles(String commaSeperatedDealOfferTitles)
	{
		this.commaSeperatedDealOfferTitles = commaSeperatedDealOfferTitles;
	}

}
