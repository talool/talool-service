package com.talool.service;

import java.util.Date;
import java.util.List;
import java.util.Random;

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
import com.talool.core.DealBook;
import com.talool.core.DealBookContent;
import com.talool.core.DealBookPurchase;
import com.talool.core.DomainFactory;
import com.talool.core.FactoryManager;
import com.talool.core.Merchant;
import com.talool.core.MerchantDeal;
import com.talool.core.service.ServiceException;
import com.talool.domain.DealBookImpl;

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

		testCreateCustomer();

		// testDeleteCustomer();

		testCreateMerchant();

		testMerchantDeals();

		testDealBooks();

		// testDeleteMerchant();
	}

	public void testDealBooks() throws ServiceException
	{
		Merchant merchant = createMerchant();
		taloolService.createAccount(merchant, "pass123");

		MerchantDeal merchantDeal = createMerchantDeal(merchant);
		taloolService.save(merchantDeal);

		DealBook dealBook = createDealBook(merchant);

		taloolService.save(dealBook);

		DealBookContent dealBookContent = createDealBookContent(merchantDeal, dealBook);
		dealBookContent.setPageNumber(new Random().nextInt(100));
		taloolService.save(dealBookContent);

	}

	public void testDeleteMerchant() throws ServiceException
	{
		final Merchant merch = taloolService.getMerchantByEmail("");
		// lets delete
		taloolService.deleteMerchant(merch.getId());

		// assert they are deleted
		Assert.assertFalse(taloolService.emailExists(AccountType.MER, ""));

	}

	private DealBookContent createDealBookContent(MerchantDeal merchantDeal, DealBook dealBook)
	{
		DealBookContent dealBookContent = domainFactory.newDealBookContent(merchantDeal, dealBook);

		return dealBookContent;
	}

	private MerchantDeal createMerchantDeal(Merchant merchant)
	{
		Long now = System.currentTimeMillis();

		MerchantDeal merchantDeal = FactoryManager.get().getDomainFactory().newMerchantDeal(merchant);

		merchantDeal.setCode("code-" + now);
		merchantDeal.setDetails("details-" + now);
		merchantDeal.setExpires(new Date());
		merchantDeal.setSummary("Summary-" + now);
		merchantDeal.setTitle("Title-" + now);
		merchantDeal.setImageUrl("http://www.ImageUrl-" + now + ".com");

		return merchantDeal;
	}

	public void cleanTest() throws ServiceException
	{
		final Customer customer = taloolService.getCustomerByEmail("christopher.justin-1000@gmail.com");

		System.out.println(customer);
	}

	public void testMerchantDeals() throws ServiceException
	{
		final Merchant merchant = createMerchant();
		taloolService.createAccount(merchant, "pass123");

		MerchantDeal merchantDeal = createMerchantDeal(merchant);

		taloolService.save(merchantDeal);

		List<MerchantDeal> merchantDeals = taloolService.getMerchantDeals(merchant.getId(),
				Boolean.TRUE);

		Assert.assertEquals(1, merchantDeals.size());
		Assert.assertEquals(merchantDeal, merchantDeals.get(0));

		taloolService.deleteMerchantDeal(merchantDeals.get(0).getId());

		merchantDeals = taloolService.getMerchantDeals(merchant.getId(), Boolean.TRUE);

		Assert.assertEquals(0, merchantDeals.size());

		// create some buys
		DealBookImpl dealBook = (DealBookImpl) createDealBook(merchant);
		// dealBook.setTheDate();

		taloolService.save(dealBook);

		Customer cust1 = createCustomer();
		taloolService.createAccount(cust1, "pass123");

		Customer cust2 = createCustomer();
		taloolService.createAccount(cust2, "pass123");

		// Yes customers can buy the same books multiple times!
		DealBookPurchase dbp = createDealBookPurchase(dealBook, cust1);
		taloolService.save(dbp);
		dbp = createDealBookPurchase(dealBook, cust1);
		taloolService.save(dbp);

		List<DealBookPurchase> purchases = taloolService
				.getPurchases(AccountType.MER, merchant.getId());

		Assert.assertEquals(2, purchases.size());

		purchases = taloolService.getPurchasesByDealBookId(dealBook.getId());

		Assert.assertEquals(2, purchases.size());

		Assert.assertEquals(cust1, purchases.get(0).getCustomer());
		Assert.assertEquals(cust1, purchases.get(1).getCustomer());

		dbp = createDealBookPurchase(dealBook, cust2);
		taloolService.save(dbp);

		purchases = taloolService.getPurchasesByDealBookId(dealBook.getId());
		Assert.assertEquals(3, purchases.size());

		purchases = taloolService.getPurchases(AccountType.MER, merchant.getId());
		Assert.assertEquals(3, purchases.size());

		purchases = taloolService.getPurchases(AccountType.CUS, cust2.getId());
		Assert.assertEquals(1, purchases.size());
		Assert.assertEquals(cust2, purchases.get(0).getCustomer());

		// System.out.println(purchases.get(0).getCreated());

		List<DealBook> books = taloolService.getDealBooksByEmail(merchant.getEmail());

		for (DealBook dbw : books)
		{
			System.out.println(dbw);
		}

	}

	private DealBook createDealBook(Merchant merchant)
	{
		DealBook dealBook = domainFactory.newDealBook(merchant);
		Long now = System.currentTimeMillis();

		dealBook.setMerchant(merchant);
		dealBook.setCode("code-" + now);
		dealBook.setCost(9.99f);
		dealBook.setDetails("details-" + now);
		dealBook.setExpires(new Date());
		dealBook.setLatitude(44.012893d);
		dealBook.setLatitude(-79.441833d);
		dealBook.setSummary("Summary-" + now);
		dealBook.setTitle("Title-" + now);

		return dealBook;

	}

	private DealBookPurchase createDealBookPurchase(DealBook dealBook, Customer customer)
	{
		return domainFactory.newDealBookPurchase(dealBook, customer);

	}

	private Merchant createMerchant()
	{
		Long now = System.currentTimeMillis();

		Merchant merchant = domainFactory.newMerchant();
		merchant.setName("Merch" + now);
		merchant.setLatitude(-10.564142);
		merchant.setLongitude(-74.047852);
		merchant.setPhone("781-818-1212");
		merchant.setWebsiteUrl("http://merch1.com" + now);
		merchant.setEmail("billyjohnson" + now + "@gmail.com");
		merchant.setLogoUrl("http://merch.logos.com/logo.png");

		Address address = domainFactory.newAddress();
		address.setAddress1(now + " East Street");
		address.setAddress2("Apt " + now);
		address.setCity("Denver");
		address.setStateProvinceCounty("CO");
		address.setZip("80218");
		address.setCountry("US");

		merchant.setAddress(address);

		return merchant;
	}

	public void testCreateMerchant() throws ServiceException
	{
		Merchant merchant = createMerchant();
		// create account
		taloolService.createAccount(merchant, "pass123");

	}

	public void testDeleteCustomer() throws ServiceException
	{
		final Customer cust = taloolService.getCustomerByEmail("");
		// lets delete
		taloolService.deleteCustomer(cust.getId());

		// assert they are deleted
		Assert.assertFalse(taloolService.emailExists(AccountType.CUS, ""));
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

	public void testCreateCustomer() throws Exception
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
	}
}
