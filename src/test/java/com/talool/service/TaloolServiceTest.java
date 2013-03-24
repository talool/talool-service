package com.talool.service;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.talool.core.AccountType;
import com.talool.core.Address;
import com.talool.core.Customer;
import com.talool.core.DealBook;
import com.talool.core.Merchant;
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

	protected String CUST_EMAIL1 = "christopher.justin-1000@gmail.com";
	protected String MERC_EMAIL1 = "merch-1000@gmail.com";

	@Test
	public void testCustomerIntegration() throws Exception
	{
		// yes if this were a "unit" test, these would not be dependent.
		// this is an integration test hacked together via JUnit, so deal with it :)
		testCreateCustomer();

		testDeleteCustomer();

		testCreateMerchant();

		testCreateDealBook();

		testDeleteMerchant();
	}

	public void testDeleteMerchant() throws ServiceException
	{
		final Merchant merch = taloolService.getMerchantByEmail(MERC_EMAIL1);
		// lets delete
		taloolService.deleteMerchant(merch.getId());

		// assert they are deleted
		Assert.assertFalse(taloolService.emailExists(AccountType.MER, MERC_EMAIL1));

	}

	public void testCreateDealBook() throws ServiceException
	{
		DealBook dealBook = taloolService.newDealBook(taloolService.getMerchantByEmail(MERC_EMAIL1));
		Long now = System.currentTimeMillis();

		dealBook.setCode("code-" + now);
		dealBook.setCost(9.99f);
		dealBook.setDetails("details-" + now);
		dealBook.setExpires(new Date());
		dealBook.setLatitude(44.012893d);
		dealBook.setLatitude(-79.441833d);
		dealBook.setSummary("Summary-" + now);
		dealBook.setTitle("Title-" + now);

		taloolService.save(dealBook);

		List<DealBook> dealBooks = taloolService.getDealBooksByEmail(MERC_EMAIL1);

		Assert.assertEquals(1, dealBooks.size());
		Assert.assertEquals(dealBook, dealBooks.get(0));

		taloolService.deleteDealBook(dealBooks.get(0).getId());

		dealBooks = taloolService.getDealBooksByEmail(MERC_EMAIL1);

		Assert.assertEquals(0, dealBooks.size());

	}

	public void testCreateMerchant() throws ServiceException
	{
		Merchant merchant = taloolService.newMerchant();
		merchant.setName("Merch1");
		merchant.setLatitude(-10.564142);
		merchant.setLongitude(-74.047852);
		merchant.setPhone("781-818-1212");
		merchant.setWebsiteUrl("http://merch1.com");
		merchant.setEmail(MERC_EMAIL1);
		merchant.setLogoUrl("http://merch.logos.com/logo.png");

		Address address = taloolService.newAddress();
		address.setAddress1("1234 East Street");
		address.setAddress2("Apt 555");
		address.setCity("Denver");
		address.setStateProvinceCounty("CO");
		address.setZip("80218");
		address.setCountry("US");

		merchant.setAddress(address);

		// create account
		taloolService.createAccount(merchant, "pass123");

	}

	public void testDeleteCustomer() throws ServiceException
	{
		final Customer cust = taloolService.getCustomerByEmail(CUST_EMAIL1);
		// lets delete
		taloolService.deleteCustomer(cust.getId());

		// assert they are deleted
		Assert.assertFalse(taloolService.emailExists(AccountType.CUS, CUST_EMAIL1));
	}

	public void testCreateCustomer() throws Exception
	{
		Customer cust = taloolService.newCustomer();
		cust.setEmail(CUST_EMAIL1);
		cust.setFirstName("Chris");
		cust.setLastName("Lintz");
		cust.setPassword("pass123");
		cust.setBirthDate(new Date());

		// create account
		taloolService.createAccount(cust, "pass123");

		// authenticate
		Customer cust2 = taloolService.authenticateCustomer(CUST_EMAIL1, "pass123");

		Assert.assertNotNull(cust2.getId());

		// find by email
		boolean emailExists = taloolService.emailExists(AccountType.CUS, CUST_EMAIL1);
		Assert.assertTrue(emailExists);
	}
}
