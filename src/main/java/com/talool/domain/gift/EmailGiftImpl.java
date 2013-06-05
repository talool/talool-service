package com.talool.domain.gift;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.talool.core.gift.EmailGift;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "gift")
@DiscriminatorValue("E")
public class EmailGiftImpl extends GiftImpl implements EmailGift
{
	private static final long serialVersionUID = -6197930806091256725L;

	@Column(name = "original_to_email", length = 128)
	private String originalToEmail;

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
	public String getOriginalToEmail()
	{
		return originalToEmail;
	}

	@Override
	public void setOriginalToEmail(final String originalToEmail)
	{
		this.originalToEmail = originalToEmail;
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

		if (!(obj instanceof EmailGiftImpl))
		{
			return false;
		}

		final EmailGiftImpl other = (EmailGiftImpl) obj;

		return super.equals(obj) && new EqualsBuilder().append(getToEmail(), other.getToEmail())
				.append(getOriginalToEmail(), other.getOriginalToEmail()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getToEmail()).append(getOriginalToEmail()).hashCode();
	}

}
