package com.talool.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talool.core.service.AnalyticService;
import com.talool.core.service.CustomerService;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.persistence.QueryHelper;
import com.talool.persistence.QueryHelper.QueryType;

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
	private TaloolService taloolService;
	private CustomerService customerService;

	private static final String TOTAL_TALOOL_CUSTOMERS = "select count(*) FROM CustomerImpl";
	private static final String TOTAL_TALOOL_REDEMPTIONS = "select count(*) FROM DealAcquireImpl where redemptionCode is not null";
	private static final String TOTAL_TALOOL_EMAIL_GIFTS = "select count(*) FROM GiftImpl where toEmail is not null";
	private static final String TOTAL_TALOOL_FB_GIFTS = "select count(*) FROM GiftImpl where toFacebookId is not null";
	private static final String TOTAL_TALOOL_FB_CUSTOMERS = "select count(*) as cnt from customer as c,customer_social_account as csa where csa.customer_id=c.customer_id and csa.social_network_id=1";
	private static final String TOTAL_PUB_FB_CUSTOMERS = "select count(*) as cnt from customer as c,deal_offer_purchase as dop,deal_offer as dof," +
			"customer_social_account as csa " +
			"where dop.customer_id=c.customer_id and dop.deal_offer_id=dof.deal_offer_id and " +
			"dof.merchant_id=:publisherMerchantId and csa.customer_id=c.customer_id and csa.social_network_id=1";

	@Override
	public long getTotalCustomers() throws ServiceException
	{
		Long count = null;

		try
		{
			final Query query = getCurrentSession().createQuery(TOTAL_TALOOL_CUSTOMERS);

			count = (Long) query.uniqueResult();
		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count == null ? 0 : count;
	}

	@Override
	public long getPublishersCustomerTotal(final UUID publisherMerchantId) throws ServiceException
	{
		try
		{
			return customerService.getPublisherCustomerSummaryCount(publisherMerchantId);
		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public long getTotalRedemptions() throws ServiceException
	{
		Long count = null;

		try
		{
			final Query query = getCurrentSession().createQuery(TOTAL_TALOOL_REDEMPTIONS);

			count = (Long) query.uniqueResult();
		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count == null ? 0 : count;
	}

	@Override
	public long getTotalEmailGifts() throws ServiceException
	{
		Long count = null;

		try
		{
			final Query query = getCurrentSession().createQuery(TOTAL_TALOOL_EMAIL_GIFTS);
			count = (Long) query.uniqueResult();
		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count == null ? 0 : count;
	}

	@Override
	public long getTotalFacebookGifts() throws ServiceException
	{
		Long count = null;

		try
		{
			final Query query = getCurrentSession().createQuery(TOTAL_TALOOL_FB_GIFTS);

			count = (Long) query.uniqueResult();
		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count == null ? 0 : count;
	}

	public long getTotalActivatedCodes(final UUID dealOfferId) throws ServiceException
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

		return count == null ? 0 : count;
	}

	public long getTotalRedemptions(final UUID customerId) throws ServiceException
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

		return count == null ? 0 : count;
	}

	@Override
	public long getTotalFacebookCustomers() throws ServiceException
	{
		Long count = null;

		try
		{
			final SQLQuery query = getCurrentSession().createSQLQuery(TOTAL_TALOOL_FB_CUSTOMERS);
			query.addScalar("cnt", StandardBasicTypes.LONG);
			count = (Long) query.uniqueResult();

		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return count == null ? 0 : count;
	}

	@Override
	public List<AvailableDeal> getAvailableDeals(UUID merchantId) throws ServiceException
	{
		List<AvailableDeal> availableDeals = new ArrayList<AvailableDeal>();

		try
		{
			final SQLQuery query = getCurrentSession().createSQLQuery(
					"SELECT title AS t, d.deal_id AS did, count(*) AS cnt " +
							"FROM deal AS d, deal_acquire AS da " +
							"WHERE d.deal_id = da.deal_id AND d.merchant_id = :merchantId AND da.acquire_status = 'REDEEMED' " +
							"GROUP BY d.deal_id " +
							"ORDER BY cnt DESC " +
							"LIMIT 10");

			query.setParameter("merchantId", merchantId, PostgresUUIDType.INSTANCE);
			query.addScalar("t", StandardBasicTypes.STRING);
			query.addScalar("did", PostgresUUIDType.INSTANCE);
			query.addScalar("cnt", StandardBasicTypes.LONG);

			@SuppressWarnings("unchecked")
			List<Object[]> l = query.list();

			for (Object[] o : l)
			{
				String title = (String) o[0];
				UUID id = (UUID) o[1];
				Long cnt = (Long) o[2];
				availableDeals.add(new AvailableDeal(title, id, cnt));
			}

		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return availableDeals;
	}

	@Override
	public List<RecentRedemption> getRecentRedemptions(UUID merchantId) throws ServiceException
	{
		List<RecentRedemption> rr = new ArrayList<RecentRedemption>();

		try
		{
			final SQLQuery query = getCurrentSession()
					.createSQLQuery(
							"SELECT title AS t, d.deal_id AS did, first_name AS fname, last_name AS lname, c.customer_id AS cid, redemption_code AS code, redemption_dt AS rdate "
									+
									"FROM deal AS d, deal_acquire AS da, customer AS c "
									+
									"WHERE d.deal_id = da.deal_id AND d.merchant_id = :merchantId AND da.customer_id = c.customer_id AND da.acquire_status = 'REDEEMED' "
									+
									"ORDER BY rdate DESC " +
									"LIMIT 10");

			query.setParameter("merchantId", merchantId, PostgresUUIDType.INSTANCE);
			query.addScalar("t", StandardBasicTypes.STRING);
			query.addScalar("did", PostgresUUIDType.INSTANCE);
			query.addScalar("fname", StandardBasicTypes.STRING);
			query.addScalar("lname", StandardBasicTypes.STRING);
			query.addScalar("cid", PostgresUUIDType.INSTANCE);
			query.addScalar("code", StandardBasicTypes.STRING);
			query.addScalar("rdate", StandardBasicTypes.DATE);

			@SuppressWarnings("unchecked")
			List<Object[]> l = query.list();

			for (Object[] o : l)
			{
				String title = (String) o[0];
				UUID id = (UUID) o[1];
				String fname = (String) o[2];
				String lname = (String) o[3];
				String name = new StringBuilder(fname).append(" ").append(lname).toString();
				UUID cId = (UUID) o[4];
				String code = (String) o[5];
				Date date = (Date) o[6];
				rr.add(new RecentRedemption(title, id, name, cId, code, date));
			}

		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return rr;
	}

	@Override
	public List<ActiveUser> getActiveUsers(UUID merchantId) throws ServiceException
	{
		List<ActiveUser> au = new ArrayList<ActiveUser>();

		try
		{
			final SQLQuery query = getCurrentSession().createSQLQuery(
					"SELECT first_name AS fname, last_name AS lname, c.customer_id AS uid, count(*) AS deals " +
							"FROM deal AS d, deal_acquire AS da, customer AS c " +
							"WHERE d.deal_id = da.deal_id AND d.merchant_id = :merchantId AND da.customer_id = c.customer_id AND da.acquire_status = 'REDEEMED' " +
							"GROUP BY uid ORDER BY deals DESC " +
							"LIMIT 10");

			query.setParameter("merchantId", merchantId, PostgresUUIDType.INSTANCE);
			query.addScalar("fname", StandardBasicTypes.STRING);
			query.addScalar("lname", StandardBasicTypes.STRING);
			query.addScalar("uid", PostgresUUIDType.INSTANCE);
			query.addScalar("deals", StandardBasicTypes.LONG);
			// query.addScalar("visits", StandardBasicTypes.LONG);
			// query.addScalar("messages", StandardBasicTypes.LONG);

			@SuppressWarnings("unchecked")
			List<Object[]> l = query.list();

			for (Object[] o : l)
			{
				String fname = (String) o[0];
				String lname = (String) o[1];
				String name = new StringBuilder(fname).append(" ").append(lname).toString();
				UUID id = (UUID) o[2];
				Long d = (Long) o[3];
				// Long v = (Long)o[4];
				// Long m = (Long)o[5];
				au.add(new ActiveUser(name, id, d, d, d));
			}

		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return au;
	}

	@Override
	public List<MerchantReach> getMerchantReaches(UUID merchantId) throws ServiceException
	{
		// TODO Auto-generated method stub

		List<MerchantReach> mr = new ArrayList<MerchantReach>();

		try
		{
			final SQLQuery query = getCurrentSession().createSQLQuery(
					"SELECT title AS t, d.deal_id AS did, count(*) AS cnt " +
							"FROM deal AS d, deal_acquire AS da " +
							"WHERE d.deal_id = da.deal_id AND d.merchant_id = :merchantId " +
							"GROUP BY d.deal_id " +
							"ORDER BY cnt DESC " +
							"LIMIT 10");

			query.setParameter("merchantId", merchantId, PostgresUUIDType.INSTANCE);
			query.addScalar("t", StandardBasicTypes.STRING);
			query.addScalar("did", PostgresUUIDType.INSTANCE);
			query.addScalar("cnt", StandardBasicTypes.LONG);

			@SuppressWarnings("unchecked")
			List<Object[]> l = query.list();

			for (Object[] o : l)
			{
				String message = (String) o[0];
				UUID id = (UUID) o[1];
				Long q = (Long) o[2];
				Long t = (Long) o[3];
				Long a = (Long) o[4];
				mr.add(new MerchantReach(message, id, q, t, a));
			}

		}
		catch (Exception e)
		{
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return mr;
	}

	public TaloolService getTaloolService()
	{
		return taloolService;
	}

	public void setTaloolService(TaloolService taloolService)
	{
		this.taloolService = taloolService;
	}

	public CustomerService getCustomerService()
	{
		return customerService;
	}

	public void setCustomerService(CustomerService customerService)
	{
		this.customerService = customerService;
	}

	@Override
	public long getPublishersCustomerRedemptionTotal(UUID publisherMerchantId) throws ServiceException
	{
		Long total = null;

		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.PublisherCustomerRedemptionTotal, null, null, true);
			final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
			query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
			query.addScalar("totalResults", StandardBasicTypes.LONG);
			total = (Long) query.uniqueResult();
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getPublisherCustomerRedemptionTotal: " + ex.getMessage(), ex);
		}

		return total == null ? 0 : total;
	}

	@Override
	public long getPublishersEmailGiftTotal(UUID publisherMerchantId) throws ServiceException
	{
		Long total = null;

		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.PublisherEmailGiftCnt, null, null, true);
			final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
			query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
			query.addScalar("totalResults", StandardBasicTypes.LONG);
			total = (Long) query.uniqueResult();
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getPublishersEmailGiftTotal: " + ex.getMessage(), ex);
		}

		return total == null ? 0 : total;
	}

	@Override
	public long getPublishersFacebookGiftTotal(UUID publisherMerchantId) throws ServiceException
	{
		Long total = null;

		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.PublisherFacebookGiftCnt, null, null, true);
			final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(newSql);
			query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
			query.addScalar("totalResults", StandardBasicTypes.LONG);
			total = (Long) query.uniqueResult();
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getPublishersFacebookGiftTotal: " + ex.getMessage(), ex);
		}

		return total == null ? 0 : total;
	}

	@Override
	public long getPublishersFacebookCustomerTotal(final UUID publisherMerchantId) throws ServiceException
	{
		Long total = null;

		try
		{
			final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(TOTAL_PUB_FB_CUSTOMERS);
			query.setParameter("publisherMerchantId", publisherMerchantId, PostgresUUIDType.INSTANCE);
			query.addScalar("cnt", StandardBasicTypes.LONG);
			total = (Long) query.uniqueResult();
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getPublishersFacebookCustomerTotal: " + ex.getMessage(), ex);
		}

		return total == null ? 0 : total;
	}

}