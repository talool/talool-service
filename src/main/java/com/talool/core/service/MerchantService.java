package com.talool.core.service;

import java.util.List;
import java.util.Set;

import com.talool.core.Deal;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
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

	public MerchantAccount authenticateMerchantAccount(final Long merchantId, final String email,
			final String password) throws ServiceException;

	public void deleteMerchant(final Long id) throws ServiceException;

	public Merchant getMerchantById(final Long id) throws ServiceException;

	public List<Merchant> getMerchantByName(final String name) throws ServiceException;

	public void save(final Merchant merchant) throws ServiceException;

	public List<Deal> getMerchantDeals(final Long merchantId, final Boolean isActive)
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
	public List<Deal> getDealsByMerchantId(final Long merchantId) throws ServiceException;

	public List<Deal> getDealsByCustomerId(final Long accountId) throws ServiceException;

	// DealOffer stuff
	public void save(final DealOffer dealOffer) throws ServiceException;

	public DealOffer getDealOffer(final Long dealOfferId) throws ServiceException;
	
	public List<DealOffer> getDealOffers() throws ServiceException;

	public void save(final Deal deal) throws ServiceException;

	public Deal getDeal(final Long dealId) throws ServiceException;

	public List<Deal> getDealsByDealOfferId(final Long dealOfferId) throws ServiceException;

	public List<DealOfferPurchase> getDealOfferPurchasesByCustomerId(final Long customerId)
			throws ServiceException;

	public List<DealOfferPurchase> getDealOfferPurchasesByDealOfferId(final Long dealOfferId)
			throws ServiceException;

	public void save(final DealOfferPurchase dealOfferPurchase) throws ServiceException;

	public Set<Tag> getDealOfferTags(final Long dealOfferId) throws ServiceException;

}
