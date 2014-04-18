package com.talool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.talool.core.activity.Activity;

/**
 * 
 * @author clintz
 * 
 */
public class PurchaseListener
{
	private static final Logger LOG = LoggerFactory.getLogger(PurchaseListener.class);

	@Subscribe
	public void sendFundRaiserFriendActivity(final PurchaseEvent purchaseEvent)
	{
		if (purchaseEvent == null || purchaseEvent.getFundraiser() == null)
		{
			return;
		}

		try
		{
			Activity activity = ActivityFactory.createFundraiserSupport(purchaseEvent.getDealOfferPurchase(), purchaseEvent.getFundraiser());

			ServiceFactory.get().getActivityService().save(activity);

		}
		catch (Exception e)
		{
			LOG.error(
					String.format("Activity not created for fundRaiserFriendActivity customerId '%s' dealOfferId '%s'", purchaseEvent
							.getDealOfferPurchase().getCustomer().getId(), purchaseEvent.getDealOfferPurchase().getDealOffer().getId()), e);
		}

	}

	/**
	 * Creates a purchase activity for the customer
	 * 
	 * @param purchaseEvent
	 */
	@Subscribe
	public void sendPurchaseActivity(final PurchaseEvent purchaseEvent)
	{
		try
		{
			Activity activity = ActivityFactory.createPurchase(purchaseEvent.getDealOfferPurchase().getDealOffer(), purchaseEvent
					.getDealOfferPurchase().getCustomer().getId());

			ServiceFactory.get().getActivityService().save(activity);

		}
		catch (Exception e)
		{
			LOG.error(
					String.format("Activity not created for purchase customerId '%s' dealOfferId '%s'", purchaseEvent.getDealOfferPurchase()
							.getCustomer().getId(), purchaseEvent.getDealOfferPurchase().getDealOffer().getId()), e);
		}
	}

}
