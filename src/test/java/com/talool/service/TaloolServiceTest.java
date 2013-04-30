package com.talool.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.talool.core.AccountType;
import com.talool.core.AcquireStatus;
import com.talool.core.Address;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealAcquireHistory;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.DealType;
import com.talool.core.DomainFactory;
import com.talool.core.FactoryManager;
import com.talool.core.Location;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantLocation;
import com.talool.core.Relationship;
import com.talool.core.RelationshipStatus;
import com.talool.core.SearchOptions;
import com.talool.core.Tag;
import com.talool.core.service.ServiceException;
import com.talool.utils.DealAcquireComparator;
import com.talool.utils.DealAcquireComparator.ComparatorType;
import com.talool.utils.MerchantComparator;

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

	// I am using this because i dont want to clone components, i just want IDs
	// to make it easier
	protected class DealAcquireHistoryWrapper
	{
		protected UUID dealAcquireId;
		protected AcquireStatus status;
		protected UUID customerId;
		protected UUID sharedbyMerchantId;
		protected UUID sharedbyCustomerId;
		protected Integer shareCount;
		protected Date updated;

		DealAcquireHistoryWrapper(DealAcquire dealAcquire)
		{
			this.dealAcquireId = dealAcquire.getId();;
			this.status = dealAcquire.getAcquireStatus();
			this.customerId = dealAcquire.getCustomer().getId();

			if (dealAcquire.getSharedByMerchant() != null)
			{
				this.sharedbyMerchantId = dealAcquire.getSharedByMerchant().getId();
			}

			if (dealAcquire.getSharedByCustomer() != null)
			{
				this.sharedbyCustomerId = dealAcquire.getSharedByCustomer().getId();
			}

			this.shareCount = dealAcquire.getShareCount();
			this.updated = dealAcquire.getUpdated();
		}

	}

	@Before
	public void setup()
	{
		domainFactory = FactoryManager.get().getDomainFactory();
	}

	@Test
	public void testCustomerIntegration() throws Exception
	{
		// yes if this were a "unit" test, these would not be dependent.
		// this is an integration test hacked together via JUnit, so no big deal!
		cleanTest();

		testRelationship();

		Customer customer = testCreateCustomer();

		Merchant testMerchant = testCreateMerchant();

		MerchantAccount testMerchantAccount = testMerchantAccount(testMerchant);

		final DealOffer dealOffer = testDealOffers(testMerchant, testMerchantAccount, customer);

		final DealOfferPurchase dealOfferPurchase = testDealOfferPurchase(customer, dealOffer);

		testDealAcquires(dealOfferPurchase, dealOffer);

	}

	/**
	 * Tests Search options on customer Deal Acquires (sorting & paginating)
	 * 
	 * @param dealOfferPurchase
	 * @param dealOffer
	 * @param expectedDealAcquires
	 * @throws ServiceException
	 */
	public void testDealAquireSearchOptions(DealOfferPurchase dealOfferPurchase, DealOffer dealOffer,
			List<DealAcquire> expectedDealAcquires) throws ServiceException
	{
		List<DealAcquire> dealAcquires = null;

		final int maxResults = 2;

		List<DealAcquire> allDealAcquires = new ArrayList<DealAcquire>();

		for (int sortTypeIndex = 0; sortTypeIndex <= 1; sortTypeIndex++)
		{
			int page = 0;
			int totalPaginated = 0;
			allDealAcquires.clear();

			boolean ascending = sortTypeIndex == 0 ? true : false;

			while (true)
			{
				SearchOptions searchOpts = new SearchOptions.Builder().maxResults(maxResults).page(page++)
						.sortProperty("dealAcquire.deal.title").ascending(ascending).build();

				dealAcquires = taloolService.getDealAcquires(dealOfferPurchase.getCustomer().getId(),
						dealOfferPurchase.getDealOffer().getMerchant().getId(), searchOpts);

				if (CollectionUtils.isEmpty(dealAcquires))
				{
					break;
				}

				allDealAcquires.addAll(dealAcquires);

				for (final DealAcquire dacs : dealAcquires)
				{
					totalPaginated++;
					LOG.info(dacs.getDeal().getTitle());
				}
			}

			Assert.assertEquals(totalPaginated, expectedDealAcquires.size());

			Comparator<DealAcquire> comparator = null;

			if (ascending)
			{
				comparator = new DealAcquireComparator(ComparatorType.DealTitle);
			}
			else
			{
				comparator = Collections.reverseOrder(new DealAcquireComparator(ComparatorType.DealTitle));

			}

			Collections.sort(expectedDealAcquires, comparator);

			for (int i = 0; i < expectedDealAcquires.size(); i++)
			{
				Assert.assertEquals(expectedDealAcquires.get(i).getId(), allDealAcquires.get(i).getId());
			}

		}

	}

	/**
	 * Tests Search options on Acquired Merchants (sorting & paginating)
	 * 
	 * @param dealOfferPurchase
	 * @param dealOffer
	 * @param expectedDealAcquires
	 * @throws ServiceException
	 */
	public void testDealAquireMerchants(DealOfferPurchase dealOfferPurchase, DealOffer dealOffer,
			List<DealAcquire> expectedDealAcquires) throws ServiceException
	{
		List<Merchant> merchantsAcquired = null;

		final int maxResults = 2;

		List<Merchant> allMerchantAcquires = new ArrayList<Merchant>();
		Set<Merchant> expectedMerchants = new HashSet<Merchant>();

		for (DealAcquire _dac : expectedDealAcquires)
		{
			expectedMerchants.add(_dac.getDeal().getMerchant());
		}

		for (int sortTypeIndex = 0; sortTypeIndex <= 1; sortTypeIndex++)
		{
			int page = 0;
			int totalPaginated = 0;
			allMerchantAcquires.clear();

			boolean ascending = sortTypeIndex == 0 ? true : false;

			while (true)
			{
				SearchOptions searchOpts = new SearchOptions.Builder().maxResults(maxResults).page(page++)
						.sortProperty("merchant.name").ascending(ascending).build();

				merchantsAcquired = taloolService.getMerchantAcquires(dealOfferPurchase.getCustomer()
						.getId(), searchOpts);

				if (CollectionUtils.isEmpty(merchantsAcquired))
				{
					break;
				}

				allMerchantAcquires.addAll(merchantsAcquired);

				for (final Merchant merch : merchantsAcquired)
				{
					totalPaginated++;
					LOG.info(merch.getName());
				}
			}

			Assert.assertEquals(totalPaginated, expectedMerchants.size());

			Comparator<Merchant> comparator = null;

			if (ascending)
			{
				comparator = new MerchantComparator(MerchantComparator.ComparatorType.Name);
			}
			else
			{
				comparator = Collections.reverseOrder(new MerchantComparator(
						MerchantComparator.ComparatorType.Name));
			}

			List<Merchant> expectedMerchantList = new ArrayList<Merchant>(expectedMerchants);

			Collections.sort(expectedMerchantList, comparator);

			for (int i = 0; i < expectedMerchantList.size(); i++)
			{
				Assert
						.assertEquals(expectedMerchantList.get(i).getId(), allMerchantAcquires.get(i).getId());
			}

		}

	}

	public void testDealAcquires(DealOfferPurchase dealOfferPurchase, DealOffer dealOffer)
			throws Exception
	{
		List<DealAcquire> dealAcquires = null;

		dealAcquires = taloolService.getDealAcquiresByCustomerId(dealOfferPurchase.getCustomer()
				.getId());

		List<Deal> deals = taloolService.getDealsByDealOfferId(dealOffer.getId());

		// test sort/pagination search options
		testDealAquireSearchOptions(dealOfferPurchase, dealOffer, dealAcquires);

		// test sort/pagination search options
		testDealAquireMerchants(dealOfferPurchase, dealOffer, dealAcquires);

		Assert.assertEquals(deals.size(), dealAcquires.size());

		for (final DealAcquire dealAcquire : dealAcquires)
		{
			Assert.assertEquals(dealOffer.getId(), dealAcquire.getDeal().getDealOffer().getId());
		}

		// update will trigger deal_acquire_history rows
		// evicting objects from session is needed because we are still under the
		// same Hibernate Session - if we dont daq will be the same object after
		// every get!
		List<DealAcquireHistoryWrapper> historyOfAcquires = new ArrayList<DealAcquireHistoryWrapper>();

		DealAcquire daq = dealAcquires.get(0);

		Customer originalCustomer = daq.getCustomer();
		Customer customerFriend = testCreateCustomer();

		// update #1 give to deal friend
		historyOfAcquires.add(new DealAcquireHistoryWrapper(daq));
		daq = taloolService.getDealAcquire(daq.getId());
		taloolService.giveDeal(daq, customerFriend);
		taloolService.refresh(daq);

		// friend shouldnt have 1
		List<DealAcquire> myAcquires = taloolService
				.getDealAcquiresByCustomerId(customerFriend.getId());
		Assert.assertEquals(1, myAcquires.size());

		// original customer should have 1 less
		myAcquires = taloolService.getDealAcquiresByCustomerId(originalCustomer.getId());
		Assert.assertEquals(dealAcquires.size() - 1, myAcquires.size());

		// update #2 friend accepts
		historyOfAcquires.add(new DealAcquireHistoryWrapper(daq));
		daq = taloolService.getDealAcquire(daq.getId());
		taloolService.acceptDeal(daq, originalCustomer.getId());
		taloolService.refresh(daq);

		// update #3 friend gives it back after accepting
		historyOfAcquires.add(new DealAcquireHistoryWrapper(daq));
		daq = taloolService.getDealAcquire(daq.getId());
		taloolService.giveDeal(daq, originalCustomer);
		taloolService.refresh(daq);

		Assert.assertEquals(new Integer(2), daq.getShareCount());

		// test the history
		List<DealAcquireHistory> history = taloolService.getDealAcquireHistory(daq.getId());

		for (DealAcquireHistoryWrapper da : historyOfAcquires)
		{
			System.out.println(da.updated);
		}

		System.out.println("----");
		for (DealAcquireHistory da : history)
		{
			System.out.println(da.getUpdated());
		}

		// test the persisted acquires with what should be
		for (int i = 0; i < historyOfAcquires.size(); i++)
		{
			DealAcquireHistory realHistory = history.get(i);
			DealAcquireHistoryWrapper expectedHistory = historyOfAcquires.get(i);

			Assert.assertEquals(expectedHistory.updated, realHistory.getUpdated());

			Assert.assertEquals(expectedHistory.customerId, realHistory.getCustomer().getId());

			UUID sharedByCustId = realHistory.getSharedByCustomer() == null ? null : realHistory
					.getSharedByCustomer().getId();

			UUID sharedByMerchId = realHistory.getSharedByMerchant() == null ? null : realHistory
					.getSharedByMerchant().getId();

			Assert.assertEquals(expectedHistory.sharedbyCustomerId, sharedByCustId);

			Assert.assertEquals(expectedHistory.sharedbyMerchantId, sharedByMerchId);

			Assert.assertEquals(expectedHistory.sharedbyMerchantId, sharedByMerchId);

		}

	}

	public void testRelationship() throws Exception
	{
		Customer fromCustomer = testCreateCustomer();
		Customer toCustomer = testCreateCustomer();

		taloolService.save(domainFactory.newRelationship(fromCustomer, toCustomer,
				RelationshipStatus.FRIEND));

		List<Relationship> rels = taloolService.getRelationshipsFrom(fromCustomer.getId());
		Assert.assertEquals(1, rels.size());
		Assert.assertEquals(rels.get(0).getToCustomer(), toCustomer);

		rels = taloolService.getRelationshipsTo(toCustomer.getId());
		Assert.assertEquals(1, rels.size());
		Assert.assertEquals(rels.get(0).getFromCustomer(), fromCustomer);

	}

	public DealOfferPurchase testDealOfferPurchase(final Customer customer, final DealOffer dealOffer)
			throws ServiceException
	{
		// lets purchase!
		DealOfferPurchase dealOfferPurchase = domainFactory.newDealOfferPurchase(customer, dealOffer);

		taloolService.save(dealOfferPurchase);
		taloolService.refresh(dealOfferPurchase);

		List<DealOfferPurchase> dealOfferPurchaseResult = taloolService
				.getDealOfferPurchasesByCustomerId(customer.getId());

		Assert.assertEquals(1, dealOfferPurchaseResult.size());

		Assert.assertEquals(dealOffer.getTitle(), dealOfferPurchaseResult.get(0).getDealOffer()
				.getTitle());

		return dealOfferPurchase;
	}

	public DealOffer testDealOffers(final Merchant merchant, final MerchantAccount merchantAccount,
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
		Assert.assertEquals(dealOffer.getCreatedByMerchantAccount().getId(), dealOfferResult
				.getCreatedByMerchantAccount().getId());
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

		return dealOffer;

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
