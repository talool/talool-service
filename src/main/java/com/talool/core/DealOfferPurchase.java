package com.talool.core;

import java.io.Serializable;
import java.util.Date;

import com.talool.domain.Properties;
import com.talool.payment.PaymentProcessor;

/**
 * 
 * @author clintz
 * 
 */
public interface DealOfferPurchase extends IdentifiableUUID, Serializable, PropertyEntity
{
	public DealOffer getDealOffer();

	public Customer getCustomer();

	public Location getLocation();

	public void setLocation(Location location);

	public Date getCreated();

	public String getProcessorTransactionId();

	public void setProcessorTransactionId(String processorTransactionId);

	public PaymentProcessor getPaymentProcessor();

	public void setPaymentProcessor(PaymentProcessor paymentProcessor);

	public Properties getProperties();

	public String getPropertyValue(final String key);

	public boolean isRefundedOrVoided();

}
