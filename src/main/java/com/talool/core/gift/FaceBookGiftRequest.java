package com.talool.core.gift;

/**
 * Gift request
 * 
 * @author clintz
 * 
 */
public interface FaceBookGiftRequest extends GiftRequest
{
	public String getToFacebookId();

	public void setToFacebookId(final String toFacebookId);

	public String getToName();

	public void setToName(final String toName);

	public String getAcceptedByFacebookId();

	public void setAcceptedByFacebookId(final String acceptedByFacebookId);

}
