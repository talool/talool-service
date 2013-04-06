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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Target;

import com.talool.core.AquireStatus;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAquire;
import com.talool.core.Location;
import com.talool.core.Merchant;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "deal_aquire", catalog = "public")
public class DealAquireImpl implements DealAquire
{
	private static final long serialVersionUID = -4850285379175281009L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_dealaq_seq")
	@SequenceGenerator(name = "my_dealaq_seq", sequenceName = "deal_aquire_deal_aquire_id_seq")
	@Column(name = "deal_aquire_id", unique = true, nullable = false)
	private Long id;

	@Access(AccessType.FIELD)
	@OneToOne(targetEntity = DealImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "deal_id")
	private Deal deal;

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = AquireStatusImpl.class)
	@JoinColumn(name = "aquire_status_id")
	private AquireStatus status;

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
	public AquireStatus getAquireStatus()
	{
		return status;
	}

	@Override
	public void setAquireStatus(AquireStatus aquireStatus)
	{
		this.status = aquireStatus;
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

		if (!(obj instanceof DealAquireImpl))
		{
			return false;
		}

		final DealAquireImpl other = (DealAquireImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getAquireStatus(), other.getAquireStatus())
				.append(getDeal(), other.getDeal()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getAquireStatus()).append(getDeal()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
