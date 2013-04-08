package com.talool.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.talool.core.AccountType;
import com.talool.core.Address;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.DealType;
import com.talool.core.DomainFactory;
import com.talool.core.FactoryManager;
import com.talool.core.Location;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantLocation;
import com.talool.core.Tag;
import com.talool.core.service.ServiceException;

/**
 * Talool Service integration test cases
 * 
 * @author clintz
 * 
 */

@TestExecutionListeners(TransactionalTestExecutionListener.class)
// Rolls back transactions by default
public class TaloolServiceTest extends HibernateFunctionalTestBase
{
	private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceTest.class);

	private DomainFactory domainFactory;

	@Before
	public void setup()
	{
		domainFactory = FactoryManager.get().getDomainFactory();
	}

	@Test
	public void testCustomerIntegration() throws Exception
	{
		// yes if this were a "unit" test, these would not be dependent.
		// this is an integration test hacked together via JUnit, so deal with it :)
		cleanTest();

		Customer customer = testCreateCustomer();

		Merchant testMerchant = testCreateMerchant();

		MerchantAccount testMerchantAccount = testMerchantAccount(testMerchant);

		testDealOffers(testMerchant, testMerchantAccount, customer);

		// testDeleteMerchant();
	}

	public void testDealOffers(final Merchant merchant, final MerchantAccount merchantAccount,
			final Customer customer) throws ServiceException
	{
		DealOffer dealOffer = domainFactory.newDealOffer(merchant, merchantAccount);
		dealOffer.setActive(true);
		dealOffer.setCode("code123");
		dealOffer.setTitle("PaybackBook #1");
		dealOffer.setDealType(DealType.PAID_BOOK);
		dealOffer
				.setImageUrl("http://cdn.dzone.com/static/images/vaannila/hibernate/hibernateManyToOnePic1.gif");
		dealOffer.setPrice(20.00f);
		dealOffer.setSummary("Payback Book Boulder, CO");
		dealOffer.setExpires(DateUtils.addYears(new Date(), 1));

		taloolService.save(dealOffer);
		taloolService.refresh(dealOffer);

		DealOffer dealOfferResult = taloolService.getDealOffer(dealOffer.getId());

		Assert.assertEquals(dealOffer.getCode(), dealOfferResult.getCode());
		Assert.assertEquals(dealOffer.getTitle(), dealOfferResult.getTitle());
		Assert.assertEquals(dealOffer.getImageUrl(), dealOfferResult.getImageUrl());
		Assert.assertEquals(dealOffer.getSummary(), dealOfferResult.getSummary());
		Assert.assertEquals(dealOffer.getCreatedByMerchant().getId(), dealOfferResult
				.getCreatedByMerchant().getId());
		Assert.assertEquals(dealOffer.getPrice(), dealOfferResult.getPrice());
		Assert.assertEquals(dealOffer.getExpires(), dealOfferResult.getExpires());
		Assert.assertEquals(dealOffer.getType(), dealOfferResult.getType());

		Tag spaTag = taloolService.getTag("spa");
		if (spaTag == null)
		{
			spaTag = domainFactory.newTag("spa");
		}
		merchant.addTag(spaTag);

		Tag chineseTag = taloolService.getTag("chinese");
		if (chineseTag == null)
		{
			chineseTag = domainFactory.newTag("chinese");
		}

		// create 20 deals for the book
		for (int i = 0; i < 15; i++)
		{
			Deal newDeal = createDeal(dealOfferResult);
			newDeal.addTag(spaTag);
			newDeal.addTag(chineseTag);

			taloolService.save(newDeal);
			taloolService.refresh(newDeal);
			Deal resultDeal = taloolService.getDeal(newDeal.getId());
			assertDeal(newDeal, resultDeal);
		}

		List<Deal> resultDeals = taloolService.getDealsByDealOfferId(dealOffer.getId());

		Assert.assertEquals(15, resultDeals.size());

		Set<Tag> dealOfferTags = taloolService.getDealOfferTags(dealOfferResult.getId());
		Assert.assertEquals(2, dealOfferTags.size());

		// lets purchase!
		DealOfferPurchase dealOfferPurchase = domainFactory.newDealOfferPurchase(customer,
				dealOfferResult);

		taloolService.save(dealOfferPurchase);
		taloolService.refresh(dealOfferPurchase);

		List<DealOfferPurchase> dealOfferPurchaseResult = taloolService
				.getDealOfferPurchasesByDealOfferId(dealOfferResult.getId());

		Assert.assertEquals(1, dealOfferPurchaseResult.size());

	}

	private void assertDeal(final Deal expectedDeal, final Deal actualDeal)
	{
		Assert.assertEquals(expectedDeal.getCode(), actualDeal.getCode());
		Assert.assertEquals(expectedDeal.getImageUrl(), actualDeal.getImageUrl());
		Assert.assertEquals(expectedDeal.getSummary(), actualDeal.getSummary());
		Assert.assertEquals(expectedDeal.getTitle(), actualDeal.getTitle());
		Assert.assertEquals(expectedDeal.getDealOffer(), actualDeal.getDealOffer());
		Assert.assertEquals(expectedDeal.getExpires(), actualDeal.getExpires());
		Assert.assertEquals(expectedDeal.getMerchant(), actualDeal.getMerchant());

		Assert.assertNotNull(expectedDeal.getUpdated());
		Assert.assertNotNull(expectedDeal.getCreated());

		Assert.assertTrue(expectedDeal.getTags().equals(actualDeal.getTags()));

	}

	public MerchantAccount testMerchantAccount(Merchant merchant) throws ServiceException,
			NoSuchAlgorithmException, UnsupportedEncodingException
	{
		String email = "chris-" + System.currentTimeMillis() + "@gmail.com";
		MerchantAccount mac = domainFactory.newMerchantAccount(merchant);
		mac.setAllowDealCreation(true);
		mac.setEmail(email);
		mac.setPassword("pass123");
		mac.setRoleTitle("Master Deal Creator");

		taloolService.save(mac);
		taloolService.refresh(mac);

		MerchantAccount macResult = taloolService.authenticateMerchantAccount(merchant.getId(), email,
				"pass123");

		Assert.assertEquals(email, macResult.getEmail());
		Assert.assertEquals(EncryptService.MD5("pass123"), macResult.getPassword());
		Assert.assertEquals("Master Deal Creator", macResult.getRoleTitle());
		Assert.assertNotNull(macResult.getCreated());
		Assert.assertNotNull(macResult.getUpdated());

		return macResult;

	}

	public void testDeleteMerchant() throws ServiceException
	{
		// final Merchant merch = taloolService.getMerchantByEmail("");
		// lets delete
		// taloolService.deleteMerchant(merch.getId());

		// assert they are deleted
		Assert.assertFalse(taloolService.emailExists(AccountType.MER, ""));

	}

	private Deal createDeal(DealOffer dealOffer)
	{
		Long now = System.currentTimeMillis();

		Deal deal = domainFactory.newDeal(dealOffer);

		deal.setCode("code-" + now);
		deal.setDetails("details-" + now);
		deal.setExpires(new Date());
		deal.setSummary("Summary-" + now);
		deal.setTitle("Title-" + now);
		deal.setImageUrl("http://www.ImageUrl-" + now + ".com");

		return deal;
	}

	public void cleanTest() throws ServiceException
	{
		final Customer customer = taloolService.getCustomerByEmail("christopher.justin-1000@gmail.com");

		System.out.println(customer);
	}

	public Merchant testCreateMerchant() throws ServiceException
	{
		Long now = System.currentTimeMillis();

		Merchant merchant = domainFactory.newMerchant();
		merchant.setName("Merch" + now);

		// tags for fun

		Tag mexicanTag = taloolService.getTag("mexican");
		if (mexicanTag == null)
		{
			mexicanTag = domainFactory.newTag("mexican");
		}
		merchant.addTag(mexicanTag);

		Tag tapasTag = taloolService.getTag("tapas");
		if (tapasTag == null)
		{
			tapasTag = domainFactory.newTag("tapas");
		}
		merchant.addTag(tapasTag);

		Tag cubanTag = taloolService.getTag("cuban");
		if (cubanTag == null)
		{
			cubanTag = domainFactory.newTag("cuban");
		}
		merchant.addTag(cubanTag);

		Address address = domainFactory.newAddress();
		address.setAddress1(now + " East Street");
		address.setAddress2("Apt " + now);
		address.setCity("Denver");
		address.setStateProvinceCounty("CO");
		address.setZip("80218");
		address.setCountry("US");

		Location location = domainFactory.newLocation(-10.564142, -74.047852);

		MerchantLocation mel = domainFactory.newMerchantLocation();
		mel.setAddress(address);
		mel.setLocation(location);
		mel.setPhone("781-818-1212");
		mel.setWebsiteUrl("http://merch1.com" + now);
		mel.setEmail("billyjohnson" + now + "@gmail.com");
		mel.setLogoUrl("http://merch.logos.com/logo.png");
		mel.setLocationName("Merch" + now * 2);

		merchant.setPrimaryLocation(mel);

		taloolService.save(merchant);
		taloolService.refresh(merchant);

		List<Merchant> resultMerchs = taloolService.getMerchantByName("Merch" + now);
		Merchant resultMerch = resultMerchs.get(0);

		Assert.assertEquals(merchant.getName(), resultMerch.getName());
		Assert.assertNotNull(resultMerch.getCreated());

		Assert.assertEquals(merchant.getPrimaryLocation().getEmail(), resultMerch.getPrimaryLocation()
				.getEmail());
		Assert.assertEquals(merchant.getPrimaryLocation().getLocationName(), resultMerch
				.getPrimaryLocation().getLocationName());
		Assert.assertEquals(merchant.getPrimaryLocation().getLogoUrl(), resultMerch
				.getPrimaryLocation().getLogoUrl());
		Assert.assertEquals(merchant.getPrimaryLocation().getPhone(), resultMerch.getPrimaryLocation()
				.getPhone());
		Assert.assertEquals(merchant.getPrimaryLocation().getWebsiteUrl(), resultMerch
				.getPrimaryLocation().getWebsiteUrl());
		Assert.assertNotNull(merchant.getPrimaryLocation().getUpdated());
		Assert.assertNotNull(merchant.getPrimaryLocation().getCreated());

		Assert.assertEquals(merchant.getPrimaryLocation().getAddress(), resultMerch
				.getPrimaryLocation().getAddress());

		Assert.assertTrue(resultMerch.getTags().contains(mexicanTag));
		Assert.assertTrue(resultMerch.getTags().contains(tapasTag));
		Assert.assertTrue(resultMerch.getTags().contains(cubanTag));

		return merchant;
	}

	private Customer createCustomer()
	{
		Long now = System.currentTimeMillis();
		String firstName = "firstName";
		String lastName = "lastName";
		String email = firstName + lastName + now + "@gmail.com";

		Customer cust = domainFactory.newCustomer();

		cust.setEmail(email);
		cust.setFirstName(firstName);
		cust.setLastName(lastName);
		cust.setPassword("pass123");
		cust.setBirthDate(new Date());

		return cust;

	}

	public Customer testCreateCustomer() throws Exception
	{
		Customer cust = createCustomer();

		// create account
		taloolService.createAccount(cust, "pass123");

		// authenticate
		Customer cust2 = taloolService.authenticateCustomer(cust.getEmail(), "pass123");

		Assert.assertNotNull(cust2.getId());

		// find by email
		boolean emailExists = taloolService.emailExists(AccountType.CUS, cust.getEmail());
		Assert.assertTrue(emailExists);

		// lets delete
		// taloolService.deleteCustomer(cust.getId());

		// assert they are deleted
		// Assert.assertFalse(taloolService.emailExists(AccountType.CUS, ""));

		return cust;

	}
}
