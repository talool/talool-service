package com.talool.core.service;

import java.util.List;

import com.talool.core.AccountType;
import com.talool.core.DealBook;
import com.talool.core.DealBookContent;
import com.talool.core.DealBookPurchase;
import com.talool.core.Merchant;
import com.talool.core.MerchantDeal;

/**
 * Merchant Service
 * 
 * @author clintz
 * 
 */
public interface MerchantService
{

	public void createAccount(final Merchant customer, final String password) throws ServiceException;

	public void deleteMerchant(final Long id) throws ServiceException;

	public Merchant getMerchantById(final Long id) throws ServiceException;

	public Merchant getMerchantByEmail(final String email) throws ServiceException;

	public void save(final Merchant merchant) throws ServiceException;

	public List<DealBook> getDealBooksByEmail(final String email) throws ServiceException;

	public List<MerchantDeal> getMerchantDeals(final Long merchantId, final Boolean isActive)
			throws ServiceException;

	public void save(final DealBook dealBook) throws ServiceException;

	public void save(final DealBookContent dealBookContenet) throws ServiceException;

	public void save(final MerchantDeal merchantDeal) throws ServiceException;

	public void deleteDealBook(final Long id) throws ServiceException;

	public void deleteDealBookContent(final Long id) throws ServiceException;

	public void deleteMerchantDeal(final Long id) throws ServiceException;

	public void save(final DealBookPurchase dealBookPurchase) throws ServiceException;

	public List<DealBookPurchase> getPurchases(final AccountType accountType, final Long accountId)
			throws ServiceException;

	public List<DealBookPurchase> getPurchasesByDealBookId(final Long dealBookId)
			throws ServiceException;

}
