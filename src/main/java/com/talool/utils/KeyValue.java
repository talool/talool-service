package com.talool.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.talool.domain.Properties;

public class KeyValue implements Serializable
{
	public static final String superUser = "super_user";
	public static final String fundraiser = "fundraiser";
	public static final String fundraisingBook = "fundraising_book";
	public static final String publisher = "publisher";
	public static final String fundraisingPublisher = "fundraising_publisher";
	public static final String analytics = "analytics";
	public static final String canMoveDeals = "can_move_deals";
	public static final String percentage = "percentage";
	public static final String dealValue = "deal_value";
	public static final String dealRating = "deal_rating";
	public static final String merchantCode = "merchant_code";
	public static final String merchantTermsAcceptedV1 = "merchant_tos_v1_accepted";
	public static final String fundraiserTermsAcceptedV1 = "fundraiser_tos_v1_accepted";
	public static final String publisherTermsAcceptedV1 = "publisher_tos_v1_accepted";
	public static final String merchantAgreementAcceptedV1 = "merchant_agreement_v1_accepted";
	public static final String merchantCustomerId = "merchant_customer_id";

	// braintree stuff
	public static final String braintreeSubmerchantId = "bt_submerch_id";
	public static final String braintreeSubmerchantStatus = "bt_submerch_status";
	public static final String braintreeSubmerchantStatusTimestamp = "bt_submerch_status_ts";
	public static final String braintreeSubmerchantStatusMessage = "bt_submerch_status_msg";

	// Talool fee discount percent
	public static final String fundraiserDistributionPercent = "FDP";
	public static final String taloolFeeDiscountPercent = "TFDP";
	public static final String taloolFeePercent = "TFP";
	public static final String taloolFeeMinumum = "TFM";

	// Payment receipt containing break down of items
	public static final String paymentReceipt = "payment_receipt";

	public String key;
	public String value;

	public KeyValue(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	public static List<KeyValue> getKeyValues(Properties props)
	{
		List<KeyValue> keyVals = new ArrayList<KeyValue>();

		for (Entry<String, String> entry : props.getAllProperties().entrySet())
		{
			keyVals.add(new KeyValue(entry.getKey(), entry.getValue()));
		}
		return keyVals;
	}

	private static final long serialVersionUID = 7882876501860467190L;

}
