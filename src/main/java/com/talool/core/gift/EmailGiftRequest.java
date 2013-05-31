package com.talool.core.gift;

/**
 * Gift request
 * 
 * @author clintz
 * 
 */
public interface EmailGiftRequest extends GiftRequest
{
	public String getToEmail();

	public void setToEmail(final String toEmail);

	public String getOriginalToEmail();

	public void setOriginalToEmail(final String originalToEmail);

}
