package com.talool.service.mail;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sendgrid.SendGrid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.talool.core.service.ServiceException;

/**
 * A wrapper around the very unuseful Sendgrid Java class.
 * 
 * buildSmtpApiHeader() must be called after adding categories and uniqueArgs
 * 
 * @author clintz
 * 
 */
public class SendgridClient extends SendGrid
{
	private static final Logger LOG = LoggerFactory.getLogger(SendgridClient.class);

	private static final String SMTP_API_HEADER = "x-smtpapi";

	private static final String UNIQUE_ARG_OBJ_NAME = "unique_args";
	private static final String CATEGORY_OBJ_NAME = "category";

	private JsonObject xsmtpheaderList = new JsonObject();

	private static class SendGridResult
	{
		String message;
		List<String> errors;
	}

	public SendgridClient(String username, String password)
	{
		super(username, password);
	}

	public SendgridClient addCategory(final String category)
	{
		if (true == xsmtpheaderList.has(CATEGORY_OBJ_NAME))
		{
			((JsonArray) this.xsmtpheaderList.get(CATEGORY_OBJ_NAME)).add(new JsonPrimitive(category));
		}
		else
		{
			this.setCategory(category);
		}

		return this;
	}

	public SendgridClient setCategory(final String category)
	{
		final JsonArray jsonCategory = new JsonArray();
		jsonCategory.add(new JsonPrimitive(category));
		xsmtpheaderList.add(CATEGORY_OBJ_NAME, jsonCategory);

		return this;
	}

	public SendgridClient buildSmtpApiHeader()
	{
		final Gson gson = new Gson();

		if (xsmtpheaderList.get(CATEGORY_OBJ_NAME) != null || xsmtpheaderList.get(UNIQUE_ARG_OBJ_NAME) != null)
		{
			addHeader(SMTP_API_HEADER, gson.toJson(xsmtpheaderList));
		}

		return this;
	}

	private void checkUniqueArgs()
	{
		if (this.xsmtpheaderList.has(UNIQUE_ARG_OBJ_NAME) == false)
		{
			this.xsmtpheaderList.add(UNIQUE_ARG_OBJ_NAME, new JsonObject());
		}
	}

	public SendgridClient addUniqueArgument(final String key, final String value)
	{
		checkUniqueArgs();
		((JsonObject) this.xsmtpheaderList.get(UNIQUE_ARG_OBJ_NAME)).addProperty(key, value);
		return this;
	}

	public SendgridClient addUniqueArgument(final String key, final Integer value)
	{
		checkUniqueArgs();
		((JsonObject) this.xsmtpheaderList.get(UNIQUE_ARG_OBJ_NAME)).addProperty(key, value);
		return this;
	}

	public SendgridClient addUniqueArgument(final String key, final Float value)
	{
		checkUniqueArgs();
		((JsonObject) this.xsmtpheaderList.get(UNIQUE_ARG_OBJ_NAME)).addProperty(key, value);
		return this;
	}

	public SendgridClient addUniqueArgument(final String key, final Boolean value)
	{
		checkUniqueArgs();
		((JsonObject) this.xsmtpheaderList.get(UNIQUE_ARG_OBJ_NAME)).addProperty(key, value);
		return this;
	}

	public SendgridClient addUniqueArgument(final String key, final Double value)
	{
		checkUniqueArgs();
		((JsonObject) this.xsmtpheaderList.get(UNIQUE_ARG_OBJ_NAME)).addProperty(key, value);
		return this;
	}

	@Override
	/**
	 * Sadly the Sendgrid Java API sucks.  This method is not supported .  Please use sendMail() 
	 */
	public String send()
	{
		throw new UnsupportedOperationException();
	}

	public void sendMail() throws ServiceException
	{
		final String result = super.send();

		if (StringUtils.containsIgnoreCase(result, "errors"))
		{
			final Gson gson = new GsonBuilder().create();
			final SendGridResult sgResult = gson.fromJson(result, SendGridResult.class);

			final StringBuilder sb = new StringBuilder();
			for (final String err : sgResult.errors)
			{
				sb.append(err).append("; ");
			}

			throw new ServiceException("Failed sending email :" + sb.toString());

		}

	}
}
