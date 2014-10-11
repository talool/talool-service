package com.talool.domain.job;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.talool.core.Customer;
import com.talool.core.DealOffer;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.domain.DealOfferImpl;
import com.talool.messaging.job.DealOfferPurchaseJob;

/**
 * 
 * @author dmccuen
 * 
 * TODO how to join the dealOffer?
 * TODO is deal_offer_purchase the right Table name?
 * 
 */
@Entity
@Table(name = "deal_offer_purchase")
@DiscriminatorValue("DO")
public class DealOfferPurchaseJobImpl extends MessagingJobImpl implements DealOfferPurchaseJob
{
	private static final long serialVersionUID = 2050241397165232974L;

	@ManyToOne(targetEntity = DealOfferImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "deal_offer_id")
	private DealOffer offer;

	public DealOfferPurchaseJobImpl()
	{};

	public DealOfferPurchaseJobImpl(final Merchant merchant, final MerchantAccount createdByMerchantAccount, final Customer fromCustomer,
			final DealOffer offer, final Date scheduledStartDate, final String notes)
	{
		super(merchant, createdByMerchantAccount, fromCustomer, scheduledStartDate, notes);
		this.offer = offer;
	}

	@Override
	public void setDealOffer(DealOffer offer)
	{
		this.offer = offer;
	}

	@Override
	public DealOffer getDealOffer()
	{
		return offer;
	}

}
