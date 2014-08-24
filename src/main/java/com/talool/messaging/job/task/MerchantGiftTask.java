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
import com.talool.messaging.job.MerchantGiftJob;
import com.talool.messaging.job.RecipientStatus;
import com.talool.service.ServiceFactory;
import com.talool.stats.PaginatedResult;

/**
 * A task that sends gifts to a persistent recipient list. If the recipient list is empty or the messagingJob's jobState
 * is STARTED, FAILED or RUNNING, the job is not run.
 * 
 * @author clintz
 * 
 */
public class MerchantGiftTask extends AbstractMessagingTask<MerchantGiftJob>
{
	private static final Logger LOG = LoggerFactory.getLogger(MerchantGiftTask.class);
	private static final int MAX_FAILED_ATTEMPTS = 3;

	public MerchantGiftTask(final MerchantGiftJob messagingJob)
	{
		super(messagingJob);
	}

	@Override
	public MerchantGiftJob call() throws Exception
	{
		int page = 0;
		int resultsPerPage = 2;
		Long totalRecipients = 0l;
		PaginatedResult<RecipientStatus> result = null;
		SearchOptions searchOpts = null;
		boolean stillProcessingBatch = true;
		final Map<Integer, Integer> failedPages = new HashMap<Integer, Integer>();

		while (stillProcessingBatch)
		{
			searchOpts = new SearchOptions.Builder().ascending(true).maxResults(resultsPerPage).page(page)
					.sortProperty("recipientStatus.id").build();

			result = ServiceFactory.get().getMessagingService().getAvailableReceipientStatuses(messagingJob.getId(), searchOpts);

			if (result != null && CollectionUtils.isNotEmpty(result.getResults()))
			{
				if (result.getTotalResults() == 0)
				{
					LOG.warn(String.format("Empty recipients for merchantGiftTask with jobId %d and deal '%s'", messagingJob.getId(),
							messagingJob.getDeal().getTitle()));

					return messagingJob;
				}

				LOG.info(String.format("Starting merchantGiftTask with jobId %d deal '%s' recipients %d", messagingJob.getId(), messagingJob
						.getDeal().getTitle(), totalRecipients));

				// persist started state
				setJobAsStarted();

				totalRecipients = result.getTotalResults(); // this should not change
				try
				{
					ServiceFactory.get().getMessagingService().processMerchantGifts(messagingJob, result.getResults());

					if ((page + 1 * resultsPerPage) >= totalRecipients)
					{
						stillProcessingBatch = false;
					}
					else
					{
						++page;
					}

				}
				catch (ServiceException se)
				{
					Integer failedAttempts = failedPages.get(page);
					failedAttempts = failedAttempts == null ? 1 : failedAttempts + 1;
					failedPages.put(page, failedAttempts);
					if (failedAttempts < MAX_FAILED_ATTEMPTS)
					{
						LOG.error(String.format("Batch fail page %d failedAttempts %d", page, failedAttempts), se);
					}
					else
					{
						LOG.error(
								String.format("Max batch failures page %d failedAttempts %d - Advancing to page %d", page, failedAttempts, ++page), se);
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
				if (entry.getValue() == MAX_FAILED_ATTEMPTS)
				{
					numRetriesExhausted++;
				}
			}
		}

		if (numRetriesExhausted > 0)
		{
			LOG.info("merchantGiftTask with jobId %d has %d batch failures", messagingJob.getId(), numRetriesExhausted);
			setJobAsFailed();
		}
		else
		{
			LOG.info("merchantGiftTask with jobId %d completed successfully", messagingJob.getId());
			setJobAsFinished();
		}

		return messagingJob;
	}
}