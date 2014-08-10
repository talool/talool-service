package com.talool.service;

import java.util.List;
import java.util.UUID;

import com.google.common.util.concurrent.FutureCallback;
import com.talool.core.service.ServiceException;
import com.talool.messaging.job.MessagingJob;

/**
 * An interface for a Messaging Service which is responsible for generating
 * messaging to customers including Gift emails.
 * 
 * @author clintz
 * 
 */
public interface MessagingService
{
	/**
	 * Schedules and persists the messaging job. The future callback is also
	 * registered.
	 * 
	 * @param messagingJob
	 * @param callback
	 * @throws ServiceException
	 */
	public void scheduleMessagingJob(final MessagingJob messagingJob, final FutureCallback<MessagingJob> callback)
			throws ServiceException;

	public MessagingJob getMessagingJob(final Long jobId) throws ServiceException;

	public List<MessagingJob> getMessagingJobByMerchantAccount(final UUID merchantAccountId) throws ServiceException;
}
