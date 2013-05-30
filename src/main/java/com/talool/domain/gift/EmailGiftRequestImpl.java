package com.talool.domain.gift;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.talool.core.gift.EmailGiftRequest;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "gift_request")
@DiscriminatorValue("E")
public class EmailGiftRequestImpl extends GiftRequestImpl implements EmailGiftRequest
{
	private static final long serialVersionUID = -6197930806091256725L;

	@Column(name = "accepted_by_email", length = 128)
	private String acceptedByEmail;

	@Column(name = "to_email", length = 128)
	private String toEmail;

	@Override
	public String getToEmail()
	{
		return toEmail;
	}

	@Override
	public void setToEmail(String toEmail)
	{
		this.toEmail = toEmail;
	}

	@Override
	public String getAcceptedByEmail()
	{
		return acceptedByEmail;
	}

	@Override
	public void setAcceptedByEmail(final String acceptedByEmail)
	{
		this.acceptedByEmail = acceptedByEmail;
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

		if (!(obj instanceof EmailGiftRequestImpl))
		{
			return false;
		}

		final EmailGiftRequestImpl other = (EmailGiftRequestImpl) obj;

		return super.equals(obj) && new EqualsBuilder().append(getToEmail(), other.getToEmail())
				.append(getAcceptedByEmail(), other.getAcceptedByEmail()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getToEmail()).append(getAcceptedByEmail()).hashCode();
	}

}
