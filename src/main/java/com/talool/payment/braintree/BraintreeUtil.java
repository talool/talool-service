package com.talool.payment.braintree;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;

public class BraintreeUtil
{
	private static final BraintreeUtil instance = new BraintreeUtil();

	private static BraintreeGateway gateway;

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
				Environment.SANDBOX,
				MERCHANT_ID,
				PUBLIC_KEY,
				PRIVATE_KEY
				);
	}

	/**
	 * Creates a payment transaction in braintree
	 * 
	 * @param customerId
	 * @param card
	 * @param venmoSession
	 * @param amount
	 * @return Braintree transaction
	 */
	public Result<Transaction> processCard(final UUID customerId, Map<String, String> cardDetails,
			final BigDecimal amount)
	{
		final TransactionRequest request = new TransactionRequest()
				.amount(amount)
				.creditCard()
				.number(null)
				.expirationMonth(null)
				.expirationYear(null)
				.cvv(null)
				.done()
				.options()
				.venmoSdkSession(null)
				.submitForSettlement(true)
				.storeInVaultOnSuccess(true)
				.done();

		Result<Transaction> result = gateway.transaction().sale(request);

		return result;

	}

}
