package com.talool.core;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.talool.service.ErrorCode;

/**
 * A generica result class returning the target or error
 * 
 * @author clintz
 * 
 * @param <T>
 */
public class ReturnResult<T>
{
	private T target;
	private List<ErrorCode> errorCodes;

	public ReturnResult(T target)
	{
		this.target = target;
	}

	public ReturnResult(final ErrorCode... errorCodes)
	{
		this.errorCodes = Arrays.asList(errorCodes);
	}

	public boolean hasErrors()
	{
		return !CollectionUtils.isEmpty(errorCodes);
	}

	public T getTarget()
	{
		return target;
	}

	public List<ErrorCode> getErrorCodes()
	{
		return errorCodes;
	}

	/**
	 * Returns a String of comma delimited errors if there are errors, other wise
	 * null is returned
	 * 
	 * @return
	 */
	public String getErrorMessage()
	{
		if (hasErrors())
		{
			StringBuilder sb = new StringBuilder();
			for (ErrorCode eCode : errorCodes)
			{
				if (sb.length() == 0)
				{
					sb.append(", ");
				}
				sb.append(eCode.getMessage());
			}
			return sb.toString();
		}

		return null;
	}

}
