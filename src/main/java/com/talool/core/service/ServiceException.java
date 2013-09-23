package com.talool.core.service;

import com.talool.service.ErrorCode;

/**
 * 
 * 
 * 
 * @author clintz
 */
public class ServiceException extends Exception
{
	private static final long serialVersionUID = 2550012244176023296L;

	protected ErrorCode errorCode = ErrorCode.UNKNOWN;

	public ServiceException()
	{
		super();
	}

	public ServiceException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public ServiceException(String arg0)
	{
		super(arg0);
	}

	public ServiceException(Throwable arg0)
	{
		super(arg0);
	}

	public ServiceException(ErrorCode errorCode)
	{
		super();
		this.errorCode = errorCode;
	}

	public ServiceException(ErrorCode errorCode, String arg0, Throwable arg1)
	{
		super(arg0, arg1);
		this.errorCode = errorCode;
	}

	public ServiceException(ErrorCode errorCode, String arg0)
	{
		super(arg0);
		this.errorCode = errorCode;
	}

	public ServiceException(ErrorCode errorCode, Throwable arg0)
	{
		super(arg0);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode()
	{
		return errorCode;
	}

	@Override
	public String getMessage()
	{
		StringBuilder sb = new StringBuilder();

		if (errorCode != null)
		{
			sb.append(errorCode.getMessage());
			if (super.getMessage() != null)
			{
				sb.append(": ").append(super.getMessage());
			}
			return sb.toString();
		}

		return super.getMessage();
	}

}
