package com.talool.service.mail;

import com.talool.core.Merchant;
import com.talool.core.MerchantCodeGroup;

public class EmailTrackingCodeEntity {

	public MerchantCodeGroup codeGroup;
	public Merchant publisher;
	public String trackingUrl;
	
	public EmailTrackingCodeEntity(MerchantCodeGroup codeGroup,
			Merchant publisher, String url) {
		super();
		this.codeGroup = codeGroup;
		this.publisher = publisher;
		this.trackingUrl = url;
	}
	
}
