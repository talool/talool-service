package com.talool.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.AccountType;
import com.talool.core.Address;
import com.talool.core.Customer;
import com.talool.core.DealBook;
import com.talool.core.DealBookContent;
import com.talool.core.DealBookPurchase;
import com.talool.core.DomainFactory;
import com.talool.core.FactoryManager;
import com.talool.core.Location;
import com.talool.core.Merchant;
import com.talool.core.Deal;
import com.talool.core.SocialAccount;

/**
 * Default Factory for all domain objects
 * 
 * @author clintz
 * 
 */
final class DomainFactoryImpl implements DomainFactory
{
	private static final Logger LOG = LoggerFactory.getLogger(DomainFactoryImpl.class);

	public DomainFactoryImpl()
	{}

	@Override
	public SocialAccount newSocialAccount(final String socialNetworkName,
			final AccountType accountType)
	{
		try
		{
			return new SocialAccountImpl(FactoryManager.get().getServiceFactory().getTaloolService()
					.getSocialNetwork(socialNetworkName), accountType);
		}
		catch (Exception e)
		{
			LOG.error("Problem getSocialNetwork " + socialNetworkName, e);
		}

		return new SocialAccountImpl();
	}

	@Override
	public Address newAddress()
	{
		return new AddressImpl();
	}

	@Override
	public Merchant newMerchant()
	{
		return new MerchantImpl();
	}

	@Override
	public DealBook newDealBook(final Merchant merchant)
	{
		return new DealBookImpl();
	}

	@Override
	public Deal newMerchantDeal(final Merchant merchant)
	{
		return new DealImpl(merchant);
	}

	@Override
	public DealBookContent newDealBookContent(final Deal merchantDeal, final DealBook dealBook)
	{
		return new DealBookContentImpl(merchantDeal, dealBook);
	}

	@Override
	public DealBookPurchase newDealBookPurchase(final DealBook dealBook, final Customer customer)
	{
		return new DealBookPurchaseImpl(dealBook, customer);
	}

	@Override
	public Customer newCustomer()
	{
		return new CustomerImpl();
	}

	@Override
	public Location newLocation(Double longitude, Double latitude)
	{
		return new LocationImpl(longitude, latitude);
	}

}
