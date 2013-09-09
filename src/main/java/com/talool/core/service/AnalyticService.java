package com.talool.core.service;

import com.talool.service.HibernateService;

/**
 * Service for Talool analytics
 * 
 * @author clintz
 * 
 */
public interface AnalyticService extends HibernateService
{
	public Long getTotalCustomers() throws ServiceException;

	public Long getTotalRedemptions() throws ServiceException;

	public Long getTotalEmailGifts() throws ServiceException;

	public Long getTotalFacebookGifts() throws ServiceException;

}
