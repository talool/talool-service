package com.talool.core.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

	public Long getPublishersCustomerTotal(final UUID publisherMerchantId) throws ServiceException;

	public Long getPublishersCustomerRedemptionTotal(final UUID publisherMerchantId) throws ServiceException;

	public Long getTotalRedemptions() throws ServiceException;

	public Long getTotalEmailGifts() throws ServiceException;

	public Long getTotalFacebookGifts() throws ServiceException;

	public Long getTotalActivatedCodes(UUID dealOfferId) throws ServiceException;

	public Long getTotalRedemptions(UUID customerId) throws ServiceException;

	public Long getTotalFacebookCustomers() throws ServiceException;

	public Long getTotalEmailCustomers() throws ServiceException;

	public List<AvailableDeal> getAvailableDeals(UUID merchantId) throws ServiceException;

	public List<RecentRedemption> getRecentRedemptions(UUID merchantId) throws ServiceException;

	public List<ActiveUser> getActiveUsers(UUID merchantId) throws ServiceException;

	public List<MerchantReach> getMerchantReaches(UUID merchantId) throws ServiceException;

	public class AvailableDeal implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public String title;
		public UUID dealId;
		public Long redemptionCount;

		public AvailableDeal(String title, UUID dealId, Long redemptionCount)
		{
			this.title = title;
			this.dealId = dealId;
			this.redemptionCount = redemptionCount;
		}
	}

	public class RecentRedemption implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public String title;
		public UUID dealId;
		public String customerName;
		public UUID customerId;
		public String code;
		public Date date;

		public RecentRedemption(String title, UUID dealId, String cName, UUID cId, String c, Date d)
		{
			this.title = title;
			this.dealId = dealId;
			this.customerName = cName;
			this.customerId = cId;
			this.code = c;
			this.date = d;
		}
	}

	public class ActiveUser implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public String customerName;
		public UUID customerId;
		public Long dealCount;
		public Long visitCount;
		public Long messageCount;

		public ActiveUser(String cName, UUID cId, Long d, Long v, Long m)
		{
			this.customerName = cName;
			this.customerId = cId;
			this.dealCount = d;
			this.visitCount = v;
			this.messageCount = m;
		}
	}

	public class MerchantReach implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public String message;
		public UUID messageId;
		public Long quota;
		public Long targetedUserCount;
		public Long acquiredUserCount;

		public MerchantReach(String m, UUID mid, Long q, Long t, Long a)
		{
			this.message = m;
			this.messageId = mid;
			this.quota = q;
			this.targetedUserCount = t;
			this.acquiredUserCount = a;
		}
	}

}
