package com.talool.domain.job;

import java.util.Date;

import com.google.common.base.Preconditions;
import com.talool.core.Customer;
import com.talool.core.MerchantAccount;
import com.talool.messaging.job.MessagingJob;

/**
 * Messaging Job Builder
 * 
 * @author clintz
 * 
 */
public class MessagingJobBuilder
{
	protected MerchantAccount createdByMerchantAccount;
	protected Customer fromCustomer;
	protected Date scheduledStartDate;
	protected String notes;

	public MessagingJobBuilder()
	{}

	public MessagingJobBuilder createdByMerchantAccount(final MerchantAccount createdByMerchantAccount)
	{
		this.createdByMerchantAccount = createdByMerchantAccount;
		return this;
	}

	public MessagingJobBuilder fromCustomer(final Customer fromCustomer)
	{
		this.fromCustomer = fromCustomer;
		return this;
	}

	public MessagingJobBuilder scheduledStartDate(final Date scheduledStartDate)
	{
		this.scheduledStartDate = scheduledStartDate;
		return this;
	}

	public MessagingJobBuilder notes(final String notes)
	{
		this.notes = notes;
		return this;
	}

	public MessagingJob build()
	{
		Preconditions.checkNotNull(createdByMerchantAccount);
		Preconditions.checkNotNull(scheduledStartDate);
		Preconditions.checkArgument(scheduledStartDate.getTime() >= System.currentTimeMillis());
		Preconditions.checkNotNull(fromCustomer);

		return new MessagingJobImpl(this);
	}

}
