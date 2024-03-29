package com.talool.core.service;

import com.talool.core.Customer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.MerchantAccount;
import com.talool.core.gift.EmailGift;
import com.talool.service.mail.EmailRequest;
import com.talool.service.mail.EmailRequestParams;
import com.talool.service.mail.EmailTrackingCodeEntity;

/**
 * Talool Email Service
 * 
 * @author clintz
 * 
 */
public interface EmailService
{
	public void sendPasswordRecoveryEmail(final EmailRequestParams<Customer> emailRequestParams) throws ServiceException;

	public void sendGiftEmail(final EmailRequestParams<EmailGift> emailRequestParams) throws ServiceException;

	public void sendGiftEmail(final EmailRequestParams<EmailGift> emailRequestParams, final String emailCategory)
			throws ServiceException;

	public void sendTrackingCodeEmail(final EmailRequestParams<EmailTrackingCodeEntity> emailRequestParams) throws ServiceException;

	public void sendMerchantAccountEmail(final EmailRequestParams<MerchantAccount> emailRequestParams) throws ServiceException;
	
	public void sendDealOfferPurchaseJobEmail(final EmailRequestParams<DealOfferPurchase> emailRequestParams, final String emailCategory) throws ServiceException;

	public void sendEmail(final EmailRequest<?> emailRequest) throws ServiceException;

}
