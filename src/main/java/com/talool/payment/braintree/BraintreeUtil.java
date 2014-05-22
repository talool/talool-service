package com.talool.payment.braintree;

import java.math.BigDecimal;

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
import com.talool.core.Customer;
import com.talool.core.DealOffer;
import com.talool.core.service.ProcessorException;
import com.talool.payment.Card;
import com.talool.payment.PaymentDetail;
import com.talool.payment.TransactionResult;
import com.talool.service.ServiceConfig;

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

	public TransactionResult processPaymentCode(final Customer customer, final DealOffer dealOffer, final String paymentCode)
			throws ProcessorException
	{
		Result<Transaction> result = null;
		TransactionRequest transRequest = null;
		TransactionResult transResult = null;

		try
		{
			transRequest = new TransactionRequest().venmoSdkPaymentMethodCode(paymentCode)
					.amount(new BigDecimal(Float.toString(dealOffer.getPrice()))).descriptor().name(createDescriptor(dealOffer)).done()
					.customField(CUSTOM_FIELD_PRODUCT, dealOffer.getTitle());

			result = gateway.transaction().sale(transRequest);

			transResult = getTransactionResult(result);

		}
		catch (Exception e)
		{
			throw new ProcessorException("Problem processing paymentCode: " + e.getMessage(), e);
		}

		return transResult;

	}

	public TransactionResult processCard(final Customer customer, final DealOffer dealOffer, final PaymentDetail paymentDetail)
			throws ProcessorException
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

			result = gateway.transaction().sale(transRequest);

			transResult = getTransactionResult(result);

		}
		catch (Exception e)
		{
			throw new ProcessorException("Problem processing card: " + e.getMessage(), e);
		}

		return transResult;

	}

	// https://www.braintreepayments.com/docs/java/merchant_accounts/create
	public void onboardSubMerchant()
	{
		String masterMerchantAccountId = ServiceConfig.get().getString("braintree.master.merchant.account.id");

		MerchantAccountRequest request = new MerchantAccountRequest().individual().firstName("Jane").lastName("Doe")
				.email("jane@14ladders.com").phone("5553334444").dateOfBirth("1981-11-19").ssn("456-45-4567").address()
				.streetAddress("111 Main St").locality("Chicago").region("IL").postalCode("60622").done().done().business()
				.legalName("Jane's Ladders").dbaName("Jane's Ladders").taxId("98-7654321").address().streetAddress("111 Main St")
				.locality("Chicago").region("IL").postalCode("60622").done().done().funding()
				.destination(MerchantAccount.FundingDestination.BANK).email("funding@blueladders.com").mobilePhone("3037777777")
				.accountNumber("1123581321").routingNumber("071101307").done().tosAccepted(true)
				.masterMerchantAccountId(masterMerchantAccountId).id("blue_ladders_store");

		Result<MerchantAccount> result = gateway.merchantAccount().create(request);

		MerchantAccount ma = result.getTarget();
		LOG.info(ma.getStatus().toString()); // should be PENDING

	}

	public void pay()
	{
		TransactionRequest request = new TransactionRequest().amount(new BigDecimal("100.00")).merchantAccountId("blue_ladders_store")
				.creditCard().number("5105105105105100").expirationDate("05/2020").done().options().submitForSettlement().holdInEscrow()
				.done().serviceFeeAmount(new BigDecimal("10.00")).done();

	}
}
