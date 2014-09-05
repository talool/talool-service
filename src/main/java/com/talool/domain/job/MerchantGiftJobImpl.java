package com.talool.domain.job;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.domain.DealImpl;
import com.talool.messaging.job.MerchantGiftJob;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "gift")
@DiscriminatorValue("MG")
public class MerchantGiftJobImpl extends MessagingJobImpl implements MerchantGiftJob
{
	private static final long serialVersionUID = 2050241397165232974L;

	@ManyToOne(targetEntity = DealImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "deal_id")
	private Deal deal;

	public MerchantGiftJobImpl()
	{};

	public MerchantGiftJobImpl(final Merchant merchant, final MerchantAccount createdByMerchantAccount, final Customer fromCustomer,
			final Deal deal, final Date scheduledStartDate, final String notes)
	{
		super(merchant, createdByMerchantAccount, fromCustomer, scheduledStartDate, notes);
		this.deal = deal;
	}

	@Override
	public void setDeal(Deal deal)
	{
		this.deal = deal;
	}

	@Override
	public Deal getDeal()
	{
		return deal;
	}

}
