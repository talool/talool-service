package com.talool.messaging.job;

import java.util.ArrayList;
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

import com.talool.core.AcquireStatus;
import com.talool.core.Customer;
import com.talool.core.DealAcquire;
import com.talool.core.SearchOptions;
import com.talool.core.service.ServiceException;
import com.talool.domain.DealAcquireImpl;
import com.talool.domain.job.RecipientStatusImpl;
import com.talool.messaging.MessagingFactory;
import com.talool.persistence.QueryHelper;
import com.talool.persistence.QueryHelper.QueryType;
import com.talool.service.AbstractHibernateService;
import com.talool.service.MessagingService;
import com.talool.stats.PaginatedResult;

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
	public MessagingJob scheduleMessagingJob(final MessagingJob messagingJob, final List<Customer> targetedCustomers)
	{
		messagingJob.setUsersTargerted(targetedCustomers.size());

		daoDispatcher.save(messagingJob);

		// need to create the tracking/recipient status records
		for (Customer customer : targetedCustomers)
		{
			daoDispatcher.save(MessagingFactory.newRecipientStatus(messagingJob, customer));
		}

		return messagingJob;

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
	public PaginatedResult<RecipientStatus> getAvailableReceipientStatuses(final Long jobId, final SearchOptions searchOpts)
			throws ServiceException
	{
		List<RecipientStatus> recipientStatuses = null;
		PaginatedResult<RecipientStatus> result = null;

		try
		{
			String newSql = QueryHelper.buildQuery(QueryType.RecipientStatusesCnt, null, null, true);
			Query query = sessionFactory.getCurrentSession().createQuery(newSql);
			query.setParameter("messagingJobId", jobId);
			final Long totalResults = (Long) query.uniqueResult();

			if (totalResults != null && totalResults > 0)
			{
				newSql = QueryHelper.buildQuery(QueryType.RecipientStatuses, null, searchOpts, false);
				query = sessionFactory.getCurrentSession().createQuery(newSql);
				query.setParameter("messagingJobId", jobId);
				QueryHelper.applyOffsetLimit(query, searchOpts);
				recipientStatuses = query.list();
			}

			result = new PaginatedResult<RecipientStatus>(searchOpts, totalResults, recipientStatuses);
			return result;
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

	@Override
	@Transactional(propagation = Propagation.NESTED, rollbackFor = ServiceException.class)
	public void processMerchantGifts(final MerchantGiftJob job, final List<RecipientStatus> recipientStatuses) throws ServiceException
	{
		final List<DealAcquire> dealAcquires = new ArrayList<DealAcquire>(recipientStatuses.size());

		for (RecipientStatus recipient : recipientStatuses)
		{

			DealAcquireImpl dac = new DealAcquireImpl();
			dac.setDeal(job.getDeal());
			dac.setAcquireStatus(AcquireStatus.PURCHASED);
			dac.setCustomer(recipient.getCustomer());
			dealAcquires.add(dac);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("processing to:" + recipient.getCustomer().getEmail());
			}

			save(dac);
		}

		daoDispatcher.flush(DealAcquireImpl.class);

		try
		{
			Query updateSends = sessionFactory.getCurrentSession().createQuery("update MerchantGiftJobImpl set sends=sends + :sends");
			updateSends.setParameter("sends", recipientStatuses.size());
			updateSends.executeUpdate();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		for (RecipientStatus recipient : recipientStatuses)
		{
			daoDispatcher.remove(recipient);
		}

		daoDispatcher.flush(RecipientStatusImpl.class);

		// TODO put the job id on the gift (or deal acquire)

		// LOG.info("sending to:" + rs.getCustomer().getEmail());
		// send the gift
		// ServiceFactory.get().getCustomerService()
		// .giftToEmail(messagingJob.getFromCustomer().getId(), daq.getId(), customer.getEmail().toLowerCase(),
		// customer.getFullName());

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = ServiceException.class)
	public void updateMessagingJobState(final Long jobId, final JobState jobState) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory.getCurrentSession().createQuery(
					"update MessagingJobImpl set jobState=:jobState,runningUpdateTime=:now where id=:jobId");

			query.setParameter("jobId", jobId);
			query.setParameter("jobState", jobState);
			query.setParameter("now", Calendar.getInstance().getTime());

			query.executeUpdate();
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem updating jobState", ex);
		}

	}
}
