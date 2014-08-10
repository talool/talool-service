package com.talool.domain.job;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.talool.core.Customer;
import com.talool.core.MerchantAccount;
import com.talool.messaging.job.MessagingJob;
import com.talool.messaging.job.MessagingJobStats;
import com.talool.messaging.job.MessagingReceipientStatus;

/**
 * Messaging Job Implementation
 * 
 * @author clintz
 * 
 */
public class MessagingJobImpl implements MessagingJob
{
	private static final long serialVersionUID = -4134065711388463064L;

	private Long id;
	private MerchantAccount createdByMerchantAccount;
	private Customer fromCustomer;
	private JobState jobState;
	private Date created;
	private Date scheduledStartDate;

	private MessagingJobStats messagingJobStats;
	private List<MessagingReceipientStatus> messagingReceipientStatuses;

	private String notes;

	public MessagingJobImpl(final MessagingJobBuilder messagingJobBuilder)
	{
		this.createdByMerchantAccount = messagingJobBuilder.createdByMerchantAccount;
		this.fromCustomer = messagingJobBuilder.fromCustomer;
		this.notes = messagingJobBuilder.notes;
		this.scheduledStartDate = messagingJobBuilder.scheduledStartDate;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public MerchantAccount getCreatedByMerchantAccount()
	{
		return createdByMerchantAccount;
	}

	@Override
	public Customer getFromCustomer()
	{
		return fromCustomer;
	}

	@Override
	public JobState getJobState()
	{
		return jobState;
	}

	@Override
	public Date getCreated()
	{
		return created;
	}

	@Override
	public Date getScheduledStartDate()
	{
		return scheduledStartDate;
	}

	@Override
	public MessagingJobStats getMessagingJobStats()
	{
		return messagingJobStats;
	}

	@Override
	public List<MessagingReceipientStatus> getMessagingReceipientStatuses()
	{
		return messagingReceipientStatuses;
	}

	@Override
	public String getNotes()
	{
		return notes;
	}

	@Override
	public void setNotes(final String notes)
	{
		this.notes = notes;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
