package com.talool.domain;

import java.io.Serializable;
import java.util.Date;

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

import com.talool.core.AcquireStatus;
import com.talool.core.Customer;
import com.talool.core.DealAcquire;
import com.talool.core.DealAcquireHistory;
import com.talool.core.Merchant;

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

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = AcquireStatusImpl.class)
	@JoinColumn(name = "acquire_status_id")
	private AcquireStatus status;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = MerchantImpl.class)
	@JoinColumn(name = "shared_by_merchant_id")
	private Merchant sharedByMerchant;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "shared_by_customer_id")
	private Customer sharedByCustomer;

	@Column(name = "share_cnt", unique = false, nullable = true)
	private Integer shareCount;

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
		return status;
	}

	@Override
	public Customer getCustomer()
	{
		return customer;
	}

	@Override
	public Merchant getSharedByMerchant()
	{
		return sharedByMerchant;
	}

	@Override
	public Customer getSharedByCustomer()
	{
		return sharedByCustomer;
	}

	@Override
	public Integer getShareCount()
	{
		return shareCount;
	}

	@Override
	public void setShareCount(Integer shareCount)
	{
		this.shareCount = shareCount;

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
				.append(getDealAcquire(), other.getDealAcquire()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getUpdated()).append(getDealAcquire()).hashCode();
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

}
