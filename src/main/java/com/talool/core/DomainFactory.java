package com.talool.core;

import java.util.UUID;

import com.talool.core.activity.Activity;
import com.talool.core.activity.ActivityEvent;
import com.talool.core.gift.EmailGift;
import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.MerchantSocialAccount;

/**
 * 
 * @author clintz
 * 
 */
public interface DomainFactory
{
	public CustomerSocialAccount newCustomerSocialAccount(final String socialNetworkName);

	public MerchantSocialAccount newMerchantSocialAccount(final String socialNetworkName);

	public Merchant newMerchant();

	public Merchant newMerchant(boolean topLevelOnly);

	public DealOffer newDealOffer(final Merchant merchant, final MerchantAccount createdByMerchant);

	public DealOfferPurchase newDealOfferPurchase(final Customer customer, final DealOffer dealOffer);

	public Deal newDeal(final DealOffer dealOffer);

	public Deal newDeal(final UUID merchantId, final MerchantAccount createdByMerchantAccount, final boolean setDefaults);

	public MerchantLocation newMerchantLocation();

	public MerchantAccount newMerchantAccount(Merchant merchant);

	public Customer newCustomer();

	public Activity newActivity(final ActivityEvent activityType, final UUID customerId);

	public Tag newTag(String tag);

	public DealOfferPurchase newDealOfferPurchase(final DealOffer dealOffer, final Customer customer);

	public Location newLocation(final Double longitude, final Double latitude);

	public Relationship newRelationship(final Customer fromCustomer, final Customer toCustomer, final RelationshipStatus status);

	public MerchantIdentity newMerchantIdentity(final UUID id, final String name);

	public MerchantMedia newMedia(final UUID merchantId, final String mediaUrl, final MediaType mediaType);

	public EmailGift newEmailGift();
}
