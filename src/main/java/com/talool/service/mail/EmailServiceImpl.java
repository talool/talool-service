package com.talool.service.mail;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Customer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.MerchantAccount;
import com.talool.core.gift.EmailGift;
import com.talool.core.service.EmailService;
import com.talool.core.service.ServiceException;
import com.talool.domain.CustomerImpl;
import com.talool.service.ServiceConfig;

/**
 * 
 * @author clintz
 * 
 */
public class EmailServiceImpl implements EmailService
{

	private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

	private static EmailServiceImpl instance;

	private final Properties mailProperties;

	private EmailServiceImpl()
	{
		mailProperties = new Properties();
		mailProperties.put("mail.smtp.auth", ServiceConfig.get().getSmtpAuth());
		mailProperties.put("mail.smtp.starttls.enable", ServiceConfig.get().getSmtpStartTtlsEnable());
		mailProperties.put("mail.smtp.host", ServiceConfig.get().getSmtpHost());
		mailProperties.put("mail.smtp.port", ServiceConfig.get().getSmtpPort());
	};

	public static synchronized EmailServiceImpl createInstance() throws IOException
	{
		if (instance == null)
		{
			instance = new EmailServiceImpl();
		}

		return instance;
	}

	// @Override
	// public void sendCustomerRegistrationEmail(final Customer customer) throws
	// ServiceException
	// {
	// try
	// {
	// sendEmail(ServiceConfig.get().getRegistrationSubj(), customer.getEmail(),
	// ServiceConfig.get().getMailFrom(),
	// FreemarkerUtil.get().renderRegistrationEmail(customer));
	//
	// if (LOG.isDebugEnabled())
	// {
	// LOG.debug("Registration email successfully sent to " +
	// customer.getEmail());
	// }
	//
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// throw new ServiceException(e.getLocalizedMessage(), e);
	// }
	// catch (TemplateException e)
	// {
	// e.printStackTrace();
	// throw new ServiceException(ErrorCode.MAIL_TEMPLATE_NOT_FOUND, e);
	// }
	//
	// }

	// @Override
	// public void sendPasswordRecoveryEmail(final Customer customer) throws
	// ServiceException
	// {
	// try
	// {
	// sendEmail(ServiceConfig.get().getPasswordRecoverySubj(),
	// customer.getEmail(), ServiceConfig.get().getMailFrom(),
	// FreemarkerUtil.get().renderPasswordRecoveryEmail(customer));
	//
	// if (LOG.isDebugEnabled())
	// {
	// LOG.debug("Password recovery email successfully sent to " +
	// customer.getEmail());
	// }
	//
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// throw new ServiceException(e.getLocalizedMessage(), e);
	// }
	// catch (TemplateException e)
	// {
	// e.printStackTrace();
	// throw new ServiceException(ErrorCode.MAIL_TEMPLATE_NOT_FOUND, e);
	// }
	//
	// }

	// @Override
	// public void sendEmail(final String subject, final String recipient, final
	// String from, final String messageBody)
	// {
	// Authenticator auth = null;
	//
	// if (ServiceConfig.get().getMailUsername() != null)
	// {
	// auth = new javax.mail.Authenticator()
	// {
	// protected PasswordAuthentication getPasswordAuthentication()
	// {
	// return new PasswordAuthentication(ServiceConfig.get().getMailUsername(),
	// ServiceConfig.get().getMailPassword());
	// }
	// };
	// }
	//
	// Session session = Session.getInstance(mailProperties, auth);
	//
	// try
	// {
	// final MimeMessage message = new MimeMessage(session);
	// message.setFrom(new InternetAddress(from));
	// message.setRecipients(Message.RecipientType.TO,
	// InternetAddress.parse(recipient));
	// message.setSubject(subject);
	// message.setText(messageBody, "utf-8", "html");
	//
	// Transport.send(message);
	//
	// }
	// catch (MessagingException e)
	// {
	// throw new RuntimeException(e);
	// }
	//
	// }

	public static void main(String args[]) throws IOException, ServiceException
	{
		ServiceConfig serviceConfig = ServiceConfig.createInstance("/etc/talool/service.properties");
		EmailServiceImpl emailService = EmailServiceImpl.createInstance();
		FreemarkerUtil.createInstance();

		// emailService.sendEmail("Talool Test Message",
		// "chris@talool.com","noreply@talool.com", "Standalone test email");

		Customer customer = new CustomerImpl();
		customer.setEmail("christopher.justin@gmail.com");
		// emailService.sendCustomerRegistrationEmail(customer);

	}

	// @Override
	// public void sendGiftEmail(final EmailGift gift) throws ServiceException
	// {
	// try
	// {
	// sendEmail(ServiceConfig.get().getGiftSubj(), gift.getToEmail(),
	// ServiceConfig.get().getMailFrom(),
	// FreemarkerUtil.get().renderGiftEmail(gift));
	//
	// if (LOG.isDebugEnabled())
	// {
	// LOG.debug("Gift email successfully sent to " + gift.getToEmail());
	// }
	//
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// throw new ServiceException(e.getLocalizedMessage(), e);
	// }
	// catch (TemplateException e)
	// {
	// e.printStackTrace();
	// throw new ServiceException(ErrorCode.MAIL_TEMPLATE_NOT_FOUND, e);
	// }
	//
	// }

	@Override
	public void sendPasswordRecoveryEmail(EmailRequestParams<Customer> emailRequestParams) throws ServiceException
	{
		throw new ServiceException("UNSUPPORTED FUNCTION");

	}

	@Override
	public void sendGiftEmail(EmailRequestParams<EmailGift> emailRequestParams) throws ServiceException
	{
		throw new ServiceException("UNSUPPORTED FUNCTION");

	}

	@Override
	public void sendMerchantAccountEmail(EmailRequestParams<MerchantAccount> emailRequestParams) throws ServiceException
	{
		throw new ServiceException("UNSUPPORTED FUNCTION");

	}

	@Override
	public void sendEmail(EmailRequest<?> emailRequest) throws ServiceException
	{
		throw new ServiceException("UNSUPPORTED FUNCTION");
	}

	@Override
	public void sendTrackingCodeEmail(EmailRequestParams<EmailTrackingCodeEntity> emailRequestParams) throws ServiceException
	{
		throw new ServiceException("UNSUPPORTED FUNCTION");

	}

	@Override
	public void sendGiftEmail(final EmailRequestParams<EmailGift> emailRequestParams, final String emailCategory)
			throws ServiceException
	{
		throw new ServiceException("UNSUPPORTED FUNCTION");
	}

	@Override
	public void sendDealOfferPurchaseJobEmail(
			EmailRequestParams<DealOfferPurchase> emailRequestParams, final String emailCategory)
			throws ServiceException {
		throw new ServiceException("UNSUPPORTED FUNCTION");
		
	}

}
