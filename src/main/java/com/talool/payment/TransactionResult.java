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
	public static final String GENERIC_FAILURE_MESSAGE = "There was a problem processing your credit card, please double check your data and try again.";
	protected String errorCode = null;
	protected String errorText = null;
	protected String transactionId = null;
	protected PaymentProcessor paymentProcessor = PaymentProcessor.BRAINTREE;

	protected PaymentReceipt paymentReceipt;

	private TransactionResult()
	{}

	/**
	 * Gets the user friendly message to display on error (dont want to expose and
	 * have issues with fraud)
	 * 
	 * @return
	 */
	public String getMessage()
	{
		return GENERIC_FAILURE_MESSAGE;
	}

	public boolean isSuccess()
	{
		return StringUtils.isEmpty(errorText);
	}

	public static TransactionResult successfulTransaction(final String transactionId, final PaymentReceipt paymentReceipt)
	{
		final TransactionResult tr = new TransactionResult();
		tr.transactionId = transactionId;
		tr.paymentReceipt = paymentReceipt;
		return tr;
	}

	public static TransactionResult failedTransaction(final String errorText, final String errorCode)
	{
		final TransactionResult tr = new TransactionResult();
		tr.setErrorText(errorText);
		tr.setErrorCode(errorCode);
		return tr;
	}

	public static TransactionResult failedTransaction(final String errorText)
	{
		final TransactionResult tr = new TransactionResult();
		tr.setErrorText(errorText);
		tr.setErrorCode("UNKNOWN");
		return tr;
	}

	/**
	 * Gets the transactionId from the processor if the result was successful
	 * 
	 * @return
	 */
	public String getTransactionId()
	{
		return transactionId;
	}

	public PaymentProcessor getPaymentProcessor()
	{
		return paymentProcessor;
	}

	/**
	 * Gets the error code from gateway/processor or other
	 * 
	 * @return
	 */
	public String getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	/**
	 * Gets the error text from gateway/processor or other
	 * 
	 * @return
	 */
	public String getErrorText()
	{
		return errorText;
	}

	public void setErrorText(String errorText)
	{
		this.errorText = errorText;
	}

	/**
	 * Gets the payment receipt associated with the transaction or null otherwise
	 * 
	 * @return
	 */
	public PaymentReceipt getPaymentReceipt()
	{
		return paymentReceipt;
	}
}
