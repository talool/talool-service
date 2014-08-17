package com.talool.messaging.job.task;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.messaging.job.MerchantGiftJob;
import com.talool.messaging.job.RecipientStatus;
import com.talool.service.ServiceFactory;

/**
 * A task that sends gifts to a persistent recipient list. If the recipient list
 * is empty or the messagingJob's jobState is STARTED, FAILED or RUNNING, the
 * job is not run.
 * 
 * @author clintz
 * 
 */
public class MerchantGiftTask extends AbstractMessagingTask<MerchantGiftJob>
{
	private static final Logger LOG = LoggerFactory.getLogger(MerchantGiftTask.class);

	public MerchantGiftTask(final MerchantGiftJob messagingJob)
	{
		super(messagingJob);
	}

	@Override
	public MerchantGiftJob call() throws Exception
	{
		try
		{
			List<RecipientStatus> recipients = ServiceFactory.get().getMessagingService().getReceipientStatuses(messagingJob.getId());

			if (CollectionUtils.isEmpty(recipients))
			{
				LOG.warn(String.format("Empty recipients for merchantGiftTask with jobId %d and deal '%s'", messagingJob.getId(), messagingJob
						.getDeal().getTitle()));

				return messagingJob;
			}

			LOG.info(String.format("Starting merchantGiftTask with jobId %d deal '%s' recipients %d", messagingJob.getId(), messagingJob
					.getDeal().getTitle(), recipients.size()));

			// persist started state
			setJobAsStarted();

			// TODO This is where we create Gifts, send via SendGrid, and associate
			// Gifts with jobIds
			for (RecipientStatus rs : recipients)
			{
				LOG.info("sending to:" + rs.getCustomer().getEmail());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return messagingJob;
	}
}