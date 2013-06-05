package com.talool.core.gift;

/**
 * Gift request
 * 
 * @author clintz
 * 
 */
public interface EmailGift extends Gift
{
	public String getToEmail();

	public void setToEmail(final String toEmail);

	public String getOriginalToEmail();

	public void setOriginalToEmail(final String originalToEmail);

}
