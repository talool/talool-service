package com.talool.messaging.job;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.Query;
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

	public MessagingServiceImpl()
	{}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void scheduleMessagingJob(final MessagingJob messagingJob, final FutureCallback<MessagingJob> callback,
			final List<Customer> targetedCustomers)
	{
		// TODO FILL IN
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

	@Override
	public List<MessagingJob> getJobsToProcess() throws ServiceException
	{
		try
		{
			final Query query = sessionFactory.getCurrentSession().createQuery(
					"from MessagingJobImpl where scheduledStartDate<=:startDate and jobState not in (:jobStates)");
			query.setParameter("startDate", Calendar.getInstance().getTime());
			query.setParameterList("jobStates", new JobState[] { JobState.FINISHED, JobState.STARTED });

			@SuppressWarnings("unchecked")
			List<MessagingJob> messagingJobs = query.list();

			return messagingJobs;

		}
		catch (Exception ex)
		{
			throw new ServiceException(ex);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RecipientStatus> getReceipientStatuses(final Long jobId) throws ServiceException
	{
		List<RecipientStatus> recipientStatuses = null;

		try
		{
			final Query query = sessionFactory.getCurrentSession().createQuery(
					"from RecipientStatusImpl as rs left join fetch rs.customer where rs.messagingJobId=:messagingJobId");
			query.setParameter("messagingJobId", jobId);
			recipientStatuses = query.list();
			return recipientStatuses;
		}
		catch (Exception ex)
		{
			throw new ServiceException(ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMessagingJobRunningTime(final MessagingJob messagingJob) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory.getCurrentSession().createQuery(
					"update MessagingJobImpl set runningUpdateTime=:time where id=:id");
			query.setParameter("id", messagingJob.getId());
			query.setParameter("time", Calendar.getInstance().getTime());
			int rowCnt = query.executeUpdate();
			if (rowCnt != 1)
			{
				throw new ServiceException("Problem updating messagingJobRunningTime");
			}
		}
		catch (ServiceException se)
		{
			throw se;
		}
		catch (Exception ex)
		{
			throw new ServiceException(ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void merge(final Object entity) throws ServiceException
	{
		try
		{
			getCurrentSession().saveOrUpdate(entity);
		}
		catch (Exception e)
		{
			throw new ServiceException("There was a problem saving entity", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final Object entity) throws ServiceException
	{
		try
		{
			getCurrentSession().merge(entity);
			// daoDispatcher.(recipientStatus);
		}
		catch (Exception e)
		{
			throw new ServiceException("There was a problem saving entity", e);
		}
	}

}
