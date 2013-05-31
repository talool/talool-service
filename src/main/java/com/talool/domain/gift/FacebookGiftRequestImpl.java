package com.talool.domain.gift;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.talool.core.gift.FaceBookGiftRequest;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "gift_request")
@DiscriminatorValue("F")
public class FacebookGiftRequestImpl extends GiftRequestImpl implements FaceBookGiftRequest
{
	private static final long serialVersionUID = 9115919880956633128L;

	@Column(name = "to_facebook_id", length = 32)
	private String toFacebookId;

	@Column(name = "original_to_facebook_id", length = 32)
	private String originalToFacebookId;

	@Override
	public String getToFacebookId()
	{
		return toFacebookId;
	}

	@Override
	public void setToFacebookId(final String toFacebookId)
	{
		this.toFacebookId = toFacebookId;
	}

	@Override
	public String getOriginalToFacebookId()
	{
		return originalToFacebookId;
	}

	@Override
	public void setOriginalToFacebookId(final String originalToFacebookId)
	{
		this.originalToFacebookId = originalToFacebookId;

	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj == null)
		{
			return false;
		}

		if (!(obj instanceof FacebookGiftRequestImpl))
		{
			return false;
		}

		final FacebookGiftRequestImpl other = (FacebookGiftRequestImpl) obj;

		return super.equals(obj) && new EqualsBuilder().append(getToFacebookId(), other.getToFacebookId())
				.append(getOriginalToFacebookId(), other.getOriginalToFacebookId()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getToFacebookId()).
				append(getOriginalToFacebookId()).hashCode();
	}

}
