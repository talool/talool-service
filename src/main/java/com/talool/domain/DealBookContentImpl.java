package com.talool.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
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
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.DealBook;
import com.talool.core.DealBookContent;
import com.talool.core.MerchantDeal;

/**
 * Deal Book Content implmentation
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "deal_book_content", catalog = "public")
public class DealBookContentImpl implements DealBookContent
{
	private static final Logger LOG = LoggerFactory.getLogger(DealBookContentImpl.class);
	private static final long serialVersionUID = -8845510876845457293L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_deal_book_content_seq")
	@SequenceGenerator(name = "my_deal_book_content_seq", sequenceName = "deal_book_content_deal_book_content_id_seq")
	@Column(name = "deal_book_content_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "page_number", unique = false, nullable = true)
	private Integer pageNumber;

	@OneToOne(targetEntity = DealBookImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "deal_book_id")
	private DealBook dealBook;

	@OneToOne(targetEntity = MerchantDealImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "merchant_deal_id")
	private MerchantDeal merchantDeal;

	@Embedded
	private CreatedUpdated createdUpdated;

	public DealBookContentImpl()
	{}

	public DealBookContentImpl(final MerchantDeal merchantDeal, final DealBook dealBook)
	{
		this.merchantDeal = merchantDeal;
		this.dealBook = dealBook;
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
	public DealBook getDealBook()
	{
		return dealBook;
	}

	@Override
	public MerchantDeal getMerchantDeal()
	{
		return merchantDeal;
	}

	@Override
	public void setPageNumber(Integer pageNumber)
	{
		this.pageNumber = pageNumber;
	}

	@Override
	public Integer getPageNumber()
	{
		return pageNumber;
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

		if (!(obj instanceof DealBookContentImpl))
		{
			return false;
		}

		final DealBookContentImpl other = (DealBookContentImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getMerchantDeal(), other.getDealBook()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getMerchantDeal()).append(getDealBook()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
