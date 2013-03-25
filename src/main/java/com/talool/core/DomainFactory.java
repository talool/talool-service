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

	public DealBook newDealBook(final Merchant merchant);

	public MerchantDeal newMerchantDeal(final Merchant merchant);

	public DealBookContent newDealBookContent(final MerchantDeal merchantDeal, final DealBook dealBook);

	public Customer newCustomer();

	public DealBookPurchase newDealBookPurchase(final DealBook dealBook, final Customer customer);

	public Location newLocation(final Double longitude, final Double latitude);
}
