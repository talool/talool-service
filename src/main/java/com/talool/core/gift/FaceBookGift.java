package com.talool.core.gift;

/**
 * Gift request
 * 
 * @author clintz
 * 
 */
public interface FaceBookGift extends Gift
{
	public String getToFacebookId();

	public void setToFacebookId(final String toFacebookId);

	public String getOriginalToFacebookId();

	public void setOriginalToFacebookId(final String originalToFacebookId);

}
