package com.talool.service.mail;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import com.talool.service.mail.FreemarkerUtil.TemplateType;

/**
 * An email request payload object.
 * 
 * @author clintz
 * 
 * @param <T>
 */
public class EmailRequest<T>
{
	private EmailParams emailParams;
	private String category;
	private TemplateType templateType;
	private T entity;
	private List<SimpleEntry<String, Integer>> uniqueIntVals;
	private List<SimpleEntry<String, String>> uniqueStringVals;

	public EmailRequest addUniqueArg(String key, String val)
	{
		if (uniqueStringVals == null)
		{
			uniqueStringVals = new ArrayList<SimpleEntry<String, String>>();
		}
		uniqueStringVals.add(new SimpleEntry<String, String>(key, val));
		return this;
	}

	public EmailRequest addUniqueArg(String key, Integer val)
	{
		if (uniqueIntVals == null)
		{
			uniqueIntVals = new ArrayList<SimpleEntry<String, Integer>>();
		}
		uniqueIntVals.add(new SimpleEntry<String, Integer>(key, val));
		return this;
	}

	public EmailParams getEmailParams()
	{
		return emailParams;
	}

	public void setEmailParams(EmailParams emailParams)
	{
		this.emailParams = emailParams;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public TemplateType getTemplateType()
	{
		return templateType;
	}

	public void setTemplateType(TemplateType templateType)
	{
		this.templateType = templateType;
	}

	public T getEntity()
	{
		return entity;
	}

	public void setEntity(T entity)
	{
		this.entity = entity;
	}

	public List<SimpleEntry<String, Integer>> getUniqueIntVals()
	{
		return uniqueIntVals;
	}

	public void setUniqueIntVals(List<SimpleEntry<String, Integer>> uniqueIntVals)
	{
		this.uniqueIntVals = uniqueIntVals;
	}

	public List<SimpleEntry<String, String>> getUniqueStringVals()
	{
		return uniqueStringVals;
	}

	public void setUniqueStringVals(List<SimpleEntry<String, String>> uniqueStringVals)
	{
		this.uniqueStringVals = uniqueStringVals;
	}

}