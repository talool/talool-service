package com.talool.service;

import java.util.UUID;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;

import com.talool.api.thrift.ActivityEvent_t;
import com.talool.api.thrift.ActivityLink_t;
import com.talool.api.thrift.Activity_t;
import com.talool.api.thrift.LinkType;
import com.talool.core.DealAcquire;
import com.talool.core.DealOffer;
import com.talool.core.DomainFactory;
import com.talool.core.FactoryManager;
import com.talool.core.activity.Activity;
import com.talool.core.activity.ActivityEvent;
import com.talool.core.gift.Gift;
import com.talool.thrift.ThriftUtil;

/**
 * 
 * @author clintz
 * 
 */
public final class ActivityFactory
{
	private static final transient DomainFactory domainFactory = FactoryManager.get().getDomainFactory();

	public static final Factory PROTOCOL_FACTORY = new TBinaryProtocol.Factory();

	public static Activity createPurchase(final DealOffer dealOffer, final UUID customerUuid) throws TException
	{
		final StringBuilder sb = new StringBuilder();
		final Activity_t tActivity = new Activity_t();
		final Activity activity = domainFactory.newActivity(ActivityEvent.PURCHASE, customerUuid);

		tActivity.setActivityDate(System.currentTimeMillis());
		tActivity.setActivityEvent(ActivityEvent_t.PURCHASE);
		sb.append("Purchased ").append(" \"").append(dealOffer.getTitle()).append("\"");
		tActivity.setTitle(sb.toString());

		final ActivityLink_t link = new ActivityLink_t(LinkType.DEAL_OFFER, dealOffer.getId().toString());
		tActivity.setActivityLink(link);

		activity.setActivityData(com.talool.thrift.ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

		return activity;
	}

	public static Activity createFriendGiftReedem(final DealAcquire dealAcquire) throws TException
	{
		final StringBuilder sb = new StringBuilder();
		final Activity_t tActivity = new Activity_t();
		final Activity activity = domainFactory.newActivity(ActivityEvent.FRIEND_GIFT_REDEEM, dealAcquire.getGift()
				.getFromCustomer()
				.getId());

		tActivity.setActivityDate(System.currentTimeMillis());
		tActivity.setActivityEvent(ActivityEvent_t.FRIEND_GIFT_REDEEM);

		sb.append("Your friend ").append(dealAcquire.getCustomer().getFullName()).append(" redeemed a deal");
		tActivity.setTitle(sb.toString());

		sb.setLength(0);
		sb.append(dealAcquire.getDeal().getTitle()).append(" at ").append(dealAcquire.getDeal().getMerchant().getName());
		tActivity.setSubtitle(sb.toString());

		ActivityLink_t link = new ActivityLink_t(LinkType.DEAL, dealAcquire.getDeal().getId().toString());
		tActivity.setActivityLink(link);

		activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

		return activity;

	}

	public static Activity createFriendRejectGift(final Gift gift) throws TException
	{
		final StringBuilder sb = new StringBuilder();
		final Activity_t tActivity = new Activity_t();
		final Activity activity = domainFactory.newActivity(ActivityEvent.FRIEND_GIFT_REJECT, gift.getFromCustomer().getId());

		tActivity.setActivityDate(System.currentTimeMillis());
		tActivity.setActivityEvent(ActivityEvent_t.FRIEND_GIFT_REJECT);

		sb.append("Your friend ").append(gift.getReceipientName()).append(" rejected a deal");
		tActivity.setTitle(sb.toString());
		sb.setLength(0);

		sb.append(gift.getDealAcquire().getDeal().getTitle()).append("\" at ").
				append(gift.getDealAcquire().getDeal().getMerchant().getName());

		tActivity.setSubtitle(sb.toString());

		activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

		return activity;

	}

	public static Activity createReject(final Gift gift, UUID customerUuid) throws TException
	{
		final StringBuilder sb = new StringBuilder();
		final Activity_t tActivity = new Activity_t();
		final Activity activity = domainFactory.newActivity(ActivityEvent.REJECT, customerUuid);

		tActivity.setActivityDate(System.currentTimeMillis());
		tActivity.setActivityEvent(ActivityEvent_t.REJECT_GIFT);

		sb.append("Rejected \"").append(gift.getDealAcquire().getDeal().getTitle()).append("\" at ").
				append(gift.getDealAcquire().getDeal().getMerchant().getName());

		tActivity.setTitle(sb.toString());

		sb.setLength(0);

		sb.append("Gift from ").append(gift.getFromCustomer().getFullName());
		tActivity.setSubtitle(sb.toString());

		activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

		return activity;

	}

	public static Activity createEmailSendGift(final Gift gift) throws TException
	{
		final Activity activity = domainFactory.newActivity(ActivityEvent.FACEBOOK_SEND_GIFT, gift.getFromCustomer().getId());
		final StringBuilder sb = new StringBuilder();
		final Activity_t tActivity = new Activity_t();

		tActivity.setActivityDate(System.currentTimeMillis());
		tActivity.setActivityEvent(ActivityEvent_t.FACEBOOK_SEND_GIFT);

		sb.append("Sent email \"").append(gift.getDealAcquire().getDeal().getTitle()).append("\" at ").
				append(gift.getDealAcquire().getDeal().getMerchant().getName());

		tActivity.setTitle(sb.toString());
		sb.setLength(0);

		sb.append("To ").append(gift.getReceipientName());
		tActivity.setSubtitle(sb.toString());

		activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

		return activity;

	}

	public static Activity createFacebookSendGift(final Gift gift) throws TException
	{
		final Activity activity = domainFactory.newActivity(ActivityEvent.FACEBOOK_SEND_GIFT, gift.getFromCustomer().getId());
		final StringBuilder sb = new StringBuilder();
		final Activity_t tActivity = new Activity_t();

		tActivity.setActivityDate(System.currentTimeMillis());
		tActivity.setActivityEvent(ActivityEvent_t.FACEBOOK_SEND_GIFT);

		sb.append("Sent gift \"").append(gift.getDealAcquire().getDeal().getTitle()).append("\" at ").
				append(gift.getDealAcquire().getDeal().getMerchant().getName());

		tActivity.setTitle(sb.toString());
		sb.setLength(0);

		sb.append("To ").append(gift.getReceipientName());
		tActivity.setSubtitle(sb.toString());

		activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

		return activity;

	}

	public static Activity createFacebookRecvGift(final Gift gift) throws TException
	{
		final Activity activity = domainFactory.newActivity(ActivityEvent.FACEBOOK_RECV_GIFT, gift.getToCustomer().getId());
		final StringBuilder sb = new StringBuilder();
		final Activity_t tActivity = new Activity_t();

		tActivity.setActivityDate(System.currentTimeMillis());
		tActivity.setActivityEvent(ActivityEvent_t.FACEBOOK_RECV_GIFT);

		sb.append("Received gift \"").append(gift.getDealAcquire().getDeal().getTitle()).append("\" at ").
				append(gift.getDealAcquire().getDeal().getMerchant().getName());

		tActivity.setTitle(sb.toString());
		sb.setLength(0);

		sb.append("From ").append(gift.getFromCustomer().getFullName());
		tActivity.setSubtitle(sb.toString());

		activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

		return activity;

	}

	public static Activity createEmailRecvGift(final Gift gift) throws TException
	{
		final Activity activity = domainFactory.newActivity(ActivityEvent.FACEBOOK_RECV_GIFT, gift.getToCustomer().getId());
		final StringBuilder sb = new StringBuilder();
		final Activity_t tActivity = new Activity_t();

		tActivity.setActivityDate(System.currentTimeMillis());
		tActivity.setActivityEvent(ActivityEvent_t.FACEBOOK_RECV_GIFT);

		sb.append("Received email \"").append(gift.getDealAcquire().getDeal().getTitle()).append("\" at ").
				append(gift.getDealAcquire().getDeal().getMerchant().getName());

		tActivity.setTitle(sb.toString());
		sb.setLength(0);

		sb.append("From ").append(gift.getFromCustomer().getFullName());
		tActivity.setSubtitle(sb.toString());

		final ActivityLink_t link = new ActivityLink_t(LinkType.GIFT, gift.getId().toString());
		tActivity.setActivityLink(link);

		activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

		return activity;

	}

	public static Activity createRedeem(final DealAcquire dealAcquire, final UUID customerUuid) throws TException
	{
		final Activity activity = domainFactory.newActivity(ActivityEvent.REDEEM, customerUuid);
		final StringBuilder sb = new StringBuilder();
		final Activity_t tActivity = new Activity_t();

		tActivity.setActivityDate(System.currentTimeMillis());
		tActivity.setActivityEvent(ActivityEvent_t.REDEEM);

		sb.append("Redeemed \"").append(dealAcquire.getDeal().getTitle()).append("\" at ").
				append(dealAcquire.getDeal().getMerchant().getName());

		tActivity.setTitle(sb.toString());

		sb.setLength(0);
		if (dealAcquire.getGiftId() != null)
		{
			sb.append("Gift from ").append(dealAcquire.getGift().getFromCustomer().getFullName());
			tActivity.setSubtitle(sb.toString());
		}

		final Gift gift = dealAcquire.getGift();
		if (gift != null)
		{
			tActivity.setSubtitle("Gift from  " + gift.getFromCustomer().getFullName());
		}

		ActivityLink_t link = new ActivityLink_t(LinkType.DEAL, dealAcquire.getDeal().getId().toString());
		tActivity.setActivityLink(link);

		activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

		return activity;

	}
}
