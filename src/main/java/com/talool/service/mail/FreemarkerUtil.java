package com.talool.service.mail;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.talool.core.Customer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantCodeGroup;
import com.talool.core.gift.Gift;
import com.talool.service.ServiceConfig;
import com.talool.utils.KeyValue;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * @author clintz
 * 
 */
public final class FreemarkerUtil
{
	private static Configuration freemarkerConfig = new Configuration();

	private static FreemarkerUtil instance;

	private static final String FUNDRAISER_NAME = "@fundraiser";

	public enum TemplateType
	{
		Registration, Gift, ResetPassword, Feedback, Fundraiser, MerchantRegistration, TrackingCode, PurchaseJob
	}

	private FreemarkerUtil() throws IOException
	{
		if (ServiceConfig.get().getTemplateDir().startsWith("classpath"))
		{
			freemarkerConfig.setClassForTemplateLoading(this.getClass(), "templates");
		}
		else
		{
			freemarkerConfig.setDirectoryForTemplateLoading(new File(ServiceConfig.get().getTemplateDir()));
		}

	};

	public static FreemarkerUtil get()
	{
		return instance;
	}

	public static synchronized FreemarkerUtil createInstance() throws IOException
	{
		if (instance == null)
		{
			instance = new FreemarkerUtil();
		}

		return instance;

	}

	public String renderRegistrationEmail(final Customer customer) throws IOException, TemplateException
	{
		final Template template = freemarkerConfig.getTemplate(ServiceConfig.get().getRegistrationTemplate());

		// Build the data-model
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("email", customer.getEmail());

		StringWriter stringWriter = new StringWriter();
		template.process(data, stringWriter);

		return stringWriter.toString();

	}
	
	public String renderMerchantRegistrationEmail(final MerchantAccount account) throws IOException, TemplateException
	{
		final Template template = freemarkerConfig.getTemplate(ServiceConfig.get().getMerchantRegistrationTemplate());

		// Build the data-model
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("email", account.getEmail());

		StringWriter stringWriter = new StringWriter();
		template.process(data, stringWriter);

		return stringWriter.toString();

	}

	public String renderPasswordRecoveryEmail(final Customer customer) throws IOException, TemplateException
	{
		final Template template = freemarkerConfig.getTemplate(ServiceConfig.get().getPasswordRecoveryTemplate());

		// Build the data-model
		// TODO build the single use link to reset the password
		StringBuilder sb = new StringBuilder(ServiceConfig.get().getPasswordRecoveryLink());
		sb.append("/").append(customer.getId()).append("/").append(customer.getResetPasswordCode());
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("resetLink", sb.toString());

		StringWriter stringWriter = new StringWriter();
		template.process(data, stringWriter);

		return stringWriter.toString();

	}

	public String renderGiftEmail(final Gift gift) throws IOException, TemplateException
	{
		final Template template = freemarkerConfig.getTemplate(ServiceConfig.get().getGiftTemplate());

		// Build the data-model
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("merchantName", gift.getDealAcquire().getDeal().getMerchant().getName());
		data.put("dealTitle", gift.getDealAcquire().getDeal().getTitle());
		data.put("dealSummary", gift.getDealAcquire().getDeal().getSummary());
		data.put("merchantImage", gift.getDealAcquire().getDeal().getImage().getMediaUrl());

		final StringBuilder sb = new StringBuilder();
		sb.append(ServiceConfig.get().getGiftLink()).append(gift.getId());
		data.put("giftLink", ServiceConfig.get().getGiftLink() + gift.getId());
		data.put("name", gift.getFromCustomer().getFirstName() + " " + gift.getFromCustomer().getLastName());
		StringWriter stringWriter = new StringWriter();
		template.process(data, stringWriter);

		return stringWriter.toString();

	}
	
	public String renderPurchaseJobEmail(final DealOfferPurchase purchase) throws IOException, TemplateException
	{
		final Template template = freemarkerConfig.getTemplate(ServiceConfig.get().getDealOfferPurchaseJobTemplate());

		// Build the data-model
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("offerTitle", purchase.getDealOffer().getTitle());
		data.put("offerSummary", purchase.getDealOffer().getSummary());
		data.put("publisherName", purchase.getDealOffer().getMerchant().getName());
		
		String notes = purchase.getProperties().getAsString(KeyValue.dealOfferPurchaseJobNotesKey);
		if (StringUtils.isEmpty(notes))
		{
			notes = "";
		}
		data.put("publisherNotes", notes);
		data.put("fullName", purchase.getCustomer().getFullName());
		
		StringWriter stringWriter = new StringWriter();
		template.process(data, stringWriter);

		return stringWriter.toString();

	}

	/**
	 * Gets the EmailMessage (subject/to) for fundraiser emails as seen in a
	 * customer's activity feed
	 * 
	 * @param offer
	 * @param fundraiser
	 * @param code
	 * @return EmailMessage
	 * @throws IOException
	 * @throws TemplateException
	 * 
	 * @TODO generate subjects and bodies via a message strategy
	 */
	public EmailMessage renderFundraiserEmail(final DealOfferPurchase dealOfferPurchase, final Merchant fundraiser, final String code, final String name)
			throws IOException, TemplateException
	{
		final StringBuilder sb = new StringBuilder();

		final Template template = freemarkerConfig.getTemplate(ServiceConfig.get().getFundraiserTemplate());

		// Build the data-model
		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("offerTitle", dealOfferPurchase.getDealOffer().getTitle());
		data.put("fundraiserName", fundraiser.getName());
		data.put("offerSummary", dealOfferPurchase.getDealOffer().getSummary());
		data.put("installLink", ServiceConfig.get().getInstallLink());
		data.put("code", code);
		data.put("fullName", name);

		final StringWriter stringWriter = new StringWriter();
		template.process(data, stringWriter);

		sb.append(ServiceConfig.get().getAndReplace(ServiceConfig.FUNDRAISER_SUBJECT, FUNDRAISER_NAME, fundraiser.getName()));

		return new EmailMessage(sb.toString(), stringWriter.toString());

	}
	
	public String renderTrackingCodeEmail(final EmailTrackingCodeEntity entity) throws IOException, TemplateException
	{
		final Template template = freemarkerConfig.getTemplate(ServiceConfig.get().getTrackingCodeTemplate());
		MerchantCodeGroup codeGroup = entity.codeGroup;
		Merchant publisher = entity.publisher;

		// Build the data-model
		final Map<String, String> data = new HashMap<String, String>();
		data.put("fundraiserName", codeGroup.getMerchant().getName());
		data.put("installLink", ServiceConfig.get().getInstallLink());
		data.put("code", codeGroup.getCodes().iterator().next().getCode());
		data.put("fullName", codeGroup.getCodeGroupTitle());
		data.put("publisherName", publisher.getName()); 
		data.put("trackingLink", entity.trackingUrl);
		data.put("cobrand", entity.cobrand);

		StringWriter stringWriter = new StringWriter();
		template.process(data, stringWriter);

		return stringWriter.toString();

	}
}
