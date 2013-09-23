package com.talool.core.service;

/**
 * NotFoundException
 * 
 * @author clintz
 * 
 */
public class NotFoundException extends Exception
{
	private static final long serialVersionUID = 2069774236102056492L;

	private String identifier;
	private String parameter;

	public NotFoundException(String identifier, String parameter)
	{
		super();
		this.identifier = identifier;
		this.parameter = parameter;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	public String getParameter()
	{
		return parameter;
	}

	public void setParameter(String parameter)
	{
		this.parameter = parameter;
	}

}
