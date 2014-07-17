package com.talool.service.mail;

import com.talool.core.Merchant;
import com.talool.core.MerchantCodeGroup;

public class EmailTrackingCodeEntity {

	public MerchantCodeGroup codeGroup;
	public Merchant publisher;
	
	public EmailTrackingCodeEntity(MerchantCodeGroup codeGroup,
			Merchant publisher) {
		super();
		this.codeGroup = codeGroup;
		this.publisher = publisher;
	}
	
}
