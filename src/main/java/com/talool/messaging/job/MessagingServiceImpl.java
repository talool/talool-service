package com.talool.messaging.job;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.util.concurrent.FutureCallback;
import com.talool.core.service.ServiceException;
import com.talool.service.MessagingService;

/**
 * Implementation of MessagingJobService
 * 
 * @author clintz
 * 
 */
@Transactional(readOnly = true)
@Service
@Repository
public class MessagingServiceImpl implements MessagingService
{
	private static final Logger LOG = LoggerFactory.getLogger(MessagingServiceImpl.class);

	@Override
	public void scheduleMessagingJob(final MessagingJob messagingJob, final FutureCallback<MessagingJob> callback)
	{
		MessagingJobManager.get().submitJob(messagingJob, callback);
	}

	@Override
	public void scheduleMessagingJob(final MessagingJob messagingJob)
	{

	}

	@Override
	public MessagingJob getMessagingJob(final Long jobId) throws ServiceException
	{
		return null;
	}

	@Override
	public List<MessagingJob> getMessagingJobByMerchantAccount(final UUID merchantAccountId) throws ServiceException
	{
		return null;
	}

}
