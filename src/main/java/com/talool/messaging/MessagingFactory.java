package com.talool.messaging;

import java.util.Date;
import java.util.concurrent.Callable;

import com.talool.core.Customer;
import com.talool.core.MerchantAccount;
import com.talool.domain.job.MessagingJobImpl;
import com.talool.messaging.job.MessagingJob;
import com.talool.messaging.job.task.MerchantGiftTask;

/**
 * Factory for creating Messaging related objects
 * 
 * @author clintz
 * 
 */
public class MessagingFactory
{
	/**
	 * Creates a new messagingJobBuilder
	 * 
	 * @return MessagingJobBuilder
	 */
	public static MessagingJob newMessagingJob(final MerchantAccount createdByMerchantAccount, final Customer fromCustomer,
			final Date scheduledStartDate, final String notes)
	{
		return new MessagingJobImpl(createdByMerchantAccount, fromCustomer, scheduledStartDate, notes);
	}

	/**
	 * Creates a new callable task mapped to the taskType
	 * 
	 * @param taskType
	 * @param messagingJob
	 * @return Callable<MessagingJob>
	 */
	public static Callable<MessagingJob> newMessagingJobTask(final String taskType, final MessagingJob messagingJob)
	{
		return new MerchantGiftTask(messagingJob);
	}

}
