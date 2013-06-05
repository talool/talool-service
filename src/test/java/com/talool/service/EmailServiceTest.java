package com.talool.service;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class EmailServiceTest
{
	@Test
	public void testRandomConfirmationCode()
	{
		System.out.println(RandomStringUtils.randomAlphanumeric(14));
	}

	@Test
	public void testTemplate()
	{
		// Freemarker configuration object
		Configuration cfg = new Configuration();
		try
		{
			cfg.setDirectoryForTemplateLoading(new File("/Users/clintz/dev/talool/talool-persistence/src/test/resources/"));
			Template template = cfg.getTemplate("registrationEmail.html");

			// Build the data-model
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("message", "Hello World!");

			// List parsing
			List<String> countries = new ArrayList<String>();
			countries.add("India");
			countries.add("United States");
			countries.add("Germany");
			countries.add("France");

			data.put("countries", countries);

			// Console output
			// Writer out = new OutputStreamWriter(System.out);
			// template.process(data, out);
			// out.flush();

			StringWriter stringWriter = new StringWriter();
			template.process(data, stringWriter);

			System.out.println(stringWriter.getBuffer().toString());

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (TemplateException e)
		{
			e.printStackTrace();
		}
	}

}
