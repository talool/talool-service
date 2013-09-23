package com.talool.payment;

/**
 * 
 * @author clintz
 * 
 */
public final class Card
{
	private String accountNumber;
	private String expirationMonth;
	private String expirationYear;
	private String securityCode;
	private String zipCode;

	public String getAccountnumber()
	{
		return accountNumber;
	}

	public Card setAccountNumber(String cardNumber)
	{
		this.accountNumber = cardNumber;
		return this;
	}

	public String getExpirationMonth()
	{
		return expirationMonth;
	}

	public Card setExpirationMonth(String expirationMonth)
	{
		this.expirationMonth = expirationMonth;
		return this;
	}

	public String getExpirationYear()
	{
		return expirationYear;
	}

	public Card setExpirationYear(String expirationYear)
	{
		this.expirationYear = expirationYear;
		return this;
	}

	public String getSecurityCode()
	{
		return securityCode;
	}

	public Card setSecurityCode(String securityCode)
	{
		this.securityCode = securityCode;
		return this;
	}

	public String getZipCode()
	{
		return zipCode;
	}

	public Card setZipCode(String zipCode)
	{
		this.zipCode = zipCode;
		return this;
	}

}
