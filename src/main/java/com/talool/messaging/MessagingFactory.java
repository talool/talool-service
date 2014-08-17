package com.talool.messaging;

import java.util.Date;
import java.util.concurrent.Callable;

import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.MerchantAccount;
import com.talool.domain.job.MerchantGiftJobImpl;
import com.talool.domain.job.RecipientStatusImpl;
import com.talool.messaging.job.MerchantGiftJob;
import com.talool.messaging.job.MessagingJob;
import com.talool.messaging.job.RecipientStatus;
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
	public static MerchantGiftJob newMerchantGiftJob(final MerchantAccount createdByMerchantAccount, final Customer fromCustomer,
			final Deal deal, final Date scheduledStartDate, final String notes)
	{
		return new MerchantGiftJobImpl(createdByMerchantAccount, fromCustomer, deal, scheduledStartDate, notes);
	}

	/**
	 * Creates a new callable task mapped to the taskType
	 * 
	 * @param taskType
	 * @param messagingJob
	 * @return Callable<MessagingJob>
	 */
	public static Callable<MerchantGiftJob> newMerchantGiftJobTask(final String taskType, final MerchantGiftJob messagingJob)
	{
		return new MerchantGiftTask(messagingJob);
	}

	public static RecipientStatus newRecipientStatus(final MessagingJob messagingJob, final Customer customer)
	{
		return new RecipientStatusImpl(messagingJob.getId(), customer);
	}

}
