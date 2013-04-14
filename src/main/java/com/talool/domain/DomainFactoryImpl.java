package com.talool.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.AccountType;
import com.talool.core.Address;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.DomainFactory;
import com.talool.core.FactoryManager;
import com.talool.core.Location;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantIdentity;
import com.talool.core.MerchantLocation;
import com.talool.core.MerchantManagedLocation;
import com.talool.core.Relationship;
import com.talool.core.RelationshipStatus;
import com.talool.core.SocialAccount;
import com.talool.core.Tag;

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
	public Deal newDeal(final DealOffer dealOffer)
	{
		return new DealImpl(dealOffer);
	}

	@Override
	public DealOfferPurchase newDealOfferPurchase(final DealOffer dealOffer, final Customer customer)
	{
		return new DealOfferPurchaseImpl(customer, dealOffer);
	}

	@Override
	public Customer newCustomer()
	{
		return new CustomerImpl();
	}

	@Override
	public Location newLocation(final Double longitude, final Double latitude)
	{
		return new LocationImpl(longitude, latitude);
	}

	@Override
	public MerchantLocation newMerchantLocation()
	{
		return new MerchantLocationImpl();
	}

	@Override
	public Tag newTag(String tagName)
	{
		Tag tag = new TagImpl();
		tag.setName(tagName.trim().toLowerCase());
		return tag;
	}

	@Override
	public MerchantAccount newMerchantAccount(final Merchant merchant)
	{
		return new MerchantAccountImpl(merchant);
	}

	@Override
	public DealOffer newDealOffer(final Merchant merchant, final MerchantAccount createdByMerchant)
	{
		return new DealOfferImpl(merchant, createdByMerchant);
	}

	@Override
	public DealOfferPurchase newDealOfferPurchase(Customer customer, DealOffer dealOffer)
	{
		return new DealOfferPurchaseImpl(customer, dealOffer);
	}

	@Override
	public MerchantManagedLocation newMerchantManagedLocation(Merchant merchant)
	{
		MerchantManagedLocation mml = new MerchantManagedLocationImpl();
		mml.setMerchant(merchant);
		mml.setMerchantLocation(newMerchantLocation());
		return mml;
	}

	@Override
	public Relationship newRelationship(final Customer fromCustomer, final Customer toCustomer,
			final RelationshipStatus status)
	{
		final Relationship rel = new RelationshipImpl();
		rel.setFromCustomer(fromCustomer);
		rel.setToCustomer(toCustomer);
		rel.setRelationshipStatus(status);
		return rel;
	}

	@Override
	public Deal newDeal()
	{
		return new DealImpl();
	}

	@Override
	public MerchantIdentity newMerchantIdentity(final Long id, final String name)
	{
		return new MerchantIdentityImpl(id, name);
	}
}
