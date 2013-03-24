package com.talool.bundles;

import java.util.ListResourceBundle;

/**
 * 
 * @author clintz
 * 
 */
public class ErrorsBundle extends ListResourceBundle
{
	private Object[][] errorContents = {
			{ "EMAIL_ALREADY_EXISTS", "The email address already exsists" },
			{ "INVALID_PASSWORD", "Invalid password" },

	};

	@Override
	protected Object[][] getContents()
	{
		return errorContents;
	}
}
