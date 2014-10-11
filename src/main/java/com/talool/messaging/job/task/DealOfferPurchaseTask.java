package com.talool.messaging.job.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.SearchOptions;
import com.talool.core.service.ServiceException;
import com.talool.messaging.job.DealOfferPurchaseJob;
import com.talool.messaging.job.RecipientStatus;
import com.talool.service.ServiceConfig;
import com.talool.service.ServiceFactory;
import com.talool.stats.PaginatedResult;

/**
 * A task that sends a deal offer to a persistent recipient list. If the recipient list is empty or the messagingJob's jobState
 * is STARTED, FAILED or RUNNING, the job is not run.
 * 
 * @author dmccuen
 * 
 */
public class DealOfferPurchaseTask extends AbstractMessagingTask<DealOfferPurchaseJob>
{
	private static final Logger LOG = LoggerFactory.getLogger(DealOfferPurchaseTask.class);

	public DealOfferPurchaseTask(final DealOfferPurchaseJob offerJob)
	{
		super(offerJob);
	}

	@Override
	public DealOfferPurchaseJob call() throws Exception
	{
		int pageChunk = 0;
		Long totalRecipients = 0l;
		PaginatedResult<RecipientStatus> result = null;
		SearchOptions searchOpts = null;
		boolean stillProcessingBatch = true;
		final Map<Integer, Integer> failedPages = new HashMap<Integer, Integer>();

		while (stillProcessingBatch)
		{
			// recipientStatus objects are removed as they are processed. we only increment pageChunk when MAX_FAILED_ATTEMPTS
			// is reached for a chunk (so we can move to the next available chunk)
			searchOpts = new SearchOptions.Builder().ascending(true).maxResults(ServiceConfig.get().getMessagingJobManagerTaskBatchSize())
					.page(pageChunk).sortProperty("recipientStatus.id").build();

			result = ServiceFactory.get().getMessagingService().getAvailableReceipientStatuses(messagingJob.getId(), searchOpts);

			if (result != null && CollectionUtils.isNotEmpty(result.getResults()))
			{
				if (result.getTotalResults() == 0)
				{
					LOG.warn(String.format("Empty recipients for DealOfferTask with jobId %d and deal '%s'", messagingJob.getId(),
							messagingJob.getDealOffer().getTitle()));

					return messagingJob;
				}

				LOG.info(String.format("Starting DealOfferTask with jobId %d deal '%s' recipients %d", messagingJob.getId(), messagingJob
						.getDealOffer().getTitle(), totalRecipients));

				// persist started state
				setJobAsStarted();

				// totalRecipients will not change
				totalRecipients = result.getTotalResults();
				try
				{
					ServiceFactory.get().getMessagingService().processDealOfferPurchases(messagingJob, result.getResults());

				}
				catch (ServiceException se)
				{
					Integer failedAttempts = failedPages.get(pageChunk);
					failedAttempts = failedAttempts == null ? 1 : failedAttempts + 1;
					failedPages.put(pageChunk, failedAttempts);
					if (failedAttempts < ServiceConfig.get().getMessagingJobManagerTaskMaxAttempts())
					{
						LOG.error(String.format("Batch fail chunk %d failedAttempts %d", pageChunk, failedAttempts), se);
					}
					else
					{
						LOG.error(String.format("Max batch failures chunk %d failedAttempts %d - Advancing to chunk %d", pageChunk,
								failedAttempts, ++pageChunk), se);
					}

				}

			}
			else
			{
				stillProcessingBatch = false;
			}

		}

		// determine if any of the batches reached their retry max
		int numRetriesExhausted = 0;

		if (MapUtils.isNotEmpty(failedPages))
		{
			for (Entry<Integer, Integer> entry : failedPages.entrySet())
			{
				if (entry.getValue() == ServiceConfig.get().getMessagingJobManagerTaskMaxAttempts())
				{
					numRetriesExhausted++;
				}
			}
		}

		if (numRetriesExhausted > 0)
		{
			LOG.info(String.format("DealOfferTask with jobId %d has %d batch failures", messagingJob.getId(), numRetriesExhausted));
			setJobAsFailed();
		}
		else
		{
			LOG.info(String.format("DealOfferTask with jobId %d completed successfully", messagingJob.getId()));
			setJobAsFinished();
		}

		return messagingJob;
	}
}