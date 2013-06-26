package com.talool.service;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.talool.core.DomainFactory;
import com.talool.core.SearchOptions;
import com.talool.core.activity.Activity;
import com.talool.core.activity.ActivityEvent;
import com.talool.core.service.ActivityService;
import com.talool.core.service.ServiceException;
import com.talool.domain.activity.ActivityImpl;

@TestExecutionListeners(TransactionalTestExecutionListener.class)
// Rolls back transactions by default
public class ActivityServiceTest extends HibernateFunctionalTestBase
{
	private static final Logger LOG = LoggerFactory.getLogger(ActivityServiceTest.class);

	private DomainFactory domainFactory;

	@Autowired
	protected ActivityService activityService;

	@Test
	public void testEntrance() throws ServiceException
	{
		testActivityCreate();
	}

	public void testActivityCreate() throws ServiceException
	{
		UUID customerId = UUID.randomUUID();

		Activity activity = new ActivityImpl();
		activity.setActivityType(ActivityEvent.REDEEM);
		activity.setCustomerId(customerId);
		activity.setActivityData("abc1".getBytes());;

		activityService.save(activity);

		activity = new ActivityImpl();
		activity.setActivityType(ActivityEvent.FACEBOOK_RECV_GIFT);
		activity.setCustomerId(customerId);
		activity.setActivityData("abc2".getBytes());;

		activityService.save(activity);

		activity = new ActivityImpl();
		activity.setActivityType(ActivityEvent.FACEBOOK_SEND_GIFT);
		activity.setCustomerId(customerId);
		activity.setActivityData("abc3".getBytes());;

		activityService.save(activity);

		SearchOptions searchOpts = new SearchOptions.Builder().maxResults(100).page(0).ascending(false)
				.sortProperty("activityType").build();

		List<Activity> activities = activityService.getActivities(activity.getCustomerId(), searchOpts);

		Assert.assertEquals(3, activities.size());

		Assert.assertEquals(ActivityEvent.FACEBOOK_SEND_GIFT, activities.get(0).getActivityEvent());
		Assert.assertEquals(ActivityEvent.FACEBOOK_RECV_GIFT, activities.get(1).getActivityEvent());
		Assert.assertEquals(ActivityEvent.REDEEM, activities.get(2).getActivityEvent());

	}
}
