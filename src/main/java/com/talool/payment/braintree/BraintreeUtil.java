package com.talool.payment.braintree;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.MerchantAccount;
import com.braintreegateway.MerchantAccountRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.WebhookNotification;
import com.talool.core.Customer;
import com.talool.core.DealOffer;
import com.talool.core.Merchant;
import com.talool.core.service.ProcessorException;
import com.talool.payment.Card;
import com.talool.payment.PaymentDetail;
import com.talool.payment.TransactionResult;
import com.talool.service.ServiceConfig;
import com.talool.utils.KeyValue;

/**
 * Braintree Utils
 * 
 * TODO - Create a PaymentProcessor interface and move these methods into an
 * implementation
 * 
 * @author clintz
 * 
 */
public class BraintreeUtil
{
	public static final String COMPANY_PREFIX_DESCRIPTOR = "TALOOL *";
	private static final Logger LOG = LoggerFactory.getLogger(BraintreeUtil.class);
	private static final BraintreeUtil instance = new BraintreeUtil();
	private static final String CLEAN_REGEX_DESCRIPTOR = "[^a-zA-Z0-9\\s]";
	private static final String CUSTOM_FIELD_PRODUCT = "product";
	private static final int PRODUCT_DESCRIPTOR_MAX_LEN = 22;
	private static BraintreeGateway gateway;

	// venmo constants
	public static final String KEY_ZIPCODE = "zipcode";
	public static final String KEY_CVV = "cvv";
	public static final String KEY_EXPIRATION_YEAR = "expiration_year";
	public static final String KEY_EXPIRATION_MONTH = "expiration_month";
	public static final String KEY_SECURITY_CODE = "security_code";
	public static final String KEY_ACCOUNT_NUMBER = "card_number";
	public static final String VENMO_SDK_SESSION = "venmo_sdk_session";

	private BraintreeUtil()
	{
		super();
		initGateway();
	}

	public static BraintreeUtil get()
	{
		return instance;
	}

	private void initGateway()
	{
		gateway = new BraintreeGateway(ServiceConfig.get().isBraintreeSandboxEnabled() ? Environment.SANDBOX : Environment.PRODUCTION,
				ServiceConfig.get().getBraintreeMerchantId(), ServiceConfig.get().getBraintreePublicKey(), ServiceConfig.get()
						.getBraintreePrivateKey());
	}

	public String getDebugString()
	{
		return String.format("Merchant Id: %s,  Public Key: %s, Private Key: %s, Env: %s", ServiceConfig.get().getBraintreeMerchantId(),
				ServiceConfig.get().getBraintreePublicKey(), ServiceConfig.get().getBraintreePrivateKey(), ServiceConfig.get()
						.isBraintreeSandboxEnabled() ? Environment.SANDBOX.toString() : Environment.PRODUCTION.toString());
	}

	/**
	 * Creates a descriptor seen on card statements
	 * 
	 * @see https 
	 *      ://www.braintreepayments.com/docs/java/transactions/dynamic_descriptors
	 * 
	 * @param dealOffer
	 * @return descriptor as a String
	 */
	public String createDescriptor(final DealOffer dealOffer)
	{
		return StringUtils.left(COMPANY_PREFIX_DESCRIPTOR + dealOffer.getTitle().replaceAll(CLEAN_REGEX_DESCRIPTOR, "").trim(),
				PRODUCT_DESCRIPTOR_MAX_LEN).toUpperCase();
	}

	private TransactionResult getTransactionResult(final Result<Transaction> result) throws ProcessorException
	{
		if (result.isSuccess())
		{
			return TransactionResult.successfulTransaction(result.getTarget().getId());
		}

		// validation errors
		if (result.getErrors() != null)
		{
			return TransactionResult.failedTransaction(result.getMessage());
		}

		switch (result.getTransaction().getStatus())
		{
			case PROCESSOR_DECLINED:

				// for (ValidationError error :
				// result.getErrors().getAllDeepValidationErrors())
				// {
				// System.out.println(error.getCode());
				// System.out.println(error.getMessage());
				// }
				return TransactionResult.failedTransaction(result.getTransaction().getProcessorResponseText(), result.getTransaction()
						.getProcessorResponseCode());

			case GATEWAY_REJECTED:
				return TransactionResult.failedTransaction(result.getMessage(), result.getTransaction().getGatewayRejectionReason().name());

			default:
				return TransactionResult.failedTransaction(TransactionResult.GENERIC_FAILURE_MESSAGE);

		}

	}

	public TransactionResult voidTransaction(final String transactionId) throws ProcessorException
	{
		TransactionResult transResult = null;

		try
		{
			final Result<Transaction> result = gateway.transaction().voidTransaction(transactionId);
			transResult = getTransactionResult(result);
		}
		catch (Exception e)
		{
			throw new ProcessorException("Problem voiding transaction: " + e.getMessage(), e);
		}

		return transResult;
	}

	public TransactionResult processPaymentCode(final Customer customer, final DealOffer dealOffer, final String paymentCode,
			final Merchant fundraiser) throws ProcessorException
	{
		Result<Transaction> result = null;
		TransactionRequest transRequest = null;
		TransactionResult transResult = null;

		try
		{
			transRequest = new TransactionRequest().venmoSdkPaymentMethodCode(paymentCode)
					.amount(new BigDecimal(Float.toString(dealOffer.getPrice()))).descriptor().name(createDescriptor(dealOffer)).done()
					.customField(CUSTOM_FIELD_PRODUCT, dealOffer.getTitle()).options().submitForSettlement(true).done();

			if (fundraiser != null)
			{
				decorateFundraiserTransaction(transRequest, fundraiser, dealOffer);
			}

			result = gateway.transaction().sale(transRequest);

			transResult = getTransactionResult(result);

		}
		catch (Exception e)
		{
			throw new ProcessorException("Problem processing paymentCode: " + e.getMessage(), e);
		}

		return transResult;

	}

	public TransactionResult processCard(final Customer customer, final DealOffer dealOffer, final PaymentDetail paymentDetail,
			Merchant fundraiser) throws ProcessorException
	{
		Result<Transaction> result = null;
		TransactionRequest transRequest = null;
		TransactionResult transResult = null;

		try
		{
			final String venmoSession = paymentDetail.getPaymentMetadata().get(VENMO_SDK_SESSION);
			final Card card = paymentDetail.getCard();

			transRequest = new TransactionRequest().amount(new BigDecimal(Float.toString(dealOffer.getPrice()))).creditCard()
					.number(card.getAccountnumber()).expirationMonth(card.getExpirationMonth()).expirationYear(card.getExpirationYear())
					.cvv(card.getSecurityCode()).done().options().venmoSdkSession(venmoSession).submitForSettlement(true)
					.storeInVault(paymentDetail.isSaveCard()).done().descriptor().name(createDescriptor(dealOffer)).done()
					.customField(CUSTOM_FIELD_PRODUCT, dealOffer.getTitle());

			if (fundraiser != null)
			{
				decorateFundraiserTransaction(transRequest, fundraiser, dealOffer);
			}

			result = gateway.transaction().sale(transRequest);

			transResult = getTransactionResult(result);

		}
		catch (Exception e)
		{
			throw new ProcessorException("Problem processing card: " + e.getMessage(), e);
		}

		return transResult;

	}

	private void decorateFundraiserTransaction(final TransactionRequest transRequest, final Merchant fundraiser,
			final DealOffer dealOffer)
	{
		String merchantAccountId = null;
		BigDecimal serviceFee = null;

		if (fundraiser != null)
		{
			merchantAccountId = fundraiser.getProperties().getAsString(KeyValue.braintreeSubmerchantId);
			Float percentToMerchant = fundraiser.getProperties().getAsFloat(KeyValue.percentage);
			if (merchantAccountId == null || percentToMerchant == null)
			{
				LOG.error(String.format(
						"Fundraiser %s and merchantId %s is missing the braintreeSubmerchantId or percent. Skipping Braintree serviceFee",
						fundraiser.getName(), fundraiser.getId()));
			}
			else
			{
				serviceFee = calculateServiceFee(percentToMerchant, dealOffer.getPrice());
				transRequest.merchantAccountId(merchantAccountId).serviceFeeAmount(serviceFee);
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Braintree: fundraiser %s cost %s percentToMerchant %s serviceFee %s ", fundraiser.getName(),
							dealOffer.getPrice(), percentToMerchant, serviceFee));
				}

			}
		}
	}

	/**
	 * Calculates the serviceFee kept by Talool based on the percent owed to the
	 * merchant and the purchase price
	 * 
	 * @param percent
	 * @param price
	 * @return
	 */
	public BigDecimal calculateServiceFee(final Float percentToMerchant, final Float purchasePrice)
	{
		return new BigDecimal(((100 - percentToMerchant) / 100) * purchasePrice).setScale(2, RoundingMode.HALF_EVEN);
	}

	// https://www.braintreepayments.com/docs/java/merchant_accounts/create
	public Result<MerchantAccount> onboardSubMerchant(final MerchantAccountRequest request)
	{
		return gateway.merchantAccount().create(request);
	}

	public void pay()
	{
		TransactionRequest request = new TransactionRequest().amount(new BigDecimal("100.00")).merchantAccountId("blue_ladders_store")
				.creditCard().number("5105105105105100").expirationDate("05/2020").done().options().submitForSettlement(true)
				.holdInEscrow(false).done().serviceFeeAmount(new BigDecimal("10.00"));

	}

	public String verifyWebhook(final String challenge)
	{
		return gateway.webhookNotification().verify(challenge);
	}

	public MerchantAccount findMerchantAccount(final String merchantAccountId)
	{
		return gateway.merchantAccount().find(merchantAccountId);
	}

	public Result<MerchantAccount> updateMerchantAccount(final String merchantAccountId, final MerchantAccountRequest request)
	{
		return gateway.merchantAccount().update(merchantAccountId, request);
	}

	public WebhookNotification parseWebhookNotification(final String btSignatureParam, final String btPayloadParam)
	{
		WebhookNotification webhookNotification = gateway.webhookNotification().parse(btSignatureParam, btPayloadParam);
		return webhookNotification;
	}
}
