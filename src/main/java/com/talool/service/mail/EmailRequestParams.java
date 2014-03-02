package com.talool.service.mail;

/**
 * Email request parameters which holds the entity (gift, customer, etc) and
 * also defines whether the request is asynchronous
 * 
 * @author clintz
 * 
 * @param <T>
 */
public class EmailRequestParams<T>
{
	private final T entity;

	public EmailRequestParams(T entity)
	{
		super();
		this.entity = entity;
	}

	public T getEntity()
	{
		return entity;
	}

}
