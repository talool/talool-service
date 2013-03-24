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

import com.talool.core.DealBook;
import com.talool.core.Merchant;

/**
 * Deal Book implementation
 * 
 * TODO Verify hashcode/equals makes sense
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "deal_book", catalog = "public")
public class DealBookImpl implements DealBook
{
	private static final Logger LOG = LoggerFactory.getLogger(DealBookImpl.class);
	private static final long serialVersionUID = -452436060657087167L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_deal_book_seq")
	@SequenceGenerator(name = "my_deal_book_seq", sequenceName = "deal_book_deal_book_id_seq")
	@Column(name = "deal_book_id", unique = true, nullable = false)
	private Long id;

	@Access(AccessType.FIELD)
	@OneToOne(targetEntity = MerchantImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;

	@Column(name = "longitude", unique = false, nullable = true)
	private Double longitude;

	@Column(name = "latitude", unique = false, nullable = true)
	private Double latitude;

	@Column(name = "title", unique = false, nullable = true, length = 256)
	private String title;

	@Column(name = "summary", unique = false, nullable = true, length = 256)
	private String summary;

	@Column(name = "details", unique = false, nullable = true, length = 256)
	private String details;

	@Column(name = "code", unique = false, nullable = true, length = 128)
	private String code;

	@Column(name = "cost", unique = false, nullable = true, precision = 10, scale = 2)
	private Float cost;

	@Column(name = "expires", unique = false, nullable = true)
	private Date expires;

	@Column(name = "is_active", unique = false, nullable = true)
	private boolean isActive = true;

	@Embedded
	private CreatedUpdated createdUpdated;

	public DealBookImpl(final Merchant merchant)
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
	public Double getLongitude()
	{
		return longitude;
	}

	@Override
	public void setLongitude(Double longitude)
	{
		this.longitude = longitude;
	}

	@Override
	public Double getLatitude()
	{
		return latitude;
	}

	@Override
	public void setLatitude(Double latitude)
	{
		this.latitude = latitude;
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
	public void setCost(Float cost)
	{
		this.cost = cost;
	}

	@Override
	public Float getCost()
	{
		return cost;
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

		if (!(obj instanceof MerchantImpl))
		{
			return false;
		}

		final DealBookImpl other = (DealBookImpl) obj;

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

}
