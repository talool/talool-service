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
	public static final String MERCHANT_REGISTRATION_TEMPLATE = "registration.merchant.template";
	public static final String MERCHANT_REGISTRATION_SUBJ = "registration.merchant.subject";
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
	public static final String TALOOL_PUBLISHER_MERCHANT_ACCOUNT_ID = "talool.publisher.merchantaccount.id";

	public static final String FUNDRAISER_SUBJECT = "fundraiser.subject";

	public static final String TAG_CACHE_REFRESH_IN_SECS = "tag.cache.refresh.in.secs";

	public static final String BRAINTREE_SANDBOX_ENABLED = "braintree.sandbox.enabled";
	public static final String BRAINTREE_MERCHANT_ID = "braintree.merchant.id";
	public static final String BRAINTREE_PUBLIC_KEY = "braintree.public.key";
	public static final String BRAINTREE_PRIVATE_KEY = "braintree.private.key";
	public static final String BRAINTREE_MASTER_MERCHANT_ID = "braintree.master.merchant.account.id";

	public static final String UPLOAD_DIR = "upload.dir";
	public static final String STATIC_LOGO_BASE_URL = "static.logo.base.url";
	public static final String IMAGE_MAGICK_PATH = "image.magick.path";

	public static final String STATSD_ENVIRONMENT_IS_PRODUCTION = "statsd.environment.is.production";

	public static final String PUBLISHER_CODE_QUOTA = "publisher.code.generation.daily.quota";
	public static final String PUBLISHER_CODE_TEMPLATE = "publisher.code.template";
	public static final String PUBLISHER_CODE_SUBJ = "publisher.code.subject";

	public static final String MESSAGING_JOB_MANAGER_ACTIVE = "messaging.job.manager.active";
	public static final String MESSAGING_JOB_MANAGER_MAX_THREADS = "messaging.job.manager.max.threads";
	public static final String MESSAGING_JOB_MANAGER_MIN_THREADS = "messaging.job.manager.min.threads";
	public static final String MESSAGING_JOB_MANAGER_SLEEP_SECS = "messaging.job.manager.sleep.secs";
	public static final String MESSAGING_JOB_MANAGER_TASK_MAX_ATTEMPTS = "messaging.job.manager.task.max.attempts";
	public static final String MESSAGING_JOB_MANAGER_TASK_BATCH_SIZE = "messaging.job.manager.task.batch.size";
	
	public static final String DEAL_OFFER_PURCHASE_JOB_SUBJ = "purchase.job.subject";
	public static final String DEAL_OFFER_PURCHASE_JOB_TEMPLATE = "purchase.job.template";

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

	public String getBraintreeMasterMerchantId()
	{
		return getString(BRAINTREE_MASTER_MERCHANT_ID);
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

	public String getTrackingCodeTemplate()
	{
		return getString(PUBLISHER_CODE_TEMPLATE);
	}

	public String getTrackingCodeSubj()
	{
		return getString(PUBLISHER_CODE_SUBJ);
	}

	public String getMerchantRegistrationTemplate()
	{
		return getString(MERCHANT_REGISTRATION_TEMPLATE);
	}

	public String getMerchantRegistrationSubj()
	{
		return getString(MERCHANT_REGISTRATION_SUBJ);
	}
	
	public String getDealOfferPurchaseJobTemplate()
	{
		return getString(DEAL_OFFER_PURCHASE_JOB_TEMPLATE);
	}

	public String getDealOfferPurchaseJobSubj()
	{
		return getString(DEAL_OFFER_PURCHASE_JOB_SUBJ);
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

	public long getTaloolPublisherMerchantAccountId()
	{
		return getLong(TALOOL_PUBLISHER_MERCHANT_ACCOUNT_ID);
	}

	public long getPublisherCodeGenerationDailyQuota()
	{
		return getLong(PUBLISHER_CODE_QUOTA);
	}

	public String getAndReplace(final String key, final String propertyKey, final String propertyVal)
	{
		return getString(key).replaceFirst(propertyKey, propertyVal);
	}

	public boolean getMessagingJobManagerActive()
	{
		return getBoolean(MESSAGING_JOB_MANAGER_ACTIVE, false);
	}

	public int getMessagingJobManagerMaxThreads()
	{
		return getInteger(MESSAGING_JOB_MANAGER_MAX_THREADS, 0);
	}

	public int getMessagingJobManagerMinThreads()
	{
		return getInteger(MESSAGING_JOB_MANAGER_MIN_THREADS, 0);
	}

	public int getMessagingJobManagerSleepSecs()
	{
		return getInteger(MESSAGING_JOB_MANAGER_SLEEP_SECS, 0);
	}

	public int getMessagingJobManagerTaskMaxAttempts()
	{
		return getInteger(MESSAGING_JOB_MANAGER_TASK_MAX_ATTEMPTS, 3);
	}

	public int getMessagingJobManagerTaskBatchSize()
	{
		return getInteger(MESSAGING_JOB_MANAGER_TASK_BATCH_SIZE, 100);
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
