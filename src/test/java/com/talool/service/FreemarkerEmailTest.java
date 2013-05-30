package com.talool.service;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.Charsets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.io.Resources;
import com.talool.core.Customer;
import com.talool.domain.CustomerImpl;
import com.talool.service.mail.FreemarkerUtil;

import freemarker.template.TemplateException;

/**
 * 
 * @author clintz
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:taloolCommon.xml", "classpath:taloolMailService.xml" })
public class FreemarkerEmailTest
{
	@Autowired
	private FreemarkerUtil freemarkerUtil;

	@Test
	public void testRegistrationEmail() throws IOException, TemplateException
	{
		String emailAdd = "billythekid@gmail.com";

		Customer customer = new CustomerImpl();
		customer.setEmail(emailAdd);
		customer.setFirstName("Billy");
		customer.setLastName("The Kid");

		String renderedEmail = freemarkerUtil.renderRegistrationEmail(customer);

		System.out.println(renderedEmail);

		URL url = Resources.getResource("./expectedRegistrationEmail.html");
		String expectedEmail = Resources.toString(url, Charsets.UTF_8);

		Assert.assertEquals(expectedEmail, renderedEmail);
	}
}
