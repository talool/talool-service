package com.talool.service;

import java.util.List;
import java.util.UUID;

import com.google.common.util.concurrent.FutureCallback;
import com.talool.core.Customer;
import com.talool.core.service.ServiceException;
import com.talool.messaging.job.MessagingJob;

/**
 * An interface for a Messaging Service which is responsible for generating
 * messaging to customers including Gifts.
 * 
 * @author clintz
 * 
 */
public interface MessagingService
{
	/**
	 * Schedules and persists the messaging job. If the scheduledStartDate is now
	 * the job is immediately submitted and the future callback is registered.
	 * 
	 * @param messagingJob
	 * @param callback
	 * @throws ServiceException
	 */
	public void scheduleMessagingJob(final MessagingJob messagingJob, final FutureCallback<MessagingJob> callback,
			final List<Customer> targetedCustomers) throws ServiceException;

	/**
	 * Schedules and persists the messaging job.
	 * 
	 * @param messagingJob
	 * @throws ServiceException
	 */
	public void scheduleMessagingJob(final MessagingJob messagingJob, final List<Customer> targetedCustomers) throws ServiceException;

	public MessagingJob getMessagingJob(final Long jobId) throws ServiceException;

	public List<MessagingJob> getMessagingJobByMerchantAccount(final UUID merchantAccountId) throws ServiceException;
}
