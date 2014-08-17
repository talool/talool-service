package com.talool.service;

import java.util.List;
import java.util.UUID;

import com.google.common.util.concurrent.FutureCallback;
import com.talool.core.Customer;
import com.talool.core.service.ServiceException;
import com.talool.messaging.job.MessagingJob;
import com.talool.messaging.job.RecipientStatus;

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

	/**
	 * Gets the list of MessagingReceipientStatus. If the jobState is Finished,
	 * this list may have been cleaned and could be null.
	 * 
	 * @return
	 */
	public List<RecipientStatus> getReceipientStatuses(final Long jobId) throws ServiceException;

	public MessagingJob getMessagingJob(final Long jobId) throws ServiceException;

	public List<MessagingJob> getMessagingJobByMerchantAccount(final UUID merchantAccountId) throws ServiceException;

	public List<MessagingJob> getJobsToProcess() throws ServiceException;

	/**
	 * Updates the running time of the messaging job. The running time is
	 * essentially the heart beat time of the job.
	 * 
	 * @param messagingJob
	 * @throws ServiceException
	 */
	public void updateMessagingJobRunningTime(final MessagingJob messagingJob) throws ServiceException;

	public void save(final Object entity) throws ServiceException;

	public void merge(final Object entity) throws ServiceException;
}
