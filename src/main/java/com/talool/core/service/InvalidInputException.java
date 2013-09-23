package com.talool.core.service;

import com.talool.service.ErrorCode;

/**
 * 
 * 
 * 
 * @author clintz
 */
public class InvalidInputException extends Exception
{
	private static final long serialVersionUID = 2550012244176023296L;

	protected ErrorCode errorCode = ErrorCode.UNKNOWN;

	protected String parameter;

	public InvalidInputException()
	{
		super();
	}

	public InvalidInputException(ErrorCode errorCode, String parameter)
	{
		super();
		this.errorCode = errorCode;
		this.parameter = parameter;
	}

	public String getParameter()
	{
		return parameter;
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