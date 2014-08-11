package com.talool.messaging.job.task;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.messaging.job.MessagingJob;

/**
 * A task that sends gifts to a persistent recipient list
 * 
 * @author clintz
 * 
 */
public class MerchantGiftTask implements Callable<MessagingJob>
{
	private static final Logger LOG = LoggerFactory.getLogger(MerchantGiftTask.class);
	final MessagingJob messagingJob;

	public MerchantGiftTask(final MessagingJob messagingJob)
	{
		this.messagingJob = messagingJob;
	}

	@Override
	public MessagingJob call() throws Exception
	{
		LOG.info("Call() starting job");
		return messagingJob;
	}

}