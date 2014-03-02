package com.talool.service.mail;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Customer;
import com.talool.core.gift.EmailGift;
import com.talool.core.service.EmailService;
import com.talool.core.service.ServiceException;
import com.talool.service.ErrorCode;
import com.talool.service.ServiceConfig;
import com.talool.service.mail.FreemarkerUtil.TemplateType;

import freemarker.template.TemplateException;

/**
 * 
 * @author dmccuen, clintz
 * 
 *         TODO Highly recommended to implement a GSON based implementation
 *         similiar to the google projecy
 *         https://github.com/sendgrid/sendgrid-google-java/
 * 
 */
public class SendGridEmailServiceImpl implements EmailService
{
	private static final Logger LOG = LoggerFactory.getLogger(SendGridEmailServiceImpl.class);
	private static SendGridEmailServiceImpl instance;

	private class SendGridParams<T>
	{
		EmailParams emailParams;
		String category;
		TemplateType templateType;
		T entity;
		List<SimpleEntry<String, Integer>> uniqueIntVals;
		List<SimpleEntry<String, String>> uniqueStringVals;

		public SendGridParams addUniqueArg(String key, String val)
		{
			if (uniqueStringVals == null)
			{
				uniqueStringVals = new ArrayList<SimpleEntry<String, String>>();
			}
			uniqueStringVals.add(new SimpleEntry<String, String>(key, val));
			return this;
		}

		public SendGridParams addUniqueArg(String key, Integer val)
		{
			if (uniqueIntVals == null)
			{
				uniqueIntVals = new ArrayList<SimpleEntry<String, Integer>>();
			}
			uniqueIntVals.add(new SimpleEntry<String, Integer>(key, val));
			return this;
		}

	}

	@SuppressWarnings("unused")
	private class MailResult
	{
		boolean success = false;

		long totalTimeInMillis;
	}

	private SendGridEmailServiceImpl()
	{}

	public static synchronized SendGridEmailServiceImpl createInstance() throws IOException
	{
		if (instance == null)
		{
			instance = new SendGridEmailServiceImpl();
		}

		return instance;
	}

	public MailResult sendEmail(final SendGridParams<?> sendGridParams) throws ServiceException
	{

		String renderedBody = null;
		final MailResult mailResult = new MailResult();

		final StopWatch watch = new StopWatch();
		watch.start();

		final String userName = ServiceConfig.get().getMailUsername();
		final String password = ServiceConfig.get().getMailPassword();

		if (userName == null || password == null)
		{
			throw new ServiceException("Sendgrid user/pass not defined ");
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Sending email " + sendGridParams.emailParams.toString());
		}

		try
		{
			renderedBody = getRenderedEmailBody(sendGridParams.templateType, sendGridParams.entity);
		}
		catch (IOException e)
		{
			new ServiceException("Problem with freemarker template " + sendGridParams.templateType + " :" + e.getLocalizedMessage(), e);
		}
		catch (TemplateException e)
		{
			throw new ServiceException(ErrorCode.MAIL_TEMPLATE_NOT_FOUND, e);
		}

		final SendgridClient sendgrid = new SendgridClient(userName, password);

		sendgrid.addTo(sendGridParams.emailParams.getRecipient());
		sendgrid.setFrom(sendGridParams.emailParams.getFrom());
		sendgrid.setSubject(sendGridParams.emailParams.getSubject());

		sendgrid.setHtml(renderedBody);

		if (sendGridParams.category != null)
		{
			sendgrid.setCategory(sendGridParams.category);
		}

		if (CollectionUtils.isNotEmpty(sendGridParams.uniqueStringVals))
		{
			for (SimpleEntry<String, String> keyVal : sendGridParams.uniqueStringVals)
			{
				sendgrid.addUniqueArgument(keyVal.getKey(), keyVal.getValue());
			}
		}

		if (CollectionUtils.isNotEmpty(sendGridParams.uniqueIntVals))
		{
			for (SimpleEntry<String, Integer> keyVal : sendGridParams.uniqueIntVals)
			{
				sendgrid.addUniqueArgument(keyVal.getKey(), keyVal.getValue());
			}
		}

		try
		{
			sendgrid.buildSmtpApiHeader().send();
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Success sending " + sendGridParams.templateType + ", " + sendGridParams.emailParams.getRecipient());
			}
			mailResult.success = true;
		}
		catch (Exception e)
		{
			throw new ServiceException("Email failed " + sendGridParams.templateType + ", " + sendGridParams.emailParams.getRecipient() + ": "
					+ e.getLocalizedMessage(),
					e);
		}
		finally
		{
			watch.stop();
			mailResult.totalTimeInMillis = watch.getTime();
		}

		return mailResult;

	}

	@Override
	public void sendCustomerRegistrationEmail(final EmailRequestParams<Customer> emailRequestParams)
			throws ServiceException
	{

		final EmailParams emailParams = new EmailParams(ServiceConfig.get().getRegistrationSubj(),
				emailRequestParams.getEntity().getEmail(), ServiceConfig.get().getMailFrom());

		final SendGridParams<Customer> sendGridParams = new SendGridParams<Customer>();
		sendGridParams.emailParams = emailParams;
		sendGridParams.category = EmailCategory.Registration.toString();
		sendGridParams.templateType = TemplateType.Registration;

		sendEmail(sendGridParams);

	}

	@Override
	public void sendPasswordRecoveryEmail(final EmailRequestParams<Customer> emailRequestParams)
			throws ServiceException
	{

		final Customer customer = emailRequestParams.getEntity();
		final EmailParams emailParams = new EmailParams(ServiceConfig.get().getPasswordRecoverySubj(),
				customer.getEmail(), ServiceConfig.get().getMailFrom());

		final SendGridParams<Customer> sendGridParams = new SendGridParams<Customer>();
		sendGridParams.category = EmailCategory.PasswordRecovery.toString();
		sendGridParams.emailParams = emailParams;
		sendGridParams.entity = customer;
		sendGridParams.templateType = TemplateType.ResetPassword;

		sendEmail(sendGridParams);

	}

	/**
	 * Gets freemarker template without throwing exception. If exception is throw
	 * it will be in the TemplateResponse
	 * 
	 * @param type
	 * @param obj
	 * @return
	 */
	private String getRenderedEmailBody(final TemplateType type, final Object obj) throws IOException, TemplateException
	{
		String emailBody = null;

		switch (type)
		{
			case Gift:
				emailBody = FreemarkerUtil.get().renderGiftEmail((EmailGift) obj);
				break;
			case Registration:
				emailBody = FreemarkerUtil.get().renderRegistrationEmail((Customer) obj);
				break;

			case ResetPassword:
				emailBody = FreemarkerUtil.get().renderRegistrationEmail((Customer) obj);
				break;

		}

		return emailBody;

	}

	@Override
	public void sendGiftEmail(final EmailRequestParams<EmailGift> emailRequestParams) throws ServiceException
	{
		final EmailParams emailParams = new EmailParams(ServiceConfig.get().getRegistrationSubj(),
				emailRequestParams.getEntity().getToEmail(), ServiceConfig.get().getMailFrom());

		final SendGridParams<EmailGift> sendGridParams = new SendGridParams<EmailGift>();
		sendGridParams.emailParams = emailParams;
		sendGridParams.category = EmailCategory.Gift.toString();
		sendGridParams.templateType = TemplateType.Gift;
		sendGridParams.entity = emailRequestParams.getEntity();
		sendGridParams.addUniqueArg("emailGiftId", emailRequestParams.getEntity().getId().toString());

		sendEmail(sendGridParams);

	}

	public static void main(String args[])
	{
		try
		{
			// testing a bad email
			final SendgridClient sendgrid = new SendgridClient("vince@talool.com", "321Abc990");
			sendgrid.addTo("chriasdqsdasds@talool.com");
			sendgrid.setFrom("chris@talool.com");
			sendgrid.setSubject("test email");
			sendgrid.setText("THis is a test email");

			sendgrid.addCategory("cat1").addCategory("cat2").addUniqueArgument("isSpam", false).
					addUniqueArgument("giftUuid", UUID.randomUUID().toString()).addUniqueArgument("count", 182881);

			// must build the header!
			sendgrid.buildSmtpApiHeader();

			// final String smtpApiHeader = getApiHeader(values, "gift");
			// sendgrid.addHeader("x-smtpapi", smtpApiHeader);
			sendgrid.send();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}
}
