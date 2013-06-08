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
	private static final char[] symbols = new char[36];

	static
	{
		for (int idx = 0; idx < 10; ++idx)
		{
			symbols[idx] = (char) ('0' + idx);
			System.out.println(symbols[idx]);
		}

		for (int idx = 10; idx < 36; ++idx)
		{
			symbols[idx] = (char) ('A' + idx - 10);
			System.out.println(symbols[idx]);
		}

	}

	@Test
	public void testRandomConfirmationCode()
	{

		for (int i = 0; i < 100; i++)
		{
			System.out.println(RandomStringUtils.random(7, 0, symbols.length - 1, true, true, symbols));
		}

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
