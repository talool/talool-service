package com.talool.core.service;

import java.util.List;
import java.util.UUID;

import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealOfferPurchase;
import com.talool.core.Merchant;
import com.talool.core.Relationship;
import com.talool.core.SearchOptions;
import com.talool.service.HibernateService;

/**
 * Customer Service
 * 
 * @author clintz
 * 
 */
public interface CustomerService extends HibernateService
{
	public void createAccount(final Customer customer, final String password) throws ServiceException;

	public void createDealOfferPurchase(final UUID customerId, final UUID dealOfferId) throws ServiceException;

	public void removeCustomer(final UUID id) throws ServiceException;

	public Customer authenticateCustomer(final String email, final String password)
			throws ServiceException;

	public void save(final Customer customer) throws ServiceException;

	public Customer getCustomerById(final UUID id) throws ServiceException;

	public Customer getCustomerByEmail(final String email) throws ServiceException;

	public List<Customer> getCustomers() throws ServiceException;

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

	public List<DealAcquire> getDealAcquires(final UUID customerId, final UUID merchantId,
			final SearchOptions searchOptions) throws ServiceException;

	public List<DealOfferPurchase> getDealOfferPurchasesByCustomerId(final UUID customerId)
			throws ServiceException;

	public void giveDeal(final DealAcquire dealAcquire, final Customer toCustomer)
			throws ServiceException;

	public void acceptDeal(final DealAcquire dealAcquire, final UUID customerId)
			throws ServiceException;

	public void rejectDeal(final DealAcquire dealAcquire, final UUID customerId)
			throws ServiceException;

	public void redeemDeal(final DealAcquire dealAcquire, final UUID customerId)
			throws ServiceException;

	public List<Deal> getDealsByCustomerId(final UUID accountId) throws ServiceException;

	public List<Merchant> getMerchantAcquires(final UUID customerId, final SearchOptions searchOptions)
			throws ServiceException;

	public List<Merchant> getMerchantAcquires(final UUID customerId, final Integer categoryId, final SearchOptions searchOptions)
			throws ServiceException;

	public List<DealAcquire> getDealAcquiresByCustomerId(final UUID customerId)
			throws ServiceException;

	public DealAcquire getDealAcquire(final UUID dealAcquireId) throws ServiceException;

	public void addFavoriteMerchant(final UUID customerId, final UUID merchantId) throws ServiceException;

	public void removeFavoriteMerchant(final UUID customerId, final UUID merchantId) throws ServiceException;

	public List<Merchant> getFavoriteMerchants(final UUID customerId, final SearchOptions searchOpts) throws ServiceException;

}
