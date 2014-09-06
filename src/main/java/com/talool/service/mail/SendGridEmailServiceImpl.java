package com.talool.service.mail;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Customer;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantCodeGroup;
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
 *         TODO Highly recommended to implement a GSON based implementation similiar to the google projecy
 *         https://github.com/sendgrid/sendgrid-google-java/
 * 
 */
public class SendGridEmailServiceImpl implements EmailService
{
	private static final Logger LOG = LoggerFactory.getLogger(SendGridEmailServiceImpl.class);
	private static SendGridEmailServiceImpl instance;

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

	public void sendEmail(final EmailRequest<?> sendGridParams) throws ServiceException
	{
		String renderedBody = null;

		final String userName = ServiceConfig.get().getMailUsername();
		final String password = ServiceConfig.get().getMailPassword();

		if (userName == null || password == null)
		{
			throw new ServiceException("Sendgrid user/pass not defined ");
		}

		try
		{
			renderedBody = sendGridParams.getEmailParams().getBody() == null ? getRenderedEmailBody(sendGridParams.getTemplateType(),
					sendGridParams.getEntity()) : sendGridParams.getEmailParams().getBody();
		}
		catch (IOException e)
		{
			new ServiceException("Problem with freemarker template " + sendGridParams.getTemplateType() + " :" + e.getLocalizedMessage(), e);
		}
		catch (TemplateException e)
		{
			throw new ServiceException(ErrorCode.MAIL_TEMPLATE_NOT_FOUND, e);
		}

		final SendgridClient sendgrid = new SendgridClient(userName, password);

		sendgrid.addTo(sendGridParams.getEmailParams().getRecipient());
		sendgrid.setFrom(sendGridParams.getEmailParams().getFrom());
		sendgrid.setSubject(sendGridParams.getEmailParams().getSubject());
		sendgrid.setFromName(sendGridParams.getEmailParams().getFromName());

		sendgrid.setHtml(renderedBody);

		if (sendGridParams.getCategory() != null)
		{
			sendgrid.setCategory(sendGridParams.getCategory());
		}

		if (CollectionUtils.isNotEmpty(sendGridParams.getUniqueStringVals()))
		{
			for (SimpleEntry<String, String> keyVal : sendGridParams.getUniqueStringVals())
			{
				sendgrid.addUniqueArgument(keyVal.getKey(), keyVal.getValue());
			}
		}

		if (CollectionUtils.isNotEmpty(sendGridParams.getUniqueIntVals()))
		{
			for (SimpleEntry<String, Integer> keyVal : sendGridParams.getUniqueIntVals())
			{
				sendgrid.addUniqueArgument(keyVal.getKey(), keyVal.getValue());
			}
		}

		try
		{
			sendgrid.buildSmtpApiHeader().sendMail();

			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Success sending email from: %s to: %s subj: %s", sendGridParams.getEmailParams().getFrom(),
						sendGridParams.getEmailParams().getRecipient(), sendGridParams.getEmailParams().getSubject()));
			}

		}
		catch (ServiceException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new ServiceException(String.format("Failed sending email from: %s to: %s subj: %s", sendGridParams.getEmailParams()
					.getRecipient(), sendGridParams.getEmailParams().getFrom(), sendGridParams.getEmailParams().getSubject()), e);
		}

	}

	@Override
	public void sendPasswordRecoveryEmail(final EmailRequestParams<Customer> emailRequestParams) throws ServiceException
	{

		final Customer customer = emailRequestParams.getEntity();
		final EmailParams emailParams = new EmailParams(ServiceConfig.get().getPasswordRecoverySubj(), customer.getEmail(), ServiceConfig
				.get().getMailFrom());

		final EmailRequest<Customer> sendGridParams = new EmailRequest<Customer>();
		sendGridParams.setCategory(EmailCategory.PasswordRecovery.toString());
		sendGridParams.setEmailParams(emailParams);
		sendGridParams.setEntity(customer);
		sendGridParams.setTemplateType(TemplateType.ResetPassword);

		sendEmail(sendGridParams);

	}

	/**
	 * Gets freemarker template without throwing exception. If exception is throw it will be in the TemplateResponse
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
				emailBody = FreemarkerUtil.get().renderPasswordRecoveryEmail((Customer) obj);
				break;

			case MerchantRegistration:
				emailBody = FreemarkerUtil.get().renderMerchantRegistrationEmail((MerchantAccount) obj);
				break;

			case TrackingCode:
				emailBody = FreemarkerUtil.get().renderTrackingCodeEmail((EmailTrackingCodeEntity) obj);
				break;

		}

		return emailBody;

	}

	@Override
	public void sendGiftEmail(final EmailRequestParams<EmailGift> emailRequestParams) throws ServiceException
	{
		sendGiftEmail(emailRequestParams, EmailCategory.Gift.toString());
	}

	@Override
	public void sendMerchantAccountEmail(EmailRequestParams<MerchantAccount> emailRequestParams) throws ServiceException
	{
		final EmailParams emailParams = new EmailParams(ServiceConfig.get().getMerchantRegistrationSubj(), emailRequestParams.getEntity()
				.getEmail(), ServiceConfig.get().getMailFrom());

		final EmailRequest<MerchantAccount> sendGridParams = new EmailRequest<MerchantAccount>();
		sendGridParams.setEmailParams(emailParams);
		sendGridParams.setCategory(EmailCategory.Merchant.toString());
		sendGridParams.setTemplateType(TemplateType.MerchantRegistration);
		sendGridParams.setEntity(emailRequestParams.getEntity());

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

			sendgrid.addCategory("cat1").addCategory("cat2").addUniqueArgument("isSpam", false)
					.addUniqueArgument("giftUuid", UUID.randomUUID().toString()).addUniqueArgument("count", 182881);

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

	@Override
	public void sendTrackingCodeEmail(EmailRequestParams<EmailTrackingCodeEntity> emailRequestParams) throws ServiceException
	{

		EmailTrackingCodeEntity entity = emailRequestParams.getEntity();
		MerchantCodeGroup codeGroup = entity.codeGroup;
		Merchant fundraiser = codeGroup.getMerchant();

		String emailSubject = ServiceConfig.get().getAndReplace(ServiceConfig.PUBLISHER_CODE_SUBJ, "@fundraiser", fundraiser.getName());
		String emailAddress = codeGroup.getCodeGroupNotes();

		EmailParams emailParams = new EmailParams(emailSubject, emailAddress, ServiceConfig.get().getMailFrom());
		emailParams.setFromName("Talool Fundraiser");

		final EmailRequest<EmailTrackingCodeEntity> sendGridParams = new EmailRequest<EmailTrackingCodeEntity>();
		sendGridParams.setEmailParams(emailParams);
		sendGridParams.setCategory(EmailCategory.MerchantCodeGroup.toString());
		sendGridParams.setTemplateType(TemplateType.TrackingCode);
		sendGridParams.setEntity(entity);

		sendEmail(sendGridParams);

	}

	@Override
	public void sendGiftEmail(final EmailRequestParams<EmailGift> emailRequestParams, final String emailCategory)
			throws ServiceException
	{
		final EmailParams emailParams = new EmailParams(ServiceConfig.get().getGiftSubj(), emailRequestParams.getEntity().getToEmail(),
				ServiceConfig.get().getMailFrom());

		final EmailRequest<EmailGift> sendGridParams = new EmailRequest<EmailGift>();
		sendGridParams.setEmailParams(emailParams);
		sendGridParams.setCategory(emailCategory);
		sendGridParams.setTemplateType(TemplateType.Gift);
		sendGridParams.setEntity(emailRequestParams.getEntity());
		sendGridParams.addUniqueArg("emailGiftId", emailRequestParams.getEntity().getId().toString());

		sendEmail(sendGridParams);
	}
}
