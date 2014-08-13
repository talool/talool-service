package com.talool.domain.job;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Type;

import com.talool.core.Customer;
import com.talool.domain.CustomerImpl;
import com.talool.messaging.job.DeliveryStatus;
import com.talool.messaging.job.MessagingJob;
import com.talool.messaging.job.RecipientStatus;

/**
 * Implementation of MessagingReceipientStatus . Represents the state of job
 * receipient.
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "recipient_status", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class RecipientStatusImpl implements RecipientStatus
{
	private static final long serialVersionUID = -1662638530762854056L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_rs_seq")
	@SequenceGenerator(name = "my_rs_seq", sequenceName = "recipient_status_recipient_status_id_seq")
	@Column(name = "recipient_status_id", unique = true, nullable = false)
	private Long id;

	@Type(type = "deliveryStatus")
	@Column(name = "delivery_status", nullable = false, columnDefinition = "delivery_status")
	private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

	@OneToOne(targetEntity = MessagingJobImpl.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "messaging_job_id")
	private MessagingJob messagingJob;

	@OneToOne(targetEntity = CustomerImpl.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	public RecipientStatusImpl(final MessagingJob messagingJob, final Customer customer)
	{
		this.messagingJob = messagingJob;
		this.customer = customer;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public Customer getCustomer()
	{
		return customer;
	}

	@Override
	public MessagingJob getMessagingJob()
	{
		return messagingJob;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public DeliveryStatus getDeliveryStatus()
	{
		return deliveryStatus;
	}

}
