package com.talool.core.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.talool.core.AcquireStatus;
import com.talool.core.AcquireStatusType;
import com.talool.core.Deal;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.DistanceEntity;
import com.talool.core.Location;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantIdentity;
import com.talool.core.MerchantManagedLocation;
import com.talool.core.Tag;

/**
 * r Merchant Service
 * 
 * @author clintz
 * 
 */
public interface MerchantService
{
	public void save(final MerchantAccount merchantAccount) throws ServiceException;

	/**
	 * For now it will throw an error if there are more than 1 MerchantAccounts
	 * for the given email . In order to support multiple accounts, do a separate
	 * call to get the MerchantAccounts for the email
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws ServiceException
	 */
	public MerchantAccount authenticateMerchantAccount(final String email, final String password)
			throws ServiceException;

	public MerchantAccount authenticateMerchantAccount(final UUID merchantId, final String email,
			final String password) throws ServiceException;

	public void deleteMerchant(final String id) throws ServiceException;

	public Merchant getMerchantById(final UUID id) throws ServiceException;

	public List<Merchant> getMerchantByName(final String name) throws ServiceException;

	public void save(final Merchant merchant) throws ServiceException;

	public List<Deal> getMerchantDeals(final UUID merchantId, final Boolean isActive)
			throws ServiceException;

	/**
	 * Gets Merchants associated with a customer (via paid deal books or free)
	 * 
	 * @param customerId
	 * @return
	 * @throws ServiceException
	 */
	public List<Merchant> getMerchantsByCustomerId(final Long customerId) throws ServiceException;

	public List<Merchant> getMerchants() throws ServiceException;

	/**
	 * Gets deals by merchantId
	 * 
	 * @param merchantId
	 * @return
	 * @throws ServiceException
	 */
	public List<Deal> getDealsByMerchantId(final UUID merchantId) throws ServiceException;

	public List<Deal> getDealsByCustomerId(final UUID accountId) throws ServiceException;

	// DealOffer stuff
	public void save(final DealOffer dealOffer) throws ServiceException;

	public DealOffer getDealOffer(final UUID dealOfferId) throws ServiceException;

	public List<DealOffer> getDealOffers() throws ServiceException;

	public List<DealOffer> getDealOffersByMerchantId(final UUID merchantId) throws ServiceException;

	public void save(final Deal deal) throws ServiceException;

	public Deal getDeal(final UUID dealId) throws ServiceException;

	public List<Deal> getDealsByDealOfferId(final UUID dealOfferId) throws ServiceException;

	public List<DealOfferPurchase> getDealOfferPurchasesByCustomerId(final UUID customerId)
			throws ServiceException;

	public List<DealOfferPurchase> getDealOfferPurchasesByDealOfferId(final UUID dealOfferId)
			throws ServiceException;

	public void save(final DealOfferPurchase dealOfferPurchase) throws ServiceException;

	public Set<Tag> getDealOfferTags(final UUID dealOfferId) throws ServiceException;

	public List<MerchantAccount> getAccountsForMerchant(final UUID merchantId)
			throws ServiceException;

	public List<MerchantManagedLocation> getLocationsForMerchant(final UUID merchantId)
			throws ServiceException;

	public MerchantManagedLocation getMerchantLocationById(final Long merchantManagedLocationId)
			throws ServiceException;

	public void save(final MerchantManagedLocation merchantManagedLocation) throws ServiceException;

	public MerchantAccount getMerchantAccountById(final Long merchantAccountId)
			throws ServiceException;

	public List<MerchantIdentity> getAuthorizedMerchantIdentities(final Long merchantAccountId)
			throws ServiceException;

	public List<Deal> getAllRelatedDealsForMerchantId(final UUID merchantId) throws ServiceException;

	public List<DealOffer> getAllRelatedDealsOffersForMerchantId(final UUID merchantId)
			throws ServiceException;

	public AcquireStatus getAcquireStatus(final AcquireStatusType type) throws ServiceException;

	public List<DistanceEntity<Merchant>> getMerchantsWithin(final Location location,
			final int maxMiles) throws ServiceException;

}
