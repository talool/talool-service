package com.talool.service.mail;

import java.io.IOException;

import jp.co.flect.sendgrid.SendGridClient;
import jp.co.flect.sendgrid.SendGridException;
import jp.co.flect.sendgrid.model.WebMail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Customer;
import com.talool.core.gift.EmailGift;
import com.talool.core.service.EmailService;
import com.talool.core.service.ServiceException;
import com.talool.service.ErrorCode;
import com.talool.service.ServiceConfig;

import freemarker.template.TemplateException;

public class SendGridEmailServiceImpl implements EmailService
{

	private static final Logger LOG = LoggerFactory.getLogger(SendGridEmailServiceImpl.class);

	private static SendGridEmailServiceImpl instance;

	private SendGridEmailServiceImpl()
	{
		/*
		 * Make sure the SendGrid credentials are set in the Service Config u:
		 * vince@talool.com p: 321Abc990
		 */
	}

	public static synchronized SendGridEmailServiceImpl createInstance() throws IOException
	{
		if (instance == null)
		{
			instance = new SendGridEmailServiceImpl();
		}

		return instance;
	}

	@Override
	public void sendEmail(String subject, String recipient, String from,
			String messageBody) throws ServiceException
	{
		final String userName = ServiceConfig.get().getMailUsername();
		final String password = ServiceConfig.get().getMailPassword();

		if (userName == null || password == null)
		{
			throw new ServiceException("Sendgrid user/pass not defined ");
		}

		final SendGridClient sendgrid = new SendGridClient(ServiceConfig.get().getMailUsername(), ServiceConfig.get().getMailPassword());
		final WebMail mail = new WebMail();
		mail.setTo(recipient);
		// sendgrid.addToName("");
		mail.setFrom(from);
		// sendgrid.addFromName("");
		mail.setSubject(subject);
		mail.setHtml(messageBody);

		try
		{
			sendgrid.mail(mail);
		}
		catch (IOException e)
		{
			throw new ServiceException("Failed sending sendgrid email: " + e.getLocalizedMessage(), e);
		}
		catch (SendGridException e)
		{
			throw new ServiceException("Failed sending sendgrid email - errors : " + e.getLocalizedMessage(), e);
		}

	}

	@Override
	public void sendCustomerRegistrationEmail(Customer customer)
			throws ServiceException
	{
		try
		{
			sendEmail(ServiceConfig.get().getRegistrationSubj(), customer.getEmail(), ServiceConfig.get().getMailFrom(),
					FreemarkerUtil.get().renderRegistrationEmail(customer));

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Registration email successfully sent to " + customer.getEmail());
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		catch (TemplateException e)
		{
			e.printStackTrace();
			throw new ServiceException(ErrorCode.MAIL_TEMPLATE_NOT_FOUND, e);
		}

	}

	@Override
	public void sendPasswordRecoveryEmail(Customer customer)
			throws ServiceException
	{
		try
		{
			sendEmail(ServiceConfig.get().getPasswordRecoverySubj(), customer.getEmail(), ServiceConfig.get().getMailFrom(),
					FreemarkerUtil.get().renderPasswordRecoveryEmail(customer));

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Password recovery email successfully sent to " + customer.getEmail());
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		catch (TemplateException e)
		{
			e.printStackTrace();
			throw new ServiceException(ErrorCode.MAIL_TEMPLATE_NOT_FOUND, e);
		}

	}

	@Override
	public void sendGiftEmail(EmailGift gift) throws ServiceException
	{
		try
		{
			sendEmail(ServiceConfig.get().getGiftSubj(), gift.getToEmail(), ServiceConfig.get().getMailFrom(),
					FreemarkerUtil.get().renderGiftEmail(gift));

			// if (LOG.isDebugEnabled())
			// {
			LOG.debug("Gift email successfully sent to " + gift.getToEmail());
			// }

		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		catch (TemplateException e)
		{
			e.printStackTrace();
			throw new ServiceException(ErrorCode.MAIL_TEMPLATE_NOT_FOUND, e);
		}
	}

}
