package com.talool.service.mail;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.talool.core.Customer;
import com.talool.core.DealOffer;
import com.talool.core.Merchant;
import com.talool.core.gift.Gift;
import com.talool.service.ServiceConfig;

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

	public enum TemplateType
	{
		Registration, Gift, ResetPassword, Feedback, Fundraiser
	}

	private FreemarkerUtil() throws IOException
	{
		if (ServiceConfig.get().getTemplateDir().startsWith("classpath"))
		{
			freemarkerConfig.setClassForTemplateLoading(this.getClass(), "templates");
		}
		else
		{
			freemarkerConfig
					.setDirectoryForTemplateLoading(new File(ServiceConfig.get().getTemplateDir()));
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
	
	public String renderFundraiserEmail(final DealOffer offer, final Merchant fundraiser, final String code) throws IOException, TemplateException
	{
		final Template template = freemarkerConfig.getTemplate(ServiceConfig.get().getFundraiserTemplate());
		
		// Build the data-model
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("offerTitle", offer.getTitle());
		data.put("fundraiserName", fundraiser.getName());
		data.put("offerSummary", offer.getSummary());
		data.put("installLink", ServiceConfig.get().getInstallLink());
		data.put("code", code);

		StringWriter stringWriter = new StringWriter();
		template.process(data, stringWriter);

		return stringWriter.toString();

	}

}
