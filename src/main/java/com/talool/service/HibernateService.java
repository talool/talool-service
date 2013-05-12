package com.talool.service;

import com.talool.core.service.ServiceException;

/**
 * 
 * @author clintz
 * 
 */
public interface HibernateService
{
	public void evict(Object obj) throws ServiceException;

	public void merge(final Object obj) throws ServiceException;

	public void refresh(final Object obj) throws ServiceException;

	public void reattach(final Object obj) throws ServiceException;

	public void initialize(final Object obj) throws ServiceException;

	public void isInitialized(final Object obj) throws ServiceException;
}
