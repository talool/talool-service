package com.talool.service;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talool.core.service.AnalyticService;
import com.talool.core.service.ServiceException;

/**
 * 
 * @author clintz
 * 
 */
@Transactional(readOnly = true)
@Service
@Repository
public class AnalyticServiceImpl extends AbstractHibernateService implements AnalyticService
{

	@Override
	public Long getTotalCustomers() throws ServiceException
	{
		Long count = null;

		try
		{
			final Query query = getCurrentSession().createQuery("select count(*) FROM CustomerImpl");

			count = (Long) query.uniqueResult();
		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count;
	}

	@Override
	public Long getTotalRedemptions() throws ServiceException
	{
		Long count = null;

		try
		{
			final Query query = getCurrentSession().createQuery("select count(*) FROM DealAcquireImpl where redemptionCode is not null");

			count = (Long) query.uniqueResult();
		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count;
	}

	@Override
	public Long getTotalEmailGifts() throws ServiceException
	{
		Long count = null;

		try
		{
			final Query query = getCurrentSession().createQuery("select count(*) FROM GiftImpl where toEmail is not null");

			count = (Long) query.uniqueResult();
		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count;
	}

	@Override
	public Long getTotalFacebookGifts() throws ServiceException
	{
		Long count = null;

		try
		{
			final Query query = getCurrentSession().createQuery("select count(*) FROM GiftImpl where toFacebookId is not null");

			count = (Long) query.uniqueResult();
		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count;
	}

}