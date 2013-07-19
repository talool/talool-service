package com.talool.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;
import com.talool.core.SearchOptions;
import com.talool.core.activity.Activity;
import com.talool.core.service.ActivityService;
import com.talool.core.service.ServiceException;
import com.talool.domain.activity.ActivityImpl;
import com.talool.persistence.QueryHelper;

/**
 * Hibernate implementation of an ActivityService
 * 
 * @author clintz
 * 
 */
@Transactional(readOnly = true)
@Service
@Repository
public class ActivityServiceImpl extends AbstractHibernateService implements ActivityService
{
	private static final Logger LOG = LoggerFactory.getLogger(ActivityServiceImpl.class);

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void save(final Activity activity) throws ServiceException
	{
		try
		{
			daoDispatcher.save(activity);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format(
					"Problem saving activity for customerId " + activity.getCustomerId(), ex));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Activity> getActivities(final UUID customerId, final SearchOptions searchOpts) throws ServiceException
	{
		try
		{
			final Search search = new Search(ActivityImpl.class);
			search.addFilterEqual("customerId", customerId);

			QueryHelper.applySearchOptions(searchOpts, search);
			search.addSort(Sort.asc("activityEvent"));

			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			String msg = String.format("Problem getActivities for customerId %s", customerId);
			LOG.error(msg, ex);
			throw new ServiceException(msg, ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void save(final List<Activity> activities) throws ServiceException
	{
		for (final Activity act : activities)
		{
			try
			{
				save(act);
			}
			catch (ServiceException se)
			{
				LOG.error(se.getLocalizedMessage(), se);
			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void activityAction(final UUID actionId) throws ServiceException
	{
		try
		{
			final Search search = new Search(ActivityImpl.class);
			search.addFilterEqual("id", actionId);
			Activity activity = (Activity) daoDispatcher.searchUnique(search);
			ActivityFactory.setActionTaken(activity, true);
			save(activity);
		}
		catch (Exception ex)
		{
			String msg = String.format("Problem activityAction for actionId %s", actionId);
			LOG.error(msg, ex);
			throw new ServiceException(msg, ex);
		}
	}

}
