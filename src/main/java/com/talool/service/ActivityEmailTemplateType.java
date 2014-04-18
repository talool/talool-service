package com.talool.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Email Template Type
 * 
 * @author clintz
 * 
 */
public enum ActivityEmailTemplateType
{
	Unknown(0), BasicFundRaiser(1);

	int templateId;

	static Map<Integer, ActivityEmailTemplateType> types = new HashMap<Integer, ActivityEmailTemplateType>();

	static
	{
		for (ActivityEmailTemplateType type : ActivityEmailTemplateType.values())
		{
			types.put(type.templateId, type);
		}
	}

	ActivityEmailTemplateType(int templateId)
	{
		this.templateId = templateId;

	}

	public int getTemplateId()
	{
		return templateId;
	}

	public String getTemplateIdAsString()
	{
		return String.valueOf(templateId);
	}

	public static ActivityEmailTemplateType getByTemplateId(int templateId)
	{
		ActivityEmailTemplateType type = types.get(templateId);
		return type == null ? ActivityEmailTemplateType.Unknown : type;
	}
}
