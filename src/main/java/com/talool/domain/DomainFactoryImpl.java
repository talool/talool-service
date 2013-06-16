package com.talool.domain;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.DomainFactory;
import com.talool.core.FactoryManager;
import com.talool.core.Location;
import com.talool.core.MediaType;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantIdentity;
import com.talool.core.MerchantLocation;
import com.talool.core.MerchantMedia;
import com.talool.core.Relationship;
import com.talool.core.RelationshipStatus;
import com.talool.core.Tag;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.MerchantSocialAccount;
import com.talool.core.social.SocialNetwork;
import com.talool.domain.social.CustomerSocialAccountImpl;
import com.talool.domain.social.MerchantSocialAccountImpl;

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
	public CustomerSocialAccount newCustomerSocialAccount(final String socialNetworkName)
	{
		CustomerSocialAccount sac = null;
		try
		{
			SocialNetwork snet = FactoryManager.get().getServiceFactory().getTaloolService()
					.getSocialNetwork(SocialNetwork.NetworkName.valueOf(socialNetworkName));

			sac = new CustomerSocialAccountImpl();
			sac.setSocialNetwork(snet);
		}
		catch (Exception e)
		{
			LOG.error("Problem getSocialNetwork " + socialNetworkName, e);
		}

		return sac;
	}

	@Override
	public MerchantSocialAccount newMerchantSocialAccount(final String socialNetworkName)
	{
		MerchantSocialAccount sac = null;
		try
		{
			SocialNetwork snet = FactoryManager.get().getServiceFactory().getTaloolService()
					.getSocialNetwork(SocialNetwork.NetworkName.valueOf(socialNetworkName));

			sac = new MerchantSocialAccountImpl();
			sac.setSocialNetwork(snet);
		}
		catch (Exception e)
		{
			LOG.error("Problem getSocialNetwork " + socialNetworkName, e);
		}

		return sac;
	}

	@Override
	public Merchant newMerchant()
	{
		return newMerchant(false);
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
		DealOffer dealOffer = new DealOfferImpl(merchant, createdByMerchant);
		dealOffer.setUpdatedByMerchantAccount(createdByMerchant);
		return dealOffer;
	}

	@Override
	public DealOfferPurchase newDealOfferPurchase(Customer customer, DealOffer dealOffer)
	{
		return new DealOfferPurchaseImpl(customer, dealOffer);
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
	public Deal newDeal(UUID merchantId, final MerchantAccount createdByMerchantAccount, final boolean setDefaults)
	{
		final Deal deal = new DealImpl(createdByMerchantAccount);
		deal.setUpdatedByMerchantAccount(createdByMerchantAccount);

		if (setDefaults)
		{
			deal.setMerchant(createdByMerchantAccount.getMerchant());

			TaloolService taloolService = FactoryManager.get().getServiceFactory().getTaloolService();

			/*
			 * Grab a DealOffer from the logged in Merchant and use it's expiration
			 * date as the default for the Deal
			 */
			try
			{
				List<DealOffer> offers = taloolService.getDealOffersByMerchantId(createdByMerchantAccount
						.getMerchant().getId());
				if (CollectionUtils.isNotEmpty(offers))
				{
					// TODO New Deals should default to the most recently updated
					// DealOffer
					DealOffer dealOffer = offers.get(0);
					deal.setDealOffer(dealOffer);
					deal.setExpires(dealOffer.getExpires());
				}

			}
			catch (ServiceException se)
			{
				LOG.error("Failed to get offers for logged in merchant", se);
			}

			/*
			 * Pass the Merchant's tags to the Deal by default TODO should use
			 * "reattach" rather than "refresh"
			 */
			try
			{
				Merchant merchant;
				if (merchantId == null)
				{
					merchant = createdByMerchantAccount.getMerchant();
				}
				else
				{
					merchant = taloolService.getMerchantById(merchantId);
				}
				taloolService.refresh(merchant);
				Set<Tag> tags = merchant.getTags();
				if (CollectionUtils.isNotEmpty(tags))
				{
					deal.setTags(tags);
				}
			}
			catch (ServiceException se)
			{
				LOG.error("Failed to reattach the merchant when getting tags", se);
			}
			catch (Exception e)
			{
				LOG.error("Failed to get tags for logged in merchant", e);
			}

		}

		return deal;
	}

	@Override
	public MerchantIdentity newMerchantIdentity(final UUID id, final String name)
	{
		return new MerchantIdentityImpl(id, name);
	}

	@Override
	public MerchantMedia newMedia(final UUID merchantId, final String mediaUrl,
			final MediaType mediaType)
	{
		final MerchantMediaImpl media = new MerchantMediaImpl();
		media.setMediaUrl(mediaUrl);
		media.setMerchantId(merchantId);
		media.setMediaType(mediaType);
		return media;
	}

	@Override
	public Merchant newMerchant(boolean topLevelOnly)
	{
		Merchant merchant = new MerchantImpl();

		if (!topLevelOnly)
		{
			MerchantLocation location = newMerchantLocation();
			merchant.addLocation(location);
		}
		return merchant;

	}
}
