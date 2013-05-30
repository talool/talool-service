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

	@Column(name = "to_facebook_name", length = 32)
	private String toName;

	@Column(name = "accepted_by_facebook_id", length = 32)
	private String acceptedByFacebookId;

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
	public String getToName()
	{
		return toName;
	}

	@Override
	public void setToName(final String toName)
	{
		this.toName = toName;
	}

	@Override
	public String getAcceptedByFacebookId()
	{
		return acceptedByFacebookId;
	}

	@Override
	public void setAcceptedByFacebookId(final String acceptedByFacebookId)
	{
		this.acceptedByFacebookId = acceptedByFacebookId;

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
				.append(getToName(), other.getToName()).append(getAcceptedByFacebookId(), other.getAcceptedByFacebookId()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getToFacebookId()).append(getToName()).
				append(getAcceptedByFacebookId()).hashCode();
	}

}
