package com.talool.core.service;

import java.util.List;
import java.util.UUID;

import com.talool.core.SearchOptions;
import com.talool.core.activity.Activity;

/**
 * Activity Service
 * 
 * @author clintz
 * 
 */
public interface ActivityService
{
	public void save(final Activity activity) throws ServiceException;

	public void save(final List<Activity> activities) throws ServiceException;

	public List<Activity> getActivities(final UUID customerId, final SearchOptions searchOpts) throws ServiceException;
}
