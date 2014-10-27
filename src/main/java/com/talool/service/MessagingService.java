package com.talool.service;

import java.util.List;

import com.talool.core.Customer;
import com.talool.core.DevicePresence;
import com.talool.core.SearchOptions;
import com.talool.core.service.ServiceException;
import com.talool.messaging.job.DealOfferPurchaseJob;
import com.talool.messaging.job.JobState;
import com.talool.messaging.job.MerchantGiftJob;
import com.talool.messaging.job.MessagingJob;
import com.talool.messaging.job.RecipientStatus;
import com.talool.stats.PaginatedResult;

/**
 * An interface for a Messaging Service which is responsible for generating messaging to customers
 * including Gifts.
 * 
 * @author clintz
 * 
 */
public interface MessagingService {
  /**
   * Schedules and persists the messaging job.
   * 
   * @param messagingJob
   * @throws ServiceException
   */
  public MessagingJob scheduleMessagingJob(final MessagingJob messagingJob, final List<Customer> targetedCustomers) throws ServiceException;

  /**
   * Gets the list of available ReceipientStatus. A ReceipientStatus is available if it does not
   * have a delieveryStatus equal to 'SUCCESS' . If the jobState is Finished this list may have been
   * cleaned and could be null.
   * 
   * @return
   */
  public PaginatedResult<RecipientStatus> getAvailableReceipientStatuses(final Long jobId, final SearchOptions searchOpts) throws ServiceException;

  public MessagingJob getMessagingJob(final Long jobId) throws ServiceException;

  public void incrementGiftOpens(final Long jobId, final int totalOpens) throws ServiceException;

  public List<MessagingJob> getMessagingJobsByMerchantAccount(final Long createdByMerchantAccountId) throws ServiceException;

  public List<MessagingJob> getJobsToProcess() throws ServiceException;

  /**
   * Updates the running time of the messaging job. The running time is essentially the heart beat
   * time of the job.
   * 
   * @param messagingJob
   * @throws ServiceException
   */
  public void updateMessagingJobRunningTime(final MessagingJob messagingJob) throws ServiceException;


  /**
   * Batch create DealAcquires and saves them. Upon success, generate a batch of Gift
   * 
   * @param job
   * @param recipientStatuses
   * @throws ServiceException
   */
  public void processMerchantGifts(final MerchantGiftJob job, final List<RecipientStatus> recipientStatuses) throws ServiceException;

  /**
   * Batch give DealOffer to recipients
   * 
   * @param job
   * @param recipientStatuses
   * @throws ServiceException
   */


  /**
   * Batch give DealOffer to recipients
   * 
   * @param job
   * @param recipientStatuses
   * @throws ServiceException
   */
  public void processDealOfferPurchases(final DealOfferPurchaseJob job, final List<RecipientStatus> recipientStatuses) throws ServiceException;

  
  /**
   * Updates the state of a messaging job
   * 
   * @param jobId
   * @param jobState
   * @throws ServiceException
   */
  public void updateMessagingJobState(final Long jobId, final JobState jobState) throws ServiceException;

  public void save(final Object entity) throws ServiceException;

  public void merge(final Object entity) throws ServiceException;

  public void updateDevicePresences(final List<DevicePresence> devicePresences) throws ServiceException;

}
