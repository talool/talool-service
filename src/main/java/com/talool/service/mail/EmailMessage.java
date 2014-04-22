package com.talool.service.mail;

/**
 * 
 * @author clintz
 * 
 */
public class EmailMessage
{
	private String subject;
	private String body;

	public EmailMessage(String subject, String body)
	{
		super();
		this.subject = subject;
		this.body = body;
	}

	public String getSubject()
	{
		return subject;
	}

	public String getBody()
	{
		return body;
	}

}
