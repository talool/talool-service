package com.talool.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;

import com.talool.api.thrift.ActivityEvent_t;
import com.talool.api.thrift.ActivityLink_t;
import com.talool.api.thrift.Activity_t;
import com.talool.api.thrift.LinkType;
import com.talool.api.thrift.PropertyConstants;
import com.talool.bundles.ActivityBundle;
import com.talool.bundles.BundleType;
import com.talool.bundles.BundleUtil;
import com.talool.core.DealAcquire;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.DomainFactory;
import com.talool.core.FactoryManager;
import com.talool.core.Merchant;
import com.talool.core.activity.Activity;
import com.talool.core.activity.ActivityEvent;
import com.talool.core.gift.EmailGift;
import com.talool.core.gift.Gift;
import com.talool.domain.gift.EmailGiftImpl;
import com.talool.domain.gift.FacebookGiftImpl;
import com.talool.thrift.ThriftUtil;

/**
 * 
 * @author clintz
 * 
 */
public final class ActivityFactory {
  private static final transient DomainFactory domainFactory = FactoryManager.get().getDomainFactory();

  public static final Factory PROTOCOL_FACTORY = new TBinaryProtocol.Factory();

  public static Activity createPurchase(final DealOffer dealOffer, final UUID customerUuid) throws TException {
    String title = null;
    final Activity activity = domainFactory.newActivity(ActivityEvent.PURCHASE, customerUuid);
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.PURCHASE);

    if (dealOffer.isFree()) {
      title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.ACQUIRED_FREE_DEAL_OFFER_TITLE, dealOffer.getTitle());
    } else {
      title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.PURCHASED_DEAL_OFFER_TITLE, dealOffer.getTitle());
    }

    tActivity.setTitle(title);

    final ActivityLink_t link = new ActivityLink_t(LinkType.DEAL_OFFER, dealOffer.getId().toString());
    tActivity.setActivityLink(link);

    activity.setActivityData(com.talool.thrift.ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;
  }

  public static Activity createFundraiserSupport(final DealOfferPurchase dop, final Merchant fundraiser) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.FUNDRAISER_SUPPORT, dop.getCustomer().getId());
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.FUNDRAISER_SUPPORT);

    String title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.FUNDRAISER_SUPPORT_TITLE, fundraiser.getName());

    tActivity.setTitle(title);

    title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.FUNDRAISER_SUPPORT_SUBTITLE);

    tActivity.setSubtitle(title);

    final ActivityLink_t link = new ActivityLink_t(LinkType.EMAIL, dop.getId().toString());

    final Map<String, String> props = new HashMap<String, String>();
    props.put(PropertyConstants.TEMPLATE_ID, ActivityEmailTemplateType.BasicFundRaiser.getTemplateIdAsString());
    link.setProperties(props);

    tActivity.setActivityLink(link);

    activity.setActivityData(com.talool.thrift.ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;
  }

  public static Activity createActivatedByCode(final DealOffer dealOffer, final UUID customerUuid, final String code) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.PURCHASE, customerUuid);
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.PURCHASE);

    final String title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.ACTIVATED_DEAL_OFFER_TITLE, dealOffer.getTitle(), code.toUpperCase());

    tActivity.setTitle(title);

    final ActivityLink_t link = new ActivityLink_t(LinkType.DEAL_OFFER, dealOffer.getId().toString());
    tActivity.setActivityLink(link);

    activity.setActivityData(com.talool.thrift.ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;
  }

  public static Activity createFriendGiftReedem(final DealAcquire dealAcquire) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.FRIEND_GIFT_REDEEM, dealAcquire.getGift().getFromCustomer().getId());
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.FRIEND_GIFT_REDEEM);

    String title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.FRIEND_REDEEMED_DEAL_TITLE, dealAcquire.getCustomer().getFullName());

    tActivity.setTitle(title);

    title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.FRIEND_REDEEMED_DEAL_SUBTITLE, dealAcquire.getDeal().getTitle(),
            dealAcquire.getDeal().getMerchant().getName());

    tActivity.setSubtitle(title);

    ActivityLink_t link = new ActivityLink_t(LinkType.DEAL, dealAcquire.getDeal().getId().toString());
    tActivity.setActivityLink(link);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;

  }

  public static Activity createFriendRejectGift(final Gift gift) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.FRIEND_GIFT_REJECT, gift.getFromCustomer().getId());
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.FRIEND_GIFT_REJECT);

    String title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.FRIEND_REJECTED_DEAL_TITLE, gift.getReceipientName());

    tActivity.setTitle(title);

    title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.FRIEND_REJECTED_DEAL_SUBTITLE, gift.getDealAcquire().getDeal()
            .getTitle(), gift.getDealAcquire().getDeal().getMerchant().getName());

    tActivity.setSubtitle(title);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;

  }

  public static Activity createGiftReturnNotExistentEmail(final Gift gift) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.FRIEND_GIFT_REJECT, gift.getFromCustomer().getId());
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.FRIEND_GIFT_REJECT);

    String title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.GIFT_RETURNED_EMAIL_DOES_NOT_EXIST_TITLE, gift.getDealAcquire()
            .getDeal().getTitle());

    tActivity.setTitle(title);

    title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.GIFT_RETURNED_EMAIL_DOES_NOT_EXIST_SUBTITLE,
            ((EmailGift) gift).getToEmail());

    tActivity.setSubtitle(title);

    final ActivityLink_t link = new ActivityLink_t(LinkType.EXTERNAL, ServiceConfig.get().getGiftReturnedLink());
    tActivity.setActivityLink(link);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;

  }

  public static Activity createFriendAcceptGift(final Gift gift) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.FRIEND_GIFT_ACCEPT, gift.getFromCustomer().getId());
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.FRIEND_GIFT_ACCEPT);

    String title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.FRIEND_ACCEPTED_DEAL_TITLE, gift.getReceipientName());

    tActivity.setTitle(title);

    title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.FRIEND_ACCEPTED_DEAL_SUBTITLE, gift.getDealAcquire().getDeal()
            .getTitle(), gift.getDealAcquire().getDeal().getMerchant().getName());

    tActivity.setSubtitle(title);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;

  }

  public static Activity createReject(final Gift gift, UUID customerUuid) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.REJECT_GIFT, customerUuid);
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.REJECT_GIFT);

    String title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.REJECTED_GIFT_TITLE, gift.getDealAcquire().getDeal().getTitle(), gift
            .getDealAcquire().getDeal().getMerchant().getName());

    tActivity.setTitle(title);

    title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.REJECTED_GIFT_SUBTITLE, gift.getFromCustomer().getFullName());

    tActivity.setSubtitle(title);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;

  }

  public static Activity createWelcome(final UUID customerUuid, final int giftCount, final Merchant whiteLabelMerchant) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.WELCOME, customerUuid);
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.WELCOME);
    String title = null;
    ActivityLink_t link = null;

    if (whiteLabelMerchant == null) {
      title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.WELCOME_TITLE, "Talool");
      link = new ActivityLink_t(LinkType.EXTERNAL, ServiceConfig.get().getConsumersLink());

    } else {
      title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.WELCOME_TITLE, whiteLabelMerchant.getName());
      link = new ActivityLink_t(LinkType.EXTERNAL, ServiceConfig.get().getConsumersLink() + "?wlid=" + whiteLabelMerchant.getId());
      tActivity.setActivityLink(link);
    }

    tActivity.setTitle(title);

    title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.WELCOME_SUBTITLE);
    tActivity.setSubtitle(title);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));
    return activity;
  }

  /**
   * Determines what gift type it is and creates the proper Activity object
   * 
   * @param gift
   * @return
   * @throws TException
   */
  public static Activity createRecvGift(final Gift gift) throws TException {
    Activity act = null;

    if (gift instanceof FacebookGiftImpl) {
      act = createFacebookRecvGift(gift);
    }
    if (gift instanceof EmailGiftImpl) {
      act = createEmailRecvGift(gift);
    }
    return act;
  }

  public static Activity createEmailSendGift(final Gift gift) throws TException {
    final EmailGift emailGift = (EmailGift) gift;
    final Activity activity = domainFactory.newActivity(ActivityEvent.EMAIL_SEND_GIFT, emailGift.getFromCustomer().getId());
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.EMAIL_SEND_GIFT);

    String title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.SENT_EMAIL_GIFT_TITLE, emailGift.getDealAcquire().getDeal().getTitle(),
            emailGift.getDealAcquire().getDeal().getMerchant().getName());

    tActivity.setTitle(title);

    title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.SENT_EMAIL_GIFT_SUBTITLE, emailGift.getReceipientName(),
            emailGift.getToEmail());

    tActivity.setSubtitle(title);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;

  }

  public static Activity createFacebookSendGift(final Gift gift) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.FACEBOOK_SEND_GIFT, gift.getFromCustomer().getId());
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.FACEBOOK_SEND_GIFT);

    String title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.SENT_FACEBOOK_GIFT_TITLE, gift.getDealAcquire().getDeal().getTitle(),
            gift.getDealAcquire().getDeal().getMerchant().getName());

    tActivity.setTitle(title);

    title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.SENT_FACEBOOK_GIFT_SUBTITLE, gift.getReceipientName());

    tActivity.setSubtitle(title);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;

  }

  static Activity_t createBaseActivity_t(final ActivityEvent_t activityEvent) {
    final Activity_t tActivity = new Activity_t();
    tActivity.setActivityDate(System.currentTimeMillis());
    tActivity.setActivityEvent(activityEvent);
    return tActivity;
  }

  public static Activity createFacebookRecvGift(final Gift gift) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.FACEBOOK_RECV_GIFT, gift.getToCustomer().getId());
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.FACEBOOK_RECV_GIFT);

    String title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.RECV_FACEBOOK_GIFT_TITLE, gift.getDealAcquire().getDeal().getTitle(),
            gift.getDealAcquire().getDeal().getMerchant().getName());

    tActivity.setTitle(title);

    title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.RECV_FACEBOOK_GIFT_SUBTITLE, gift.getFromCustomer().getFullName());

    tActivity.setSubtitle(title);

    final ActivityLink_t link = new ActivityLink_t(LinkType.GIFT, gift.getId().toString());
    tActivity.setActivityLink(link);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;

  }

  /**
   * Sets the closeState to true on the serilized activity object
   * 
   * @param activity
   * @return
   * @throws TException
   */
  public static void setActionTaken(final Activity activity, final boolean actionTaken) throws TException {
    final Activity_t tActivity = new Activity_t();

    ThriftUtil.deserialize(activity.getActivityData(), tActivity, PROTOCOL_FACTORY);

    tActivity.setActionTaken(actionTaken);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

  }

  public static Activity createEmailRecvGift(final Gift gift) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.EMAIL_RECV_GIFT, gift.getToCustomer().getId());
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.EMAIL_RECV_GIFT);

    String title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.RECV_EMAIL_GIFT_TITLE, gift.getDealAcquire().getDeal().getTitle(), gift
            .getDealAcquire().getDeal().getMerchant().getName());

    tActivity.setTitle(title);

    title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.RECV_EMAIL_GIFT_SUBTITLE, gift.getFromCustomer().getFullName());

    tActivity.setSubtitle(title);

    final ActivityLink_t link = new ActivityLink_t(LinkType.GIFT, gift.getId().toString());
    tActivity.setActivityLink(link);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;

  }

  public static Activity createFriendAccept(final Gift gift) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.FRIEND_GIFT_ACCEPT, gift.getFromCustomer().getId());
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.FRIEND_GIFT_ACCEPT);

    String title = BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.FRIEND_ACCEPTED_DEAL_TITLE, gift.getReceipientName());
    tActivity.setTitle(title);

    title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.FRIEND_ACCEPTED_DEAL_SUBTITLE, gift.getDealAcquire().getDeal()
            .getTitle(), gift.getDealAcquire().getDeal().getMerchant().getName());

    tActivity.setSubtitle(title);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;

  }

  public static Activity createRedeem(final DealAcquire dealAcquire, final UUID customerUuid) throws TException {
    final Activity activity = domainFactory.newActivity(ActivityEvent.REDEEM, customerUuid);
    final Activity_t tActivity = createBaseActivity_t(ActivityEvent_t.REDEEM);

    String title =
        BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.REDEEMED_DEAL_TITLE, dealAcquire.getDeal().getTitle(), dealAcquire
            .getDeal().getMerchant().getName());

    tActivity.setTitle(title);

    if (dealAcquire.getGiftId() != null) {
      title =
          BundleUtil.render(BundleType.ACTIVITY, Locale.ENGLISH, ActivityBundle.REDEEMED_DEAL_SUBTITLE, dealAcquire.getGift().getFromCustomer()
              .getFullName());

      tActivity.setSubtitle(title);
    }

    final ActivityLink_t link = new ActivityLink_t(LinkType.DEAL, dealAcquire.getDeal().getId().toString());
    tActivity.setActivityLink(link);

    activity.setActivityData(ThriftUtil.serialize(tActivity, PROTOCOL_FACTORY));

    return activity;

  }
}
