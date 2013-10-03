package com.talool.payment.braintree;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.talool.core.Customer;
import com.talool.core.DealOffer;
import com.talool.core.service.ProcessorException;
import com.talool.domain.DealOfferImpl;
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
	private static final Logger LOG = LoggerFactory.getLogger(BraintreeUtil.class);
	private static final BraintreeUtil instance = new BraintreeUtil();
	private static final String COMPANY_PREFIX_DESCRIPTOR = "TAL*";
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

	private static final String MERCHANT_ID = "mkf3rwysqz6w9x44";
	private static final String PUBLIC_KEY = "ck6f7kcdq8jwq5b8";
	private static final String PRIVATE_KEY = "ac3b232be33cce4cf3ce108106d0a93e";

	private BraintreeUtil()
	{
		super();
		initGateway();
	}

	public BraintreeUtil get()
	{
		return instance;
	}

	private void initGateway()
	{
		gateway = new BraintreeGateway(
				ServiceConfig.get().isBraintreeSandboxEnabled() ? Environment.SANDBOX : Environment.PRODUCTION,
				MERCHANT_ID,
				PUBLIC_KEY,
				PRIVATE_KEY
				);
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
	private static String createDescriptor(final DealOffer dealOffer)
	{
		return StringUtils.left(COMPANY_PREFIX_DESCRIPTOR + dealOffer.getTitle(), PRODUCT_DESCRIPTOR_MAX_LEN).toUpperCase();
	}

	private static TransactionResult getTransactionResult(final Result<Transaction> result) throws ProcessorException
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

	public static TransactionResult voidTransaction(final String transactionId) throws ProcessorException
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

	public static TransactionResult processPaymentCode(final Customer customer, final DealOffer dealOffer,
			final String paymentCode) throws ProcessorException
	{
		Result<Transaction> result = null;
		TransactionRequest transRequest = null;
		TransactionResult transResult = null;

		try
		{
			transRequest = new TransactionRequest()
					.venmoSdkPaymentMethodCode(paymentCode)
					.amount(new BigDecimal(Float.toString(dealOffer.getPrice())))
					.descriptor()
					.name(createDescriptor(dealOffer))
					.done()
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

	public static TransactionResult processCard(final Customer customer, final DealOffer dealOffer,
			final PaymentDetail paymentDetail) throws ProcessorException
	{
		Result<Transaction> result = null;
		TransactionRequest transRequest = null;
		TransactionResult transResult = null;

		try
		{
			final String venmoSession = paymentDetail.getPaymentMetadata().get(VENMO_SDK_SESSION);
			final Card card = paymentDetail.getCard();

			transRequest = new TransactionRequest()
					.amount(new BigDecimal(Float.toString(dealOffer.getPrice())))
					.creditCard()
					.number(card.getAccountnumber())
					.expirationMonth(card.getExpirationMonth())
					.expirationYear(card.getExpirationYear())
					.cvv(card.getSecurityCode())
					.done()
					.options()
					.venmoSdkSession(venmoSession)
					.submitForSettlement(true)
					.storeInVault(paymentDetail.isSaveCard())
					.done()
					.descriptor()
					.name(createDescriptor(dealOffer))
					.done()
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

	public static void main(String args[])
	{
		DealOffer dof = new DealOfferImpl();
		dof.setTitle("Boulder Payback Book");

		System.out.println(BraintreeUtil.createDescriptor(dof));
	}
}
