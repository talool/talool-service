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

	public static final String SMTP_AUTH = "mail.smtp.auth";
	public static final String SMPT_START_TTLS = "mail.smtp.starttls.enable";
	public static final String SMPT_HOST = "mail.smtp.host";
	public static final String SMPT_PORT = "mail.smtp.port";
	public static final String MAIL_USERNAME = "mail.username";
	public static final String MAIL_PASSWORD = "mail.password";
	public static final String MAIL_FROM = "mail.from";
	public static final String TEMPLATE_DIR = "template.dir";
	public static final String REGISTRATION_TEMPLATE = "registration.template";
	public static final String REGISTRATION_SUBJ = "registration.subject";
	public static final String PW_RECOVERY_TEMPLATE = "pwrecovery.template";
	public static final String PW_RECOVERY_SUBJ = "pwrecovery.subject";
	public static final String PW_RECOVERY_LINK = "pwrecovery.link";
	public static final String FUNDRAISER_TEMPLATE = "fundraiser.template";
	public static final String INSTALL_LINK = "install.link";
	public static final String GIFT_TEMPLATE = "gift.template";
	public static final String GIFT_SUBJECT = "gift.subject";
	public static final String GIFT_LINK = "gift.link";
	public static final String CONSUMERS_LINK = "consumers.link";
	public static final String GIFT_RETURNED_LINK = "gift.returned.link";

	public static final String FUNDRAISER_SUBJECT = "fundraiser.subject";

	public static final String TAG_CACHE_REFRESH_IN_SECS = "tag.cache.refresh.in.secs";

	public static final String BRAINTREE_SANDBOX_ENABLED = "braintree.sandbox.enabled";
	public static final String BRAINTREE_MERCHANT_ID = "braintree.merchant.id";
	public static final String BRAINTREE_PUBLIC_KEY = "braintree.public.key";
	public static final String BRAINTREE_PRIVATE_KEY = "braintree.private.key";

	public static final String UPLOAD_DIR = "upload.dir";
	public static final String STATIC_LOGO_BASE_URL = "static.logo.base.url";
	public static final String IMAGE_MAGICK_PATH = "image.magick.path";

	public static final String STATSD_ENVIRONMENT_IS_PRODUCTION = "statsd.environment.is.production";

	// private static final String UPLOAD_LOGO_MAX_SIZE_BYTES =
	// "upload.logo.max.size.bytes";
	// private static final Integer DEFAULT_UPLOAD_LOGO_MAX_SIZE_BYTES = 10240;

	private ServiceConfig(String file) throws ConfigurationException
	{
		super(file);

	}

	public static ServiceConfig get()
	{
		return instance;
	}

	public String getFundraiserSubject()
	{
		return getString(FUNDRAISER_SUBJECT);
	}

	public String getBraintreePublicKey()
	{
		return getString(BRAINTREE_PUBLIC_KEY);
	}

	public String getGiftReturnedLink()
	{
		return getString(GIFT_RETURNED_LINK);
	}

	public String getBraintreeMerchantId()
	{
		return getString(BRAINTREE_MERCHANT_ID);
	}

	public String getBraintreePrivateKey()
	{
		return getString(BRAINTREE_PRIVATE_KEY);
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

	public String getFundraiserTemplate()
	{
		return getString(FUNDRAISER_TEMPLATE);
	}

	public String getInstallLink()
	{
		return getString(INSTALL_LINK);
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

	public String getStaticLogoBaseUrl()
	{
		return getString(STATIC_LOGO_BASE_URL);
	}

	public String getUploadDir()
	{
		return getString(UPLOAD_DIR);
	}

	public String getImageMagickPath()
	{
		return getString(IMAGE_MAGICK_PATH);
	}

	public boolean isStatsDEnvironmentProduction()
	{
		return getBoolean(STATSD_ENVIRONMENT_IS_PRODUCTION);
	}

	public String getAndReplace(final String key, final String propertyKey, final String propertyVal)
	{
		return getString(key).replaceFirst(propertyKey, propertyVal);
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
