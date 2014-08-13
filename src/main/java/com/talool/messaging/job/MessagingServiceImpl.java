package com.talool.messaging.job;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.util.concurrent.FutureCallback;
import com.talool.core.Customer;
import com.talool.core.service.ServiceException;
import com.talool.messaging.MessagingFactory;
import com.talool.service.AbstractHibernateService;
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
public class MessagingServiceImpl extends AbstractHibernateService implements MessagingService
{
	private static final Logger LOG = LoggerFactory.getLogger(MessagingServiceImpl.class);

	@Override
	public void scheduleMessagingJob(final MessagingJob messagingJob, final FutureCallback<MessagingJob> callback,
			final List<Customer> targetedCustomers)
	{
		MessagingJobManager.get().submitJob(messagingJob, callback);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void scheduleMessagingJob(final MessagingJob messagingJob, final List<Customer> targetedCustomers)
	{
		daoDispatcher.save(messagingJob);

		// need to create the tracking/recipient status records
		for (Customer customer : targetedCustomers)
		{
			daoDispatcher.save(MessagingFactory.newRecipientStatus(messagingJob, customer));
		}

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
