package com.talool.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

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
public class DealAcquireHistoryImpl implements DealAcquireHistory
{
	private static final long serialVersionUID = 7508266282237562791L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_dealaq_h_seq")
	@SequenceGenerator(name = "my_dealaq_h_seq", sequenceName = "deal_acquire_history_deal_acquire_history_id_seq")
	@Column(name = "deal_acquire_history_id", unique = true, nullable = false)
	private Long id;

	@Access(AccessType.FIELD)
	@OneToOne(targetEntity = DealAcquireImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "deal_acquire_id")
	private DealAcquire dealAcquire;

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = AcquireStatusImpl.class)
	@JoinColumn(name = "acquire_status_id")
	private AcquireStatus status;

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = MerchantImpl.class)
	@JoinColumn(name = "shared_by_merchant_id")
	private Merchant sharedByMerchant;

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "shared_by_customer_id")
	private Customer sharedByCustomer;

	@Column(name = "share_cnt", unique = false, nullable = true)
	private Integer shareCount;

	@Column(name = "update_dt", unique = false, insertable = false, updatable = false)
	private Date updated;

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public Date getUpdated()
	{
		return updated;
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

		if (getId() != other.getId())
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
		return dealAcquire;
	}

}
