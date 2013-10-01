package com.talool.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author clintz
 * 
 */
public enum ErrorCode
{
	UNKNOWN(0, "Unknown"),
	VALID_EMAIL_REQUIRED(100, "Valid email required"),
	PASS_REQUIRED(101, "Password is required"),
	PASS_CONFIRM_MUST_MATCH(102, "Password confirmation does not match"),
	PASS_RESET_CODE_REQUIRED(103, "Password reset code required"),
	PASS_RESET_CODE_EXPIRED(104, "Password reset code expired"),
	PASS_RESET_CODE_INVALID(105, "Password reset code invalid"),

	EMAIL_ALREADY_TAKEN(1000, "Email already taken"),
	INVALID_USERNAME_OR_PASSWORD(1001, "Invalid username or password"),
	CUSTOMER_DOES_NOT_OWN_DEAL(1002, "Customer does not own deal"),
	DEAL_ALREADY_REDEEMED(1003, "Deal already redeemed"),
	GIFTING_NOT_ALLOWED(1004, "Cannot gift deal due to acquire status"),
	CUSTOMER_NOT_FOUND(1005, "Customer not found"),
	EMAIL_REQUIRED(1006, "Email is required"),
	EMAIL_OR_PASS_INVALID(1007, "Invalid email or password"),

	GENERAL_PROCESSOR_ERROR(1500, "Unknown processor error"),

	MAIL_TEMPLATE_NOT_FOUND(2004, "Mail template not found"),
	ACTIVIATION_CODE_NOT_FOUND(3000, "Activiation code not found"),
	ACTIVIATION_CODE_ALREADY_ACTIVATED(3001, "Activiation code already activated");

	private final int code;
	private final String message;
	private static Map<Integer, ErrorCode> errorCodeMap = new HashMap<Integer, ErrorCode>();

	static
	{
		for (ErrorCode ec : ErrorCode.values())
		{
			errorCodeMap.put(ec.getCode(), ec);
		}

	}

	public static ErrorCode findByCode(final int code)
	{
		return errorCodeMap.get(code);
	}

	public int getCode()
	{
		return code;
	}

	public String getMessage()
	{
		return message;
	}

	ErrorCode(int code, String message)
	{
		this.code = code;
		this.message = message;
	}

}
