package com.talool.payment;

/**
 * 
 * @author clintz
 * 
 */
public final class Card
{
	private String cardNumber;
	private String expirationMonth;
	private String expirationYear;
	private String cvv;

	public String getCardNumber()
	{
		return cardNumber;
	}

	public void setCardNumber(String cardNumber)
	{
		this.cardNumber = cardNumber;
	}

	public String getExpirationMonth()
	{
		return expirationMonth;
	}

	public void setExpirationMonth(String expirationMonth)
	{
		this.expirationMonth = expirationMonth;
	}

	public String getExpirationYear()
	{
		return expirationYear;
	}

	public void setExpirationYear(String expirationYear)
	{
		this.expirationYear = expirationYear;
	}

	public String getCvv()
	{
		return cvv;
	}

	public void setCvv(String cvv)
	{
		this.cvv = cvv;
	}

}
