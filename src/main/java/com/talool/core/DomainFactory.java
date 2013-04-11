package com.talool.core;

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

	public MerchantLocation newMerchantLocation();

	public MerchantManagedLocation newMerchantManagedLocation(Merchant merchant);
	
	public MerchantAccount newMerchantAccount(Merchant merchant);

	public Customer newCustomer();

	public Tag newTag(String tag);

	public DealOfferPurchase newDealOfferPurchase(final DealOffer dealOffer, final Customer customer);

	public Location newLocation(final Double longitude, final Double latitude);
}
