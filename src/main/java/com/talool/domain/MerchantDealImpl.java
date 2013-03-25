package com.talool.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Merchant;
import com.talool.core.MerchantDeal;

/**
 * Deal Book implementation
 * 
 * TODO Verify hashcode/equals makes sense
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "merchant_deal", catalog = "public")
public class MerchantDealImpl implements MerchantDeal
{
	private static final Logger LOG = LoggerFactory.getLogger(MerchantDealImpl.class);
	private static final long serialVersionUID = -452436060657087167L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_merchant_deal_seq")
	@SequenceGenerator(name = "my_merchant_deal_seq", sequenceName = "merchant_deal_merchant_deal_id_seq")
	@Column(name = "merchant_deal_id", unique = true, nullable = false)
	private Long id;

	@Access(AccessType.FIELD)
	@OneToOne(targetEntity = MerchantImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;

	@Column(name = "title", unique = false, nullable = true, length = 256)
	private String title;

	@Column(name = "image_url", unique = false, nullable = true, length = 128)
	private String imageUrl;

	@Column(name = "summary", unique = false, nullable = true, length = 256)
	private String summary;

	@Column(name = "details", unique = false, nullable = true, length = 256)
	private String details;

	@Column(name = "code", unique = false, nullable = true, length = 128)
	private String code;

	@Column(name = "expires", unique = false, nullable = true)
	private Date expires;

	@Column(name = "is_active", unique = false, nullable = true)
	private boolean isActive = true;

	@Embedded
	private CreatedUpdated createdUpdated;

	public MerchantDealImpl()
	{}

	public MerchantDealImpl(final Merchant merchant)
	{
		this.merchant = merchant;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public Date getCreated()
	{
		return createdUpdated.getCreated();
	}

	@Override
	public Date getUpdated()
	{
		return createdUpdated.getUpdated();
	}

	@Override
	public Merchant getMerchant()
	{
		return merchant;
	}

	@Override
	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public String getTitle()
	{
		return title;
	}

	@Override
	public void setSummary(String summary)
	{
		this.summary = summary;
	}

	@Override
	public String getSummary()
	{
		return summary;
	}

	@Override
	public void setDetails(String details)
	{
		this.details = details;
	}

	@Override
	public String geDetails()
	{
		return details;
	}

	@Override
	public void setCode(String code)
	{
		this.code = code;

	}

	@Override
	public String getCode()
	{
		return code;
	}

	@Override
	public void setExpires(Date expires)
	{
		this.expires = expires;

	}

	@Override
	public Date getExpires()
	{
		return expires;
	}

	@Override
	public boolean isActive()
	{
		return isActive;
	}

	@Override
	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
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

		if (!(obj instanceof MerchantDealImpl))
		{
			return false;
		}

		final MerchantDealImpl other = (MerchantDealImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getTitle(), other.getTitle())
				.append(getMerchant(), other.getMerchant()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getTitle()).append(getMerchant()).hashCode();
	}

	@Override
	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	@Override
	public String getImageUrl()
	{
		return imageUrl;
	}

}
