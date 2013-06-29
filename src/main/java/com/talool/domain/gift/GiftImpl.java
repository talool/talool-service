package com.talool.domain.gift;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.talool.core.Customer;
import com.talool.core.DealAcquire;
import com.talool.core.gift.Gift;
import com.talool.core.gift.GiftStatus;
import com.talool.domain.CustomerImpl;
import com.talool.domain.DealAcquireImpl;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "gift")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "request_type", discriminatorType = DiscriminatorType.CHAR, length = 1)
@DiscriminatorValue("G")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public abstract class GiftImpl implements Gift
{
	private static final long serialVersionUID = -5248037524436980144L;

	@Id
	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "gift_id", unique = true, nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "from_customer_id")
	private Customer fromCustomer;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = DealAcquireImpl.class)
	@JoinColumn(name = "deal_acquire_id")
	private DealAcquire dealAcquire;

	@Type(type = "giftStatus")
	@Column(name = "gift_status", nullable = false, columnDefinition = "gift_status")
	private GiftStatus giftStatus = GiftStatus.PENDING;

	@Column(name = "receipient_name", length = 32)
	private String receipientName;

	@Column(name = "update_dt", unique = false, insertable = false, updatable = false)
	private Date updated;

	@Column(name = "create_dt", unique = false, insertable = false, updatable = false)
	private Date created;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "to_customer_id")
	private Customer toCustomer;

	@Override
	public UUID getId()
	{
		return id;
	}

	@Override
	public Date getCreated()
	{
		return created;
	}

	@Override
	public Date getUpdated()
	{
		return updated;
	}

	@Override
	public Customer getFromCustomer()
	{
		return fromCustomer;
	}

	@Override
	public void setFromCustomer(final Customer fromCustomer)
	{
		this.fromCustomer = fromCustomer;
	}

	@Override
	public DealAcquire getDealAcquire()
	{
		return dealAcquire;
	}

	@Override
	public void setDealAcquire(final DealAcquire dealAcquire)
	{
		this.dealAcquire = dealAcquire;
	}

	@Override
	public String getReceipientName()
	{
		return receipientName;
	}

	@Override
	public void setReceipientName(final String receipientName)
	{
		this.receipientName = receipientName;
	}

	@Override
	public GiftStatus getGiftStatus()
	{
		return giftStatus;
	}

	@Override
	public void setGiftStatus(final GiftStatus giftStatus)
	{
		this.giftStatus = giftStatus;

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

		if (!(obj instanceof GiftImpl))
		{
			return false;
		}

		final GiftImpl other = (GiftImpl) obj;

		return super.equals(obj) && new EqualsBuilder().append(getFromCustomer(), other.getFromCustomer())
				.append(getDealAcquire(), other.getDealAcquire()).append(getGiftStatus(), other.getGiftStatus()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getFromCustomer()).append(getDealAcquire()).append(getGiftStatus()).
				hashCode();
	}

	@Override
	public Customer getToCustomer()
	{
		return toCustomer;
	}

	@Override
	public void setToCustomer(Customer customer)
	{
		this.toCustomer = customer;

	}

}
