package com.talool.core.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealOfferPurchase;
import com.talool.core.Location;
import com.talool.core.Merchant;
import com.talool.core.Relationship;
import com.talool.core.SearchOptions;
import com.talool.core.gift.EmailGift;
import com.talool.core.gift.Gift;
import com.talool.core.gift.GiftStatus;
import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.SocialNetwork;
import com.talool.domain.CustomerCriteria;
import com.talool.payment.PaymentDetail;
import com.talool.payment.TransactionResult;
import com.talool.service.HibernateService;
import com.talool.stats.CustomerSummary;
import com.talool.stats.PaginatedResult;

/**
 * Customer Service
 * 
 * @author clintz
 * 
 */
public interface CustomerService extends HibernateService, RequestHeaderSupport
{
	public void createAccount(final Customer customer, final String password) throws ServiceException;

	public void createDealOfferPurchase(final UUID customerId, final UUID dealOfferId) throws ServiceException;

	public void removeCustomer(final UUID id) throws ServiceException;

	public Customer authenticateCustomer(final String email, final String password) throws ServiceException;

	public void save(final Customer customer) throws ServiceException;

	public Customer getCustomerById(final UUID id) throws ServiceException;

	public Customer getCustomerByEmail(final String email) throws ServiceException, InvalidInputException;

	public List<Customer> getCustomers() throws ServiceException;

	public List<Customer> getCustomers(final CustomerCriteria criteria) throws ServiceException;

	public long getCustomerCount(final CustomerCriteria criteria) throws ServiceException;

	/**
	 * Gets Merchants associated with a customer (via paid deal books or free)
	 * 
	 * @param customerId
	 * @return
	 * @throws ServiceException
	 */
	public List<Merchant> getMerchantsByCustomerId(final Long customerId) throws ServiceException;

	public List<Customer> getFriends(final UUID id) throws ServiceException;

	public void save(final Relationship relationship) throws ServiceException;

	public List<Relationship> getRelationshipsFrom(final UUID customerId) throws ServiceException;

	public List<Relationship> getRelationshipsTo(final UUID customerId) throws ServiceException;

	public List<DealAcquire> getDealAcquires(final UUID customerId, final UUID merchantId, final SearchOptions searchOptions)
			throws ServiceException;

	public List<DealOfferPurchase> getDealOfferPurchasesByCustomerId(final UUID customerId) throws ServiceException;

	/**
	 * Redeems the deal if all the acquireStatus is in the proper state. Returns a confirmation code upon successful
	 * redemption.
	 * 
	 * @param dealAcquire
	 * @param customerId
	 * @return A String representing the confirmation code
	 * 
	 * @throws ServiceException
	 */
	public String redeemDeal(final UUID dealAcquireId, final UUID customerId, final Location location) throws ServiceException;

	public List<Deal> getDealsByCustomerId(final UUID accountId) throws ServiceException;

	public List<Merchant> getMerchantAcquires(final UUID customerId, final SearchOptions searchOptions) throws ServiceException;

	/**
	 * Gets merchants acquired by customerId using searchOptions. If location is not null, the distanceInMeters is
	 * calculated for every merchant location
	 * 
	 * @param customerId
	 * @param categoryId
	 * @param searchOptions
	 * @return
	 * @throws ServiceException
	 */
	public List<Merchant> getMerchantAcquires(final UUID customerId, final SearchOptions searchOptions, final Location location)
			throws ServiceException;

	/**
	 * Gets merchants acquired by customerId using searchOptions
	 * 
	 * @param customerId
	 * @param categoryId
	 * @param searchOptions
	 * @return
	 * @throws ServiceException
	 */
	public List<Merchant> getMerchantAcquires(final UUID customerId, final Integer categoryId, final SearchOptions searchOptions)
			throws ServiceException;

	public List<DealAcquire> getDealAcquiresByCustomerId(final UUID customerId) throws ServiceException;

	public DealAcquire getDealAcquire(final UUID dealAcquireId) throws ServiceException;

	public void addFavoriteMerchant(final UUID customerId, final UUID merchantId) throws ServiceException;

	public void removeFavoriteMerchant(final UUID customerId, final UUID merchantId) throws ServiceException;

	public void remove(CustomerSocialAccount cas) throws ServiceException;

	/**
	 * Removes the socialAccount for the given customerId and SocialNetwork.
	 * 
	 * @param customerId
	 * @param socialNetwork
	 * @throws ServiceException
	 */
	public void removeSocialAccount(final UUID customerId, final SocialNetwork socialNetwork) throws ServiceException;

	public List<Merchant> getFavoriteMerchants(final UUID customerId, final SearchOptions searchOpts) throws ServiceException;

	// Gift stuff

	/**
	 * 
	 * @param owningCustomerId
	 * @param dealAcquireId
	 * @param facebookId
	 * @param receipientName
	 * @return Gift UUID
	 * @throws ServiceException
	 */
	public UUID giftToFacebook(final UUID owningCustomerId, final UUID dealAcquireId, final String facebookId,
			final String receipientName) throws ServiceException;

	/**
	 * 
	 * @param owningCustomerId
	 * @param dealAcquireId
	 * @param email
	 *          Gift UUID
	 * @param receipientName
	 * @throws ServiceException
	 */
	public UUID giftToEmail(final UUID owningCustomerId, final UUID dealAcquireId, final String email, final String receipientName)
			throws ServiceException;

	public UUID giftToEmail(final UUID owningCustomerId, final UUID dealAcquireId, final EmailGift gift, final String emailCategory)
			throws ServiceException;

	public Gift getGift(final UUID giftId) throws ServiceException;

	/**
	 * Gets the Gift (if one exists) related to the dealAcquireId
	 * 
	 * @param dealAcquireId
	 * @return Gift or null if there isn't one associated with the dealAcquireId
	 * @throws ServiceException
	 */
	public Gift getGiftOnDealAcquire(final UUID dealAcquireId) throws ServiceException;

	public List<Gift> getGifts(final UUID customerId, final GiftStatus[] requestStatus) throws ServiceException;

	public DealAcquire acceptGift(final UUID giftRequestId, final UUID receipientCustomerId) throws ServiceException;

	public void rejectGift(final UUID giftRequestId, final UUID receipientCustomerId) throws ServiceException;

	public void save(final CustomerSocialAccount socialAccount) throws ServiceException;

	public Customer getCustomerBySocialLoginId(final String socialLoginId) throws ServiceException;

	public void activateCode(final UUID customerId, final UUID dealOfferid, final String code) throws ServiceException;

	/**
	 * Checks whether a book (activation code) is a valid code
	 * 
	 * @param code
	 * @param dealOfferid
	 * @return
	 * @throws ServiceException
	 */
	public boolean isActivationCodeValid(final String code, final UUID dealOfferid) throws ServiceException;

	/**
	 * Creates a password reset code and a password reset expires. These are saved on the customer (getPasswordResetCode,
	 * getPasswordResetExpires)
	 * 
	 * @param customer
	 * @throws ServiceException
	 */
	public void createPasswordReset(final Customer customer) throws ServiceException;

	public TransactionResult purchaseByCard(final UUID customerId, final UUID dealOfferId, final PaymentDetail paymentDetail,
			final Map<String, String> paymentProperties) throws ServiceException, NotFoundException;

	public TransactionResult purchaseByCode(final UUID customerId, final UUID dealOfferId, final String paymentCode,
			final Map<String, String> paymentProperties) throws ServiceException, NotFoundException;

	public TransactionResult purchaseByNonce(final UUID customerId, final UUID dealOfferId, final String nonce,
			final Map<String, String> paymentProperties) throws ServiceException, NotFoundException;

	public String generateBraintreeClientToken(final UUID customerId) throws ServiceException, NotFoundException;

	/**
	 * Gets all customers in Talool
	 * 
	 * @param searchOpts
	 * @param calculateTotalResults
	 * @return
	 * @throws ServiceException
	 */
	public PaginatedResult<CustomerSummary> getCustomerSummary(final SearchOptions searchOpts, final boolean calculateTotalResults)
			throws ServiceException;

	/**
	 * Gets all customers matching email expression.
	 * 
	 * @param searchOpts
	 * @param email
	 * @param calculateTotalResults
	 * @return
	 * @throws ServiceException
	 */
	public PaginatedResult<CustomerSummary> getCustomerSummary(final SearchOptions searchOpts, final String email,
			final boolean calculateTotalResults) throws ServiceException;

	/**
	 * Gets customer summary count matching email expression
	 * 
	 * @param email
	 * @return
	 * @throws ServiceException
	 */
	public long getCustomerSummaryCount(final String email) throws ServiceException;

	/**
	 * Gets all the customers of a publisher. A publishers customers are defined by having at least 1 deal offer belonging
	 * to the publisher
	 * 
	 * @param publisherMerchantId
	 * @param searchOpts
	 * @param calculateRowSize
	 * @return
	 * @throws ServiceException
	 */
	public PaginatedResult<CustomerSummary> getPublisherCustomerSummary(final UUID publisherMerchantId, final SearchOptions searchOpts,
			final boolean calculateRowSize) throws ServiceException;

	public PaginatedResult<CustomerSummary> getPublisherCustomerSummaryByEmail(final UUID publisherMerchantId,
			final SearchOptions searchOpts, final String email, final boolean calculateRowSize) throws ServiceException;

	public long getPublisherCustomerSummaryEmailCount(final UUID publisherMerchantId, final String email) throws ServiceException;

	public long getCustomerSummaryCount() throws ServiceException;

	public long getPublisherCustomerSummaryCount(final UUID publisherMerchantId) throws ServiceException;

	/**
	 * Gives the gift back to the giver with the given reason code
	 * 
	 * @param giftId
	 * @throws ServiceException
	 */
	public void giveGiftBackToGiver(final UUID giftId, final String reason) throws ServiceException;

}
