package com.talool.domain.job;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.talool.core.Customer;
import com.talool.messaging.job.MessagingReceipientStatus;

/**
 * Implementation of MessagingReceipientStatus . Represents the state of job
 * receipient.
 * 
 * @author clintz
 * 
 */
public class MessagingReceipientStatusImpl implements MessagingReceipientStatus
{
	private static final long serialVersionUID = -1662638530762854056L;
	private Long id;
	private Customer customer;
	private MessagingStatus messagingStatus;

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
	public MessagingStatus getMessagingStatus()
	{
		return messagingStatus;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
