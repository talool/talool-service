package com.talool.domain.gift;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.talool.core.gift.GiftRequest;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "gift_request")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "request_type", discriminatorType = DiscriminatorType.CHAR, length = 1)
@DiscriminatorValue("G")
public abstract class GiftRequestImpl implements GiftRequest
{
	private static final long serialVersionUID = -5248037524436980144L;

	@Id
	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "gift_request_id", unique = true, nullable = false)
	private UUID id;

	@Type(type = "pg-uuid")
	@Column(name = "customer_id", unique = true, nullable = false)
	private UUID customerId;

	@Type(type = "pg-uuid")
	@Column(name = "deal_acquire_id", unique = true, nullable = false)
	private UUID dealAcquireId;

	@Column(name = "receipient_name", length = 32)
	private String receipientName;

	@Column(name = "is_accepted")
	private boolean isAccepted;

	@Column(name = "update_dt", unique = false, insertable = false, updatable = false)
	private Date updated;

	@Column(name = "create_dt", unique = false, insertable = false, updatable = false)
	private Date created;

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
	public UUID getCustomerId()
	{
		return customerId;
	}

	@Override
	public void setCustomerId(UUID customerId)
	{
		this.customerId = customerId;
	}

	@Override
	public UUID getDealAcquireId()
	{
		return dealAcquireId;
	}

	@Override
	public void setDealAcquireId(final UUID dealAcquireId)
	{
		this.dealAcquireId = dealAcquireId;
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

		if (!(obj instanceof GiftRequestImpl))
		{
			return false;
		}

		final GiftRequestImpl other = (GiftRequestImpl) obj;

		return super.equals(obj) && new EqualsBuilder().append(getCustomerId(), other.getCustomerId())
				.append(getDealAcquireId(), other.getDealAcquireId()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getCustomerId()).append(getDealAcquireId()).hashCode();
	}

	@Override
	public boolean isAccepted()
	{
		return isAccepted;
	}

	@Override
	public void setIsAccepted(final boolean isAccepted)
	{
		this.isAccepted = isAccepted;
	}

}
