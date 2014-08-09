package com.talool.stats;

import java.io.Serializable;
import java.util.UUID;

/**
 * 
 * @author dmccuen
 * 
 */
public final class MerchantCodeSummary implements Serializable
{

	private static final long serialVersionUID = -5354872779942234861L;
	private UUID merchantId;
	private String name;
	private String email;
	private String code;
	private int purchaseCount;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getPurchaseCount() {
		return purchaseCount;
	}
	public void setPurchaseCount(int purchaseCount) {
		this.purchaseCount = purchaseCount;
	}
	
	

}
