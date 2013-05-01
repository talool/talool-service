package com.talool.core;

import java.util.UUID;

/**
 * 
 * @author clintz
 * 
 */
public interface DomainFactory
{
	public SocialAccount newSocialAccount(final String socialNetworkName,
			final AccountType accountType);

	public Address newAddress();

	public Merchant newMerchant();

	public DealOffer newDealOffer(final Merchant merchant, final MerchantAccount createdByMerchant);

	public DealOfferPurchase newDealOfferPurchase(final Customer customer, final DealOffer dealOffer);

	public Deal newDeal(final DealOffer dealOffer);

	public Deal newDeal(final MerchantAccount createdByMerchantAccount);

	public MerchantLocation newMerchantLocation();

	public MerchantAccount newMerchantAccount(Merchant merchant);

	public Customer newCustomer();

	public Tag newTag(String tag);

	public DealOfferPurchase newDealOfferPurchase(final DealOffer dealOffer, final Customer customer);

	public Location newLocation(final Double longitude, final Double latitude);

	public Relationship newRelationship(final Customer fromCustomer, final Customer toCustomer,
			final RelationshipStatus status);

	public MerchantIdentity newMerchantIdentity(final UUID id, final String name);
}
