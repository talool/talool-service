package com.talool.payment;

import org.apache.commons.lang3.StringUtils;

/**
 * A transaction result
 * 
 * @author clintz
 * 
 */
public class TransactionResult
{
	protected String errorMessage = null;
	protected String transactionId = null;
	protected PaymentProcessor paymentProcessor = PaymentProcessor.BRAINTREE;

	private TransactionResult()
	{}

	public boolean isSuccess()
	{
		return StringUtils.isEmpty(errorMessage);
	}

	public static TransactionResult successfulTransaction(final String transactionId)
	{
		final TransactionResult tr = new TransactionResult();
		tr.transactionId = transactionId;
		return tr;
	}

	public static TransactionResult failedTransaction(final String errorMessage)
	{
		final TransactionResult tr = new TransactionResult();
		tr.setErrorMessage(errorMessage);
		return tr;
	}

	public String getTransactionId()
	{
		return transactionId;
	}

	private void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public String getMessage()
	{
		return errorMessage;
	}

	public PaymentProcessor getPaymentProcessor()
	{
		return paymentProcessor;
	}

}
