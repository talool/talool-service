package com.talool.messaging.job;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.search.Search;
import com.talool.core.AcquireStatus;
import com.talool.core.Customer;
import com.talool.core.FactoryManager;
import com.talool.core.SearchOptions;
import com.talool.core.gift.EmailGift;
import com.talool.core.service.ServiceException;
import com.talool.domain.DealAcquireImpl;
import com.talool.domain.job.MessagingJobImpl;
import com.talool.messaging.MessagingFactory;
import com.talool.persistence.QueryHelper;
import com.talool.persistence.QueryHelper.QueryType;
import com.talool.service.AbstractHibernateService;
import com.talool.service.MessagingService;
import com.talool.service.ServiceFactory;
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
	private static final String MERCHANT_GIFT_EMAIL_CATEGORY = "MerchantGiftJob";

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
		MessagingJob job = null;
		try
		{
			job = daoDispatcher.find(MessagingJobImpl.class, jobId);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getMessagingJob  " + jobId, ex);
		}

		return job;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MessagingJob> getMessagingJobsByMerchantAccount(final Long createdByMerchantAccountId) throws ServiceException
	{
		final Search search = new Search(MessagingJobImpl.class);
		try
		{
			search.addFilterEqual("createdByMerchantAccount.id", createdByMerchantAccountId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			String msg = "Problem getMessagingJobByCreatedMerchantAccount createdMerchantAccountId: " + createdByMerchantAccountId;
			LOG.error(msg, ex);
			throw new ServiceException(msg, ex);
		}
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
	@Transactional(propagation = Propagation.NESTED)
	public void processMerchantGifts(final MerchantGiftJob job, final List<RecipientStatus> recipientStatuses) throws ServiceException
	{
		final Map<UUID, RecipientStatus> dealMap = new HashMap<UUID, RecipientStatus>();

		// step #1 - batch generating deal acquires
		for (RecipientStatus recipient : recipientStatuses)
		{
			final DealAcquireImpl dac = new DealAcquireImpl();
			dac.setDeal(job.getDeal());
			dac.setAcquireStatus(AcquireStatus.PURCHASED);
			dac.setCustomer(job.getFromCustomer());

			ServiceFactory.get().getTaloolService().save(dac);

			UUID dealAcquireId = dac.getId();
			dealMap.put(dealAcquireId, recipient);

			// it is safer to remove the recipient here rather than wait for the gift to be created.
			// a rare worst case is that on error below, a recipient will never get the email, but we are guarnateed they will
			// not get a dup deal
			daoDispatcher.remove(recipient);
		}

		// step #1 - its ok to ensure the merchants Merchant DealAcquires are saved first
		getCurrentSession().flush();
		getCurrentSession().clear();

		// the category set on the email message for Sendgrid
		final String emailCategory = MERCHANT_GIFT_EMAIL_CATEGORY + "-" + job.getId();
		// step #2 - lets generate gifts to the customers now based on the acquires persisted above
		for (Entry<UUID, RecipientStatus> entry : dealMap.entrySet())
		{
			// if a ServiceException is thrown creating the gift, an email will not be sent
			// however, created dealAcquires above will still be persisted (not rolled back)
			final EmailGift emailGift = FactoryManager.get().getDomainFactory().newEmailGift();
			emailGift.setReceipientName(entry.getValue().getCustomer().getFullName());
			emailGift.setToEmail(entry.getValue().getCustomer().getEmail().toLowerCase());
			ServiceFactory.get().getCustomerService()
					.giftToEmail(job.getId(), job.getFromCustomer().getId(), entry.getKey(), emailGift, emailCategory);
		}

		try
		{
			final Query updateSends = sessionFactory.getCurrentSession().createQuery(
					"update MerchantGiftJobImpl set sends=sends + :sends where id=:jobId");
			updateSends.setParameter("sends", recipientStatuses.size());
			updateSends.setParameter("jobId", job.getId());
			updateSends.executeUpdate();
		}
		catch (Exception ex)
		{
			throw new ServiceException("problem updating sends", ex);
		}

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

	@Override
	public void incrementGiftOpens(final Long jobId, final int totalOpens) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory.getCurrentSession().createQuery(
					"update MerchantGiftJobImpl set giftOpens=giftOpens + :totalOpens where id=:jobId");

			query.setParameter("jobId", jobId);
			query.setParameter("totalOpens", totalOpens);
			query.executeUpdate();
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem updating jobState", ex);
		}
	}

}
