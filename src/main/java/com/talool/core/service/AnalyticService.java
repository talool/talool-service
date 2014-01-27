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
	/**
	 * Gets total customers count in Talool
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public long getTotalCustomers() throws ServiceException;

	/**
	 * Gets the publishers total customers count
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public long getPublishersCustomerTotal(final UUID publisherMerchantId) throws ServiceException;

	/**
	 * Gets all activation code summaries in Talool
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public List<ActivationCodeSummary> getActivationCodeSummaries() throws ServiceException;

	public List<ActivationCodeSummary> getPublishersActivationCodeSummaries(final UUID publisherMerchantId) throws ServiceException;

	/**
	 * Gets the publishers total redemption count
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public long getPublishersCustomerRedemptionTotal(final UUID publisherMerchantId) throws ServiceException;

	/**
	 * Gets total redemption count in Talool
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public long getTotalRedemptions() throws ServiceException;

	/**
	 * Gets the publishers total email gift count
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public long getPublishersEmailGiftTotal(final UUID publisherMerchantId) throws ServiceException;

	/**
	 * Gets the publishers total facebook gift count
	 * 
	 * @param publisherMerchantId
	 * @return
	 * @throws ServiceException
	 */
	public long getPublishersFacebookGiftTotal(final UUID publisherMerchantId) throws ServiceException;

	/**
	 * Gets the total email gift count in Talool
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public long getTotalEmailGifts() throws ServiceException;

	/**
	 * Gets the total facebook gift count in Talool
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public long getTotalFacebookGifts() throws ServiceException;

	/**
	 * Gets the total activated codes for the deal offer
	 * 
	 * @param dealOfferId
	 * @return
	 * @throws ServiceException
	 */
	public long getTotalActivatedCodes(UUID dealOfferId) throws ServiceException;

	public long getTotalRedemptions(UUID customerId) throws ServiceException;

	/**
	 * Gets the publishers total facebook customer count
	 * 
	 * @param publisherMerchantId
	 * @return
	 * @throws ServiceException
	 */
	public long getPublishersFacebookCustomerTotal(final UUID publisherMerchantId) throws ServiceException;

	public long getTotalFacebookCustomers() throws ServiceException;

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

	public class ActivationCodeSummary implements Serializable
	{
		private static final long serialVersionUID = -6982479518083932037L;
		public final String dealOfferTitle;
		public final long totalActivatedCodes;
		public final long totalCodes;

		public ActivationCodeSummary(String dealOfferTitle, long totalActivatedCodes, long totalCodes)
		{
			this.dealOfferTitle = dealOfferTitle;
			this.totalActivatedCodes = totalActivatedCodes;
			this.totalCodes = totalCodes;
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
