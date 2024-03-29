package com.talool.domain;

import java.util.Date;
import java.util.UUID;

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
import org.hibernate.annotations.Type;

import com.talool.core.AcquireStatus;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.gift.Gift;
import com.talool.domain.gift.GiftImpl;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "deal_acquire", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class DealAcquireImpl implements DealAcquire
{
	private static final long serialVersionUID = -4850285379175281009L;

	@Id
	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "deal_acquire_id", unique = true, nullable = false)
	private UUID id;

	@OneToOne(targetEntity = DealImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "deal_id")
	private Deal deal;

	@OneToOne(targetEntity = GiftImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "gift_id")
	private Gift gift;

	@Column(name = "gift_id", insertable = false, updatable = false)
	private UUID giftId;

	@Type(type = "acquireStatus")
	@Column(name = "acquire_status", nullable = false, columnDefinition = "acquire_status")
	private AcquireStatus acquireStatus;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Type(type = "geomType")
	@Column(name = "redeemed_at_geom", nullable = true)
	private com.vividsolutions.jts.geom.Geometry redeemedAtGeometry;

	@Column(name = "redemption_code", length = 6)
	private String redemptionCode;

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

	public void setDeal(Deal deal)
	{
		this.deal = deal;
	}

	@Override
	public AcquireStatus getAcquireStatus()
	{
		return acquireStatus;
	}

	@Override
	public void setAcquireStatus(final AcquireStatus acquireStatus)
	{
		this.acquireStatus = acquireStatus;
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

	public void setRedemptionDate(final Date date)
	{
		this.redemptionDate = date;
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
				.append(getDeal(), other.getDeal()).append(getCustomer(), other.getCustomer()).
				append(getGiftId(), other.getGiftId()).
				isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getAcquireStatus()).append(getDeal()).append(getCustomer()).append(getGiftId())
				.hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	public void setRedemptionCode(final String redemptionCode)
	{
		this.redemptionCode = redemptionCode;
	}

	@Override
	public String getRedemptionCode()
	{
		return redemptionCode;
	}

	@Override
	public Geometry getRedeemedAtGeometry()
	{
		return redeemedAtGeometry;
	}

	@Override
	public void setRedeemedAtGeometry(final Geometry getRedeemedAtGeometry)
	{
		this.redeemedAtGeometry = getRedeemedAtGeometry;
	}

	@Override
	public Gift getGift()
	{
		return gift;
	}

	@Override
	public void setGift(final Gift gift)
	{
		this.gift = gift;
	}

	@Override
	public UUID getGiftId()
	{
		return giftId;
	}

}
