package com.talool.core.service;

/**
 * 
 * @author clintz
 * 
 */
public class ProcessorException extends Exception
{
	private static final long serialVersionUID = 8437218228900247304L;

	public ProcessorException()
	{
		super();
	}

	public ProcessorException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public ProcessorException(String arg0)
	{
		super(arg0);
	}

	public ProcessorException(Throwable arg0)
	{
		super(arg0);
	}

}
