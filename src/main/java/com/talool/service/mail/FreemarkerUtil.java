package com.talool.service.mail;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.talool.core.Customer;
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

	private FreemarkerUtil() throws IOException
	{
		freemarkerConfig
				.setDirectoryForTemplateLoading(new File(ServiceConfig.get().getTemplateDir()));

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
		System.out.println("rendering");
		final Template template = freemarkerConfig.getTemplate(ServiceConfig.get().getRegistrationTemplate());

		// Build the data-model
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("email", customer.getEmail());

		StringWriter stringWriter = new StringWriter();
		template.process(data, stringWriter);

		return stringWriter.toString();

	}

}
