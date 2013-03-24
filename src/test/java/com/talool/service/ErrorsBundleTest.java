package com.talool.service;

import java.util.ResourceBundle;

import org.junit.Ignore;

/**
 * 
 * @author clintz
 * 
 */
public class ErrorsBundleTest
{
	@Ignore
	public void testBundle()
	{

		ResourceBundle errors = ResourceBundle.getBundle("com.talool.core.ErrorsBundle");

		String error = (String) errors.getObject("EMAIL_ALREADY_EXISTS");
		System.out.println(error);

	}

}
