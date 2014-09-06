package com.talool.domain.activity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.talool.core.activity.Activity;
import com.talool.core.activity.ActivityEvent;
import com.talool.domain.Properties;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "activity", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class ActivityImpl implements Activity
{
	private static final long serialVersionUID = 771169851352564045L;

	@Id
	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "activity_id", unique = true, nullable = false)
	private UUID id;

	@Type(type = "pg-uuid")
	@Column(name = "customer_id", nullable = false)
	private UUID customerId;

	@Type(type = "pg-uuid")
	@Column(name = "gift_id")
	private UUID giftId;

	@Column(name = "activity_data", nullable = false)
	private byte[] activityData;

	@Type(type = "activityType")
	@Column(name = "activity_type", nullable = false)
	private ActivityEvent activityEvent;

	@Column(name = "activity_dt", insertable = false, updatable = false)
	private Date activityDate;

	@Column(name = "opened")
	private boolean opened;

	@Embedded
	private Properties props;

	@Override
	public UUID getId()
	{
		return id;
	}

	@Override
	public ActivityEvent getActivityEvent()
	{
		return activityEvent;
	}

	@Override
	public void setActivityEvent(final ActivityEvent activityEvent)
	{
		this.activityEvent = activityEvent;
	}

	@Override
	public UUID getCustomerId()
	{
		return customerId;
	}

	@Override
	public void setCustomerId(final UUID customerId)
	{
		this.customerId = customerId;
	}

	@Override
	public void setActivityData(final byte[] activityData)
	{
		this.activityData = activityData;
	}

	@Override
	public byte[] getActivityData()
	{
		return activityData;
	}

	@Override
	public Date getActivityDate()
	{
		return activityDate;
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

		if (!(obj instanceof ActivityImpl))
		{
			return false;
		}

		final ActivityImpl other = (ActivityImpl) obj;

		return super.equals(obj)
				&& new EqualsBuilder().append(getCustomerId(), other.getCustomerId()).append(getActivityEvent(), other.getActivityEvent())
						.append(getActivityDate(), other.getActivityDate()).append(getActivityData(), other.getActivityData()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getCustomerId()).append(getActivityEvent()).append(getActivityDate())
				.append(getActivityData()).hashCode();
	}

	@Override
	public UUID getGiftId()
	{
		return giftId;
	}

	@Override
	public void setGiftId(UUID giftId)
	{
		this.giftId = giftId;
	}

	@Override
	public Properties getProperties()
	{
		if (props == null)
		{
			props = new Properties();
		}
		return props;
	}

	@Override
	public boolean getIsOpened()
	{
		return opened;
	}

	@Override
	public void setIsOpened(boolean isOpened)
	{
		this.opened = isOpened;
	}
}
