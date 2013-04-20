package com.talool.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Type;

import com.talool.core.AcquireStatus;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.Location;
import com.talool.core.Merchant;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "deal_acquire", catalog = "public")
public class DealAcquireImpl implements DealAcquire
{
	private static final long serialVersionUID = -4850285379175281009L;

	@Id
	@Access(AccessType.FIELD)
	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "deal_acquire_id", unique = true, nullable = false)
	private UUID id;

	@Access(AccessType.FIELD)
	@OneToOne(targetEntity = DealImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "deal_id")
	private Deal deal;

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

	@Embedded
	@Target(LocationImpl.class)
	private Location location;

	@Column(name = "redemption_dt", unique = false, nullable = true)
	private Date redemptionDate;

	@Embedded
	private CreatedUpdated createdUpdated;

	@Override
	public UUID getId()
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
	public Deal getDeal()
	{
		return deal;
	}

	@Override
	public void setDeal(Deal deal)
	{
		this.deal = deal;
	}

	@Override
	public AcquireStatus getAcquireStatus()
	{
		return status;
	}

	@Override
	public void setAcquireStatus(AcquireStatus acquireStatus)
	{
		this.status = acquireStatus;
	}

	@Override
	public Customer getCustomer()
	{
		return customer;
	}

	@Override
	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}

	@Override
	public Merchant getSharedByMerchant()
	{
		return sharedByMerchant;
	}

	@Override
	public void setSharedByMerchant(Merchant merchant)
	{
		this.setSharedByMerchant(merchant);

	}

	@Override
	public Customer getSharedByCustomer()
	{
		return sharedByCustomer;
	}

	@Override
	public void setSharedByCusomer(Customer customer)
	{
		this.sharedByCustomer = customer;

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
	public Location getLocation()
	{
		return location;
	}

	@Override
	public void setLocation(Location location)
	{
		this.location = location;
	}

	@Override
	public Date getRedemptionDate()
	{
		return redemptionDate;
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

		if (!(obj instanceof DealAcquireImpl))
		{
			return false;
		}

		final DealAcquireImpl other = (DealAcquireImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getAcquireStatus(), other.getAcquireStatus())
				.append(getDeal(), other.getDeal()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getAcquireStatus()).append(getDeal()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
