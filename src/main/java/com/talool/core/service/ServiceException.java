package com.talool.core.service;

/**
 * 
 * 
 * 
 * @author clintz
 */
public class ServiceException extends Exception
{
	private static final long serialVersionUID = 2550012244176023296L;

	public static enum Type
	{
		UNKNOWN(0, "Unknown"), EMAIL_ALREADY_TAKEN(1000, "Email already taken"), INVALID_USERNAME_OR_PASSWORD(
				1001, "Invalid username or password"), CUSTOMER_DOES_NOT_OWN_DEAL(1002,
				"Customer does not own deal");

		private final int code;
		private final String message;

		Type(int code, String message)
		{
			this.code = code;
			this.message = message;
		}

		public int getCode()
		{
			return code;
		}

		public String getMessage()
		{
			return message;
		}

	}

	protected Type type = Type.UNKNOWN;

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

	public ServiceException(Type type)
	{
		super();
		this.type = type;
	}

	public ServiceException(Type type, String arg0, Throwable arg1)
	{
		super(arg0, arg1);
		this.type = type;
	}

	public ServiceException(Type type, String arg0)
	{
		super(arg0);
		this.type = type;
	}

	public ServiceException(Type type, Throwable arg0)
	{
		super(arg0);
		this.type = type;
	}

	public Type getType()
	{
		return type;
	}

}
