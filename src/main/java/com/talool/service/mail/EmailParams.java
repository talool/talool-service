package com.talool.service.mail;

/**
 * Email parameters
 * 
 * @author clintz
 * 
 */
public class EmailParams
{

	private String subject;
	private String recipient;
	private String from;
	private String body;
	private String fromName;
	
	private static final String talool = "Talool";

	public EmailParams(String subject, String recipient, String from, String body)
	{
		super();
		this.subject = subject;
		this.recipient = recipient;
		this.from = from;
		this.body = body;
		this.fromName = talool;
	}

	public EmailParams(String subject, String recipient, String from)
	{
		super();
		this.subject = subject;
		this.recipient = recipient;
		this.from = from;
		this.fromName = talool;
	}

	public String getSubject()
	{
		return subject;
	}

	public String getRecipient()
	{
		return recipient;
	}

	public String getFrom()
	{
		return from;
	}

	public String getBody()
	{
		return body;
	}
	
	public String getFromName()
	{
		return fromName;
	}
	
	public void setFromName(String name)
	{
		fromName = name;
	}
}
