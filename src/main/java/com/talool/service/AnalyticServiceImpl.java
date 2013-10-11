package com.talool.service;

import java.util.UUID;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
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

	public Long getTotalActivatedCodes(final UUID dealOfferId) throws ServiceException
	{
		Long count = null;

		try
		{
			final Query query = getCurrentSession().createQuery(
					"select count(*) FROM ActivationCodeImpl where activatedDate is not null AND dealOfferId = :offerId");
			query.setParameter("offerId", dealOfferId);
			count = (Long) query.uniqueResult();
		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count;
	}

	public Long getTotalRedemptions(final UUID customerId) throws ServiceException
	{
		Long count = null;

		try
		{
			final Query query = getCurrentSession().createQuery(
					"select count(*) FROM DealAcquireImpl where redemptionCode is not null AND customer.id = :customerId");
			query.setParameter("customerId", customerId);
			count = (Long) query.uniqueResult();
		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count;
	}

	@Override
	public Long getTotalFacebookCustomers() throws ServiceException
	{
		Long count = null;

		try
		{
			final SQLQuery query = getCurrentSession().createSQLQuery(
					"select count(*) as cnt from customer as c,customer_social_account as csa where csa.customer_id=c.customer_id and csa.social_network_id=1");
			query.addScalar("cnt", StandardBasicTypes.LONG);
			count = (Long) query.uniqueResult();

		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count;
	}

	@Override
	public Long getTotalEmailCustomers() throws ServiceException
	{
		// TODO Auto-generated method stub
		return null;
	}

}