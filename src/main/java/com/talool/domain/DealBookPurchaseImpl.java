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

import com.talool.core.Customer;
import com.talool.core.DealBook;
import com.talool.core.DealBookPurchase;

/**
 * Deal BookPurchase implementation
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "deal_book_purchase", catalog = "public")
public class DealBookPurchaseImpl implements DealBookPurchase
{
	private static final long serialVersionUID = -2356911722279787849L;
	private static final Logger LOG = LoggerFactory.getLogger(DealBookPurchaseImpl.class);

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_dbp_seq")
	@SequenceGenerator(name = "my_dbp_seq", sequenceName = "deal_book_purchase_deal_book_purchase_id_seq")
	@Column(name = "deal_book_purchase_id", unique = true, nullable = false)
	private Long id;

	@OneToOne(targetEntity = DealBookImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "deal_book_id")
	private DealBook dealBook;

	@OneToOne(targetEntity = CustomerImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Embedded
	private CreatedUpdated createdUpdated;

	public DealBookPurchaseImpl()
	{}

	public DealBookPurchaseImpl(final DealBook dealBook, final Customer customer)
	{
		this.dealBook = dealBook;
		this.customer = customer;
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
	public Customer getCustomer()
	{
		return customer;
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

		if (!(obj instanceof DealBookPurchaseImpl))
		{
			return false;
		}

		final DealBookPurchaseImpl other = (DealBookPurchaseImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getDealBook(), other.getCustomer()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getDealBook()).append(getCustomer()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
