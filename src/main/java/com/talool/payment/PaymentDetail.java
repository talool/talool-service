package com.talool.payment;

import java.util.Map;

/**
 * 
 * @author clintz
 * 
 */
public final class PaymentDetail
{
	private boolean encryptedFields = true;
	private Card card;
	private Map<String, String> paymentMetadata;
	private boolean saveCard = true;

	public boolean isEncryptedFields()
	{
		return encryptedFields;
	}

	public PaymentDetail setEncryptedFields(boolean encryptedFields)
	{
		this.encryptedFields = encryptedFields;
		return this;
	}

	public Card getCard()
	{
		return card;
	}

	public PaymentDetail setCard(Card card)
	{
		this.card = card;
		return this;
	}

	public Map<String, String> getPaymentMetadata()
	{
		return paymentMetadata;
	}

	public PaymentDetail setPaymentMetadata(Map<String, String> paymentMetadata)
	{
		this.paymentMetadata = paymentMetadata;
		return this;
	}

	public boolean isSaveCard()
	{
		return saveCard;
	}

	public PaymentDetail setSaveCard(boolean saveCard)
	{
		this.saveCard = saveCard;
		return this;
	}

}
