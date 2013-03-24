package com.talool.core.service;

import java.util.List;

import com.talool.core.DealBook;
import com.talool.core.Merchant;

/**
 * Merchant Service
 * 
 * @author clintz
 * 
 */
public interface MerchantService
{
	// merchant stuff
	public Merchant newMerchant();

	public void createAccount(final Merchant customer, final String password) throws ServiceException;

	public void deleteMerchant(final Long id) throws ServiceException;

	public Merchant getMerchantById(final Long id) throws ServiceException;

	public Merchant getMerchantByEmail(final String email) throws ServiceException;

	public void save(final Merchant merchant) throws ServiceException;

	// deal book stuff

	public DealBook newDealBook(final Merchant merchant);

	public List<DealBook> getDealBooksByEmail(final String email) throws ServiceException;

	public void save(final DealBook dealBook) throws ServiceException;

	public void deleteDealBook(final Long id) throws ServiceException;

}
