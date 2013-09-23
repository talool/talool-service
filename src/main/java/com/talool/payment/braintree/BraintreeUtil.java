package com.talool.payment.braintree;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.talool.core.Customer;
import com.talool.core.DealOffer;
import com.talool.payment.Card;
import com.talool.payment.PaymentDetail;
import com.talool.payment.TransactionResult;
import com.talool.service.ServiceConfig;

/**
 * 
 * @author clintz
 * 
 */
public class BraintreeUtil
{
	private static final BraintreeUtil instance = new BraintreeUtil();

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

	private static final Logger LOG = LoggerFactory.getLogger(BraintreeUtil.class);

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

	public static TransactionResult processCard(final Customer customer, final DealOffer dealOffer, final PaymentDetail paymentDetail)
	{
		final String venmoSession = paymentDetail.getPaymentMetadata().get(VENMO_SDK_SESSION);
		final Card card = paymentDetail.getCard();

		final TransactionRequest request = new TransactionRequest()
				.amount(new BigDecimal(dealOffer.getPrice()))
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
				.done();

		final Result<Transaction> result = gateway.transaction().sale(request);

		final TransactionResult transactionResult = result.isSuccess() ? TransactionResult.successfulTransaction(result.getTarget().getId())
				: TransactionResult.failedTransaction(result.getMessage());

		return transactionResult;

	}
}
