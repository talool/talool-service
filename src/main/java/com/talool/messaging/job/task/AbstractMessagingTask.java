package com.talool.messaging.job.task;

import java.util.Calendar;
import java.util.concurrent.Callable;

import com.talool.core.service.ServiceException;
import com.talool.messaging.job.JobState;
import com.talool.messaging.job.MerchantGiftJob;
import com.talool.messaging.job.MessagingJob;
import com.talool.service.ServiceFactory;

/**
 * An Abstract messaging task. All Messaging tasks should derive from this
 * class.
 * 
 * @author clintz
 * 
 */
public abstract class AbstractMessagingTask<T extends MessagingJob> implements Callable<MerchantGiftJob>
{
	protected final T messagingJob;

	public AbstractMessagingTask(T messagingJob)
	{
		this.messagingJob = messagingJob;
	}

	public void setJobAsStarted() throws ServiceException
	{
		updateJobState(JobState.STARTED);
	}

	public void setJobAsFinished() throws ServiceException
	{
		updateJobState(JobState.FINISHED);
	}

	public void setJobAsFailed() throws ServiceException
	{
		updateJobState(JobState.FAILED);
	}

	protected void updateJobState(JobState jobState) throws ServiceException
	{
		messagingJob.setJobState(jobState);
		messagingJob.setRunningUpdateTime(Calendar.getInstance().getTime());
		ServiceFactory.get().getMessagingService().merge(messagingJob);
	}

}
