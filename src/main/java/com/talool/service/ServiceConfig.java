package com.talool.service;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Service configuration for Talool Java services
 * 
 * @author clintz
 * 
 */
public class ServiceConfig extends PropertiesConfiguration
{
	private static ServiceConfig instance;

	private static final String SMTP_AUTH = "mail.smtp.auth";
	private static final String SMPT_START_TTLS = "mail.smtp.starttls.enable";
	private static final String SMPT_HOST = "mail.smtp.host";
	private static final String SMPT_PORT = "mail.smtp.port";
	private static final String MAIL_USERNAME = "mail.username";
	private static final String MAIL_PASSWORD = "mail.password";
	private static final String MAIL_FROM = "mail.from";
	private static final String TEMPLATE_DIR = "template.dir";
	private static final String REGISTRATION_TEMPLATE = "registration.template";
	private static final String REGISTRATION_SUBJ = "registration.subject";
	private static final String PW_RECOVERY_TEMPLATE = "pwrecovery.template";
	private static final String PW_RECOVERY_SUBJ = "pwrecovery.subject";
	private static final String PW_RECOVERY_LINK = "pwrecovery.link";
	private static final String GIFT_TEMPLATE = "gift.template";
	private static final String GIFT_SUBJECT = "gift.subject";
	private static final String GIFT_LINK = "gift.link";
	private static final String CONSUMERS_LINK = "consumers.link";
	private static final String TAG_CACHE_REFRESH_IN_SECS = "tag.cache.refresh.in.secs";

	private static final String BRAINTREE_SANDBOX_ENABLED = "braintree.sandbox.enabled";

	private ServiceConfig(String file) throws ConfigurationException
	{
		super(file);
	}

	public static ServiceConfig get()
	{
		return instance;
	}

	public boolean isBraintreeSandboxEnabled()
	{
		return getBoolean(BRAINTREE_SANDBOX_ENABLED);
	}

	public int getTagCacheRefreshInSecs()
	{
		return getInt(TAG_CACHE_REFRESH_IN_SECS);
	}

	public String getSmtpAuth()
	{
		return getString(SMTP_AUTH);
	}

	public String getSmtpStartTtlsEnable()
	{
		return getString(SMPT_START_TTLS);
	}

	public String getSmtpHost()
	{
		return getString(SMPT_HOST);
	}

	public String getSmtpPort()
	{
		return getString(SMPT_PORT);
	}

	public String getMailUsername()
	{
		return getString(MAIL_USERNAME);
	}

	public String getMailPassword()
	{
		return getString(MAIL_PASSWORD);
	}

	public String getMailFrom()
	{
		return getString(MAIL_FROM);
	}

	public String getTemplateDir()
	{
		return getString(TEMPLATE_DIR);
	}

	public String getRegistrationTemplate()
	{
		return getString(REGISTRATION_TEMPLATE);
	}

	public String getRegistrationSubj()
	{
		return getString(REGISTRATION_SUBJ);
	}

	public String getPasswordRecoveryTemplate()
	{
		return getString(PW_RECOVERY_TEMPLATE);
	}

	public String getPasswordRecoverySubj()
	{
		return getString(PW_RECOVERY_SUBJ);
	}

	public String getPasswordRecoveryLink()
	{
		return getString(PW_RECOVERY_LINK);
	}

	public String getGiftTemplate()
	{
		return getString(GIFT_TEMPLATE);
	}

	public String getGiftLink()
	{
		return getString(GIFT_LINK);
	}

	public String getConsumersLink()
	{
		return getString(CONSUMERS_LINK);
	}

	public String getGiftSubj()
	{
		return getString(GIFT_SUBJECT);
	}

	public static synchronized ServiceConfig createInstance(final String propertyFile)
	{
		if (instance == null)
		{
			try
			{
				instance = new ServiceConfig(propertyFile);
			}
			catch (ConfigurationException ex)
			{
				if (instance == null)
				{
					throw new AssertionError(ex.getLocalizedMessage());
				}
			}
		}

		return instance;
	}
}
