package com.talool.messaging.job;

import com.talool.messaging.job.task.AbstractMessagingTask;
import com.talool.messaging.job.task.DealOfferPurchaseTask;
import com.talool.messaging.job.task.MerchantGiftTask;

/**
 * A factory for messaging tasks. All messaging tasks need to be mapped into here based on the JobType
 * 
 * @author clintz
 * 
 */
public class MessagingTaskFactory
{
	/**
	 * Creates the appropriate messaging task given the MessagingJob instance.
	 * 
	 * @param job
	 * @return
	 */
	public static AbstractMessagingTask<? extends MessagingJob> createMessagingTask(final MessagingJob job)
	{
		if (job instanceof MerchantGiftJob)
		{
			return new MerchantGiftTask((MerchantGiftJob) job);
		}
		else if (job instanceof DealOfferPurchaseJob)
		{
			return new DealOfferPurchaseTask((DealOfferPurchaseJob) job);
		}
		else
		{
			return null;
		}
	}
}
