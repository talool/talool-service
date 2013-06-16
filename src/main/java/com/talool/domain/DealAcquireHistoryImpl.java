package com.talool.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import com.talool.core.AcquireStatus;
import com.talool.core.Customer;
import com.talool.core.DealAcquire;
import com.talool.core.DealAcquireHistory;
import com.talool.core.gift.Gift;
import com.talool.domain.gift.GiftImpl;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "deal_acquire_history", catalog = "public")
@Immutable
public class DealAcquireHistoryImpl implements DealAcquireHistory
{
	private static final long serialVersionUID = 7508266282237562791L;

	public static class DealAcquireHistoryPK implements Serializable
	{
		private static final long serialVersionUID = -9082450751406248126L;

		@OneToOne(targetEntity = DealAcquireImpl.class, fetch = FetchType.LAZY)
		@JoinColumn(name = "deal_acquire_id")
		protected DealAcquire dealAcquire;

		@Column(name = "update_dt", unique = false, insertable = false, updatable = false)
		protected Date updated;

	}

	@EmbeddedId
	private final DealAcquireHistoryPK primaryKey;

	@Type(type = "acquireStatus")
	@Column(name = "acquire_status", nullable = false, columnDefinition = "acquire_status", updatable = false)
	private AcquireStatus acquireStatus;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToOne(targetEntity = GiftImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "gift_id")
	private Gift gift;

	@Column(name = "gift_id", insertable = false, updatable = false)
	private UUID giftId;

	public DealAcquireHistoryImpl()
	{
		this.primaryKey = new DealAcquireHistoryPK();
	}

	@Override
	public Date getUpdated()
	{
		return primaryKey.updated;
	}

	@Override
	public AcquireStatus getAcquireStatus()
	{
		return acquireStatus;
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

		if (!(obj instanceof DealAcquireHistoryImpl))
		{
			return false;
		}

		final DealAcquireHistoryImpl other = (DealAcquireHistoryImpl) obj;

		if (getDealAcquire() != other.getDealAcquire())
		{
			return false;
		}

		return new EqualsBuilder().append(getUpdated(), other.getUpdated())
				.append(getDealAcquire(), other.getDealAcquire()).
				append(getCustomer(), other.getCustomer()).
				append(getGift(), other.getGift()).
				append(getUpdated(), other.getUpdated()).
				isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getUpdated()).append(getDealAcquire()).
				append(getCustomer()).append(getGift()).append(getUpdated()).
				hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public DealAcquire getDealAcquire()
	{
		return primaryKey.dealAcquire;
	}

	@Override
	public Gift getGift()
	{
		return gift;
	}

	@Override
	public UUID getGiftId()
	{
		return giftId;
	}

}
