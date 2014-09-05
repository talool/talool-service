package com.talool.stats;

import java.io.Serializable;
import java.util.UUID;

/**
 * 
 * @author dmccuen
 * 
 */
public final class FundraiserSummary implements Serializable
{
	private static final long serialVersionUID = 380097877144262322L;
	private UUID merchantId;
	private String name;
	private int dealOffersSoldCount;
	private int merchantCodeCount;
	private String properties;
	
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	public UUID getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(UUID merchantId) {
		this.merchantId = merchantId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDealOffersSoldCount() {
		return dealOffersSoldCount;
	}
	public void setDealOffersSoldCount(int dealOffersSoldCount) {
		this.dealOffersSoldCount = dealOffersSoldCount;
	}
	public int getMerchantCodeCount() {
		return merchantCodeCount;
	}
	public void setMerchantCodeCount(int merchantCodeCount) {
		this.merchantCodeCount = merchantCodeCount;
	}

	

}
