package com.talool.domain.job;

import java.util.Date;
import java.util.UUID;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.talool.core.Customer;
import com.talool.core.DealOffer;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.messaging.job.DealOfferPurchaseJob;
import com.talool.utils.KeyValue;

/**
 * 
 * @author dmccuen
 * 
 */
@Entity
@Table(name = "gift")
@DiscriminatorValue("DO")
public class DealOfferPurchaseJobImpl extends MessagingJobImpl implements DealOfferPurchaseJob
{
	private static final long serialVersionUID = 2050241397165232974L;

	public DealOfferPurchaseJobImpl()
	{};

	public DealOfferPurchaseJobImpl(final Merchant merchant, final MerchantAccount createdByMerchantAccount, final Customer fromCustomer,
			final DealOffer offer, final Date scheduledStartDate, final String notes)
	{
		super(merchant, createdByMerchantAccount, fromCustomer, scheduledStartDate, notes);
		setDealOfferId(offer.getId());
	}

	@Override
	public void setDealOfferId(UUID offerId)
	{
		this.getProperties().createOrReplace(KeyValue.dealOfferIdKey, offerId.toString());
	}

	@Override
	public UUID getDealOfferId()
	{
		UUID id = null;
		String idStr = this.getProperties().getAsString(KeyValue.dealOfferIdKey);
		if (idStr != null)
		{
			id = UUID.fromString(idStr);
		}
		return id;
	}

}
