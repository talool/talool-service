package com.talool.core.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.talool.core.Deal;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferGeoSummariesResult;
import com.talool.core.DealOfferPurchase;
import com.talool.core.Location;
import com.talool.core.MediaType;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantIdentity;
import com.talool.core.MerchantLocation;
import com.talool.core.MerchantMedia;
import com.talool.core.SearchOptions;
import com.talool.core.Tag;
import com.talool.domain.PropertyCriteria;
import com.talool.service.HibernateService;
import com.talool.stats.DealOfferMetrics;
import com.talool.stats.DealOfferSummary;
import com.talool.stats.DealSummary;
import com.talool.stats.MerchantSummary;
import com.talool.stats.PaginatedResult;
import com.talool.utils.KeyValue;

/**
 * r Merchant Service
 * 
 * @author clintz
 * 
 */
public interface MerchantService extends HibernateService
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

	@Deprecated
	public List<Merchant> getMerchants() throws ServiceException;

	public List<Merchant> getAllMerchants() throws ServiceException;

	/**
	 * Gets deals by merchantId
	 * 
	 * @param merchantId
	 * @return
	 * @throws ServiceException
	 */
	public List<Deal> getDealsByMerchantId(final UUID merchantId) throws ServiceException;

	// DealOffer stuff
	public void save(final DealOffer dealOffer) throws ServiceException;

	public DealOffer getDealOffer(final UUID dealOfferId) throws ServiceException;

	public List<DealOffer> getDealOffers() throws ServiceException;

	public List<DealOffer> getDealOffersByMerchantId(final UUID merchantId) throws ServiceException;

	public void save(final Deal deal) throws ServiceException;

	public Deal getDeal(final UUID dealId) throws ServiceException;

	/**
	 * Gets all deals by deal offer. If activeDealsOnly=true, expired or inActive
	 * deals will not be returned.
	 * 
	 * @param dealOfferId
	 * @param searchOpts
	 * @param eagerlyLoadedProps
	 * @param activeDealsOnly
	 * @return
	 * @throws ServiceException
	 */
	public List<Deal> getDealsByDealOfferId(final UUID dealOfferId, final SearchOptions searchOpts, boolean activeDealsOnly) throws ServiceException;

	public List<DealOfferPurchase> getDealOfferPurchasesByDealOfferId(final UUID dealOfferId) throws ServiceException;

	public void save(final DealOfferPurchase dealOfferPurchase) throws ServiceException;

	public Set<Tag> getDealOfferTags(final UUID dealOfferId) throws ServiceException;

	public List<MerchantAccount> getAccountsForMerchant(final UUID merchantId) throws ServiceException;

	public List<MerchantLocation> getLocationsForMerchant(final UUID merchantId) throws ServiceException;

	public MerchantLocation getMerchantLocationById(final Long merchantManagedLocationId) throws ServiceException;

	public void save(final MerchantLocation merchantLocation) throws ServiceException;

	public MerchantAccount getMerchantAccountById(final Long merchantAccountId) throws ServiceException;

	public List<MerchantIdentity> getAuthorizedMerchantIdentities(final Long merchantAccountId)
			throws ServiceException;

	public List<Deal> getAllRelatedDealsForMerchantId(final UUID merchantId) throws ServiceException;

	public List<DealOffer> getAllRelatedDealsOffersForMerchantId(final UUID merchantId)
			throws ServiceException;

	public List<Merchant> getMerchantsWithin(final Location location, final int maxMiles, final SearchOptions searchOptions)
			throws ServiceException;

	public List<Merchant> getMerchantsWithin2(final Location location, final int maxMiles, final SearchOptions searchOptions)
			throws ServiceException;

	public List<MerchantMedia> getMerchantMedias(final UUID merchantId, final MediaType[] mediaTypes,
			final SearchOptions searchOptions)
			throws ServiceException;

	public void saveMerchantMedia(final MerchantMedia merchantMedia) throws ServiceException;

	/**
	 * Gets active and non-expired DealOffer summaries within maxMiles of a
	 * location. If the location is null or fields are zero or null then the
	 * fallbackSearchOptions are used.
	 * 
	 * @param location
	 * @param maxMiles
	 * @param searchOptions
	 * @param fallbackSearchOptions
	 * @return
	 * @throws ServiceException
	 */
	public DealOfferGeoSummariesResult getDealOfferGeoSummariesWithin(final Location location, final int maxMiles,
			final SearchOptions searchOptions, final SearchOptions fallbackSearchOptions) throws ServiceException;

	public Map<UUID, DealOfferMetrics> getDealOfferMetrics() throws ServiceException;

	public List<Merchant> getMerchantsByDealOfferId(final UUID dealOfferId, final SearchOptions searchOptions) throws ServiceException;

	public List<Merchant> getMerchantsCreatedByMerchant(final UUID merchantId) throws ServiceException;

	/**
	 * Gets all deal offers in Talool
	 * 
	 * @param searchOpts
	 * @param calculateTotalResults
	 * @return
	 * @throws ServiceException
	 */
	public PaginatedResult<DealOfferSummary> getDealOfferSummary(final SearchOptions searchOpts, final boolean calculateTotalResults)
			throws ServiceException;

	/**
	 * Gets all deal offers matching title expression.
	 * 
	 * @param searchOpts
	 * @param title
	 * @param calculateTotalResults
	 * @return
	 * @throws ServiceException
	 */
	public PaginatedResult<DealOfferSummary> getDealOfferSummary(final SearchOptions searchOpts, final String title, final boolean calculateTotalResults)
			throws ServiceException;

	/**
	 * Gets offer summary count matching title expression
	 * 
	 * @param title
	 * @return
	 * @throws ServiceException
	 */
	public long getDealOfferSummaryCount(final String title) throws ServiceException;

	/**
	 * Gets all the offers of a publisher. A publishers offers are defined by a
	 * merchant that is the publisher
	 * 
	 * @param publisherMerchantId
	 * @param searchOpts
	 * @param calculateRowSize
	 * @return
	 * @throws ServiceException
	 */
	public PaginatedResult<DealOfferSummary> getPublisherDealOfferSummary(final UUID publisherMerchantId,
			final SearchOptions searchOpts, final boolean calculateRowSize) throws ServiceException;

	public PaginatedResult<DealOfferSummary> getPublisherDealOfferSummaryByTitle(final UUID publisherMerchantId,
			final SearchOptions searchOpts, final String title, final boolean calculateRowSize) throws ServiceException;

	public long getPublisherDealOfferSummaryTitleCount(final UUID publisherMerchantId, final String title) throws ServiceException;

	public long getDealOfferSummaryCount() throws ServiceException;

	public long getPublisherDealOfferSummaryCount(final UUID publisherMerchantId) throws ServiceException;

	/**
	 * Gets all merchants in Talool
	 * 
	 * @param searchOpts
	 * @param calculateTotalResults
	 * @return
	 * @throws ServiceException
	 */
	public PaginatedResult<MerchantSummary> getMerchantSummary(final SearchOptions searchOpts, final PropertyCriteria propertyCriteria, final boolean calculateTotalResults)
			throws ServiceException;

	/**
	 * Gets all merchants matching name expression.
	 * 
	 * @param searchOpts
	 * @param name
	 * @param calculateTotalResults
	 * @return
	 * @throws ServiceException
	 */
	public PaginatedResult<MerchantSummary> getMerchantSummary(final SearchOptions searchOpts, final String name, final PropertyCriteria propertyCriteria, final boolean calculateTotalResults)
			throws ServiceException;

	/**
	 * Gets merchant summary count matching name expression
	 * 
	 * @param name
	 * @return
	 * @throws ServiceException
	 */
	public long getMerchantSummaryCount(final String name, final PropertyCriteria propertyCriteria) throws ServiceException;

	/**
	 * Gets all the merchants of a publisher. A publishers merchants are defined
	 * by having a location created by the publisher
	 * 
	 * @param publisherMerchantId
	 * @param searchOpts
	 * @param calculateRowSize
	 * @return
	 * @throws ServiceException
	 */
	public PaginatedResult<MerchantSummary> getPublisherMerchantSummary(final UUID publisherMerchantId,
			final SearchOptions searchOpts, final PropertyCriteria propertyCriteria, final boolean calculateRowSize) throws ServiceException;

	public PaginatedResult<MerchantSummary> getPublisherMerchantSummaryByName(final UUID publisherMerchantId,
			final SearchOptions searchOpts, final String name, final PropertyCriteria propertyCriteria, final boolean calculateRowSize) throws ServiceException;

	public long getPublisherMerchantSummaryNameCount(final UUID publisherMerchantId, final String name, final PropertyCriteria propertyCriteria) throws ServiceException;

	public long getMerchantSummaryCount(final PropertyCriteria propertyCriteria) throws ServiceException;

	public long getPublisherMerchantSummaryCount(final UUID publisherMerchantId, final PropertyCriteria propertyCriteria) throws ServiceException;

	/**
	 * Gets deals in an offer
	 * 
	 * @param dealOfferId
	 * @param searchOpts
	 * @param calculateTotalResults
	 * @return
	 * @throws ServiceException
	 */
	public PaginatedResult<DealSummary> getDealSummary(final UUID dealOfferId, final SearchOptions searchOpts, final boolean calculateTotalResults)
			throws ServiceException;

	public long getDealSummaryCount(final UUID dealOfferId) throws ServiceException;

	public void moveDeals(final List<UUID> dealIds, final UUID dealOfferId, final long merchantAccountId) throws ServiceException;

	public List<DealOffer> getPublisherDealOffers(final UUID publisherId, final PropertyCriteria propertyCriteria) throws ServiceException;
	
	public Merchant getFundraiserByTrackingCode(final String code) throws ServiceException;
}
