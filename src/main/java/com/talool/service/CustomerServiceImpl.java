package com.talool.service;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.type.PostgresUUIDType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.hibernate.DAODispatcher;
import com.googlecode.genericdao.search.Search;
import com.talool.core.AccountType;
import com.talool.core.AcquireStatus;
import com.talool.core.AcquireStatusType;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealOfferPurchase;
import com.talool.core.FavoriteMerchant;
import com.talool.core.IdentifiableUUID;
import com.talool.core.Merchant;
import com.talool.core.Relationship;
import com.talool.core.RequestStatus;
import com.talool.core.SearchOptions;
import com.talool.core.gift.GiftRequest;
import com.talool.core.service.CustomerService;
import com.talool.core.service.ServiceException;
import com.talool.core.service.ServiceException.Type;
import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.SocialNetwork;
import com.talool.domain.CustomerImpl;
import com.talool.domain.DealAcquireImpl;
import com.talool.domain.DealOfferPurchaseImpl;
import com.talool.domain.FavoriteMerchantImpl;
import com.talool.domain.RelationshipImpl;
import com.talool.domain.gift.EmailGiftRequestImpl;
import com.talool.domain.gift.FacebookGiftRequestImpl;
import com.talool.domain.gift.GiftRequestImpl;
import com.talool.domain.gift.TaloolGiftRequestImpl;
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
public class CustomerServiceImpl extends AbstractHibernateService implements CustomerService
{
	private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void createAccount(final Customer customer, final String password) throws ServiceException
	{
		createAccount(AccountType.CUS, customer, password);
	}

	private static class GiftOwnership
	{
		GiftRequest giftRequest;
		Customer receivingCustomer;
	}

	private void createAccount(final AccountType accountType, final IdentifiableUUID account,
			final String password) throws ServiceException
	{
		try
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Creating accountType:" + accountType + ": " + account.toString());
			}

			final String md5pass = EncryptService.MD5(password);

			((CustomerImpl) (account)).setPassword(md5pass);
			save((CustomerImpl) account);
			daoDispatcher.flush(CustomerImpl.class);
			daoDispatcher.refresh((CustomerImpl) account);

		}
		catch (Exception e)
		{
			final String err = "There was a problem registering  " + account;
			LOG.error(err, e);
			throw new ServiceException(err, e);
		}

	}

	@Override
	public void removeCustomer(UUID id) throws ServiceException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Customer authenticateCustomer(final String email, final String password)
			throws ServiceException
	{
		final Search search = new Search(CustomerImpl.class);

		search.addFilterEqual("email", email);
		try
		{
			search.addFilterEqual("password", EncryptService.MD5(password));
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem authenticating", ex);
		}

		return (Customer) daoDispatcher.searchUnique(search);

	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void save(final Customer customer) throws ServiceException
	{
		try
		{
			daoDispatcher.save(customer);
		}
		catch (Exception e)
		{
			final String err = "There was a problem saving customer " + customer;
			LOG.error(err, e);
			throw new ServiceException(err, e);
		}
	}

	@Override
	public Customer getCustomerById(final UUID id) throws ServiceException
	{
		Customer customer;
		try
		{
			customer = daoDispatcher.find(CustomerImpl.class, id);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getCustomerById  " + id, ex);
		}

		return customer;
	}

	@Override
	public Customer getCustomerByEmail(final String email) throws ServiceException
	{
		Customer customer = null;

		try
		{
			Search search = new Search(CustomerImpl.class);
			search.addFilterEqual("email", email);
			customer = (Customer) daoDispatcher.searchUnique(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getCustomerByEmail  " + email, ex);
		}

		return customer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getCustomers() throws ServiceException
	{
		try
		{
			final Search search = new Search(CustomerImpl.class);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getCustomers"), ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final Relationship relationship) throws ServiceException
	{
		try
		{
			daoDispatcher.save(relationship);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format(
					"Problem saving relationship fromCustomer '%s' toCustomer '%s' "
							+ relationship.getFromCustomer(), relationship.getToCustomer(), ex));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getFriends(final UUID id) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory
					.getCurrentSession()
					.createQuery(
							"from CustomerImpl c, RelationshipImpl r where c.id=r.customer.id and r.friend.id=:customerId");

			query.setParameter("customerId", id);
			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getFriends %s", id), ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void acceptDeal(final DealAcquire dealAcquire, final UUID customerId)
			throws ServiceException
	{
		// TODO apply state change logic. only accept deals in valid states to be
		// accepted

		if (dealAcquire.getAcquireStatus().getStatus().equals(AcquireStatusType.REDEEMED))
		{
			throw new ServiceException("Cannot acceptDeal an already redeemed deal " + dealAcquire);
		}
		try
		{
			final DealAcquireImpl dealAcq = (DealAcquireImpl) dealAcquire;

			dealAcq.setAcquireStatus(ServiceFactory.get().getTaloolService()
					.getAcquireStatus(AcquireStatusType.ACCEPTED_CUSTOMER_SHARE));

			daoDispatcher.save(dealAcq);

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem acceptDeal %s %s", dealAcquire), ex);
		}

	}

	@Override
	/**
	 * Current only supports rejecting deals given by customers (not merchants)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectDeal(final DealAcquire dealAcquire, final UUID customerId)
			throws ServiceException
	{
		// TODO apply state change logic. only reject deals in valid states to be
		// accepted

		if (AcquireStatusType.REDEEMED == AcquireStatusType.valueOf(dealAcquire.getAcquireStatus()
				.getStatus()))
		{
			throw new ServiceException("Cannot rejectDeal an already redeemed deal " + dealAcquire);
		}

		if (!dealAcquire.getCustomer().getId().equals(customerId))
		{
			throw new ServiceException(ServiceException.Type.CUSTOMER_DOES_NOT_OWN_DEAL,
					"Customer does not own deal");
		}

		try
		{
			final DealAcquireImpl dealAcq = (DealAcquireImpl) dealAcquire;

			// give it back to the original share
			final Customer rejectedBy = dealAcquire.getCustomer();
			dealAcq.setCustomer(dealAcquire.getSharedByCustomer());
			dealAcq.setSharedByCustomer(rejectedBy);

			daoDispatcher.save(dealAcquire);

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem acceptDeal %s %s", dealAcquire), ex);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void redeemDeal(final DealAcquire dealAcquire, final UUID customerId)
			throws ServiceException
	{
		final DealAcquireImpl dealAcq = (DealAcquireImpl) dealAcquire;

		if (AcquireStatusType.REDEEMED == AcquireStatusType.valueOf(dealAcq.getAcquireStatus()
				.getStatus()))
		{
			throw new ServiceException("Cannot redeem already redeemed deal " + dealAcquire);
		}
		if (!dealAcquire.getCustomer().getId().equals(customerId))
		{
			throw new ServiceException(ServiceException.Type.CUSTOMER_DOES_NOT_OWN_DEAL,
					"Customer does not own deal");
		}

		try
		{
			dealAcq.setAcquireStatus(ServiceFactory.get().getTaloolService()
					.getAcquireStatus(AcquireStatusType.REDEEMED));

			dealAcq.setRedemptionDate(Calendar.getInstance().getTime());

			daoDispatcher.save(dealAcq);

		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem redeemDeal %s", ex);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Relationship> getRelationshipsFrom(final UUID customerId) throws ServiceException
	{
		try
		{
			final Search search = new Search(RelationshipImpl.class);
			search.addFilterEqual("fromCustomer.id", customerId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getRelationshipsFrom %s", customerId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Relationship> getRelationshipsTo(final UUID customerId) throws ServiceException
	{
		try
		{
			final Search search = new Search(RelationshipImpl.class);
			search.addFilterEqual("toCustomer.id", customerId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getRelationshipsTo %s", customerId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealAcquire> getDealAcquires(final UUID customerId, final UUID merchantId,
			final SearchOptions searchOpts) throws ServiceException
	{
		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.DealAcquires, null, searchOpts,
					true);

			final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
			query.setParameter("customerId", customerId);
			query.setParameter("merchantId", merchantId);
			QueryHelper.applyOffsetLimit(query, searchOpts);

			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format(
					"Problem getDealAcquires customerId %s merchantId %s", customerId, merchantId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchantAcquires(final UUID customerId, final SearchOptions searchOpts)
			throws ServiceException
	{
		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.MerchantAcquires, null, searchOpts,
					true);

			final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
			query.setParameter("customerId", customerId);
			QueryHelper.applyOffsetLimit(query, searchOpts);
			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getMerchantAcquires customerId %s",
					customerId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealAcquire> getDealAcquiresByCustomerId(final UUID customerId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(DealAcquireImpl.class);
			search.addFilterEqual("customer.id", customerId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(
					String.format("Problem getDealAcquiresByCustomerId %s", customerId), ex);
		}
	}

	@Override
	public DealAcquire getDealAcquire(final UUID dealAcquireId) throws ServiceException
	{
		try
		{
			return daoDispatcher.find(DealAcquireImpl.class, dealAcquireId);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealAcquire %s", dealAcquireId), ex);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Deal> getDealsByCustomerId(final UUID accountId) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory
					.getCurrentSession()
					.createQuery(
							"from MerchantDealImpl md, DealBookPurchaseImpl dbp where dbp.merchantId=md.id and dbp.customerId=:customerId");

			query.setParameter("customerId", accountId);
			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealsByCustomerId %s", accountId), ex);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DealOfferPurchase> getDealOfferPurchasesByCustomerId(final UUID customerId)
			throws ServiceException
	{
		try
		{
			final Search search = new Search(DealOfferPurchaseImpl.class);
			search.addFilterEqual("customer.id", customerId);
			return daoDispatcher.search(search);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getDealBookPurchasesByCustomerId %s",
					customerId), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchantsByCustomerId(final Long customerId) throws ServiceException
	{
		try
		{
			final Query query = sessionFactory
					.getCurrentSession()
					.createQuery(
							"select distinct m from DealBookPurchaseImpl dbp,  MerchantDealImpl md, MerchantImpl m, DealBookContentImpl dbc "
									+ "where dbp.customer.id=:customerId AND dbp.dealBook.id=dbc.dealBook.id AND dbc.merchantDeal.merchant.id=md.merchant.id AND dbc.merchantDeal.merchant.id=m.id");

			query.setParameter("customerId", customerId);
			return query.list();

		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getMerchantsByCustomerId %s", customerId),
					ex);
		}
	}

	public DAODispatcher getDaoDispatcher()
	{
		return daoDispatcher;
	}

	public void setDaoDispatcher(DAODispatcher daoDispatcher)
	{
		this.daoDispatcher = daoDispatcher;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addFavoriteMerchant(final UUID customerId, final UUID merchantId) throws ServiceException
	{
		final FavoriteMerchant favMerchant = new FavoriteMerchantImpl(customerId, merchantId);

		try
		{
			daoDispatcher.save(favMerchant);
		}
		catch (Exception e)
		{
			throw new ServiceException(String.format("There was a problem adding favorite merchant: customerId %s merchantId %s",
					customerId, merchantId));
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeFavoriteMerchant(final UUID customerId, final UUID merchantId) throws ServiceException
	{
		try
		{
			final Search search = new Search(FavoriteMerchantImpl.class).addFilterEqual("customerId", customerId).addFilterEqual(
					"merchantId", merchantId);
			final FavoriteMerchant favMerchant = (FavoriteMerchant) daoDispatcher.searchUnique(search);
			if (favMerchant != null)
			{
				daoDispatcher.remove(favMerchant);
			}
			else
			{
				LOG.warn(String.format("Ignoring remove of favorite merchant (not found) customerId %s merchantId %s", customerId,
						merchantId));
			}

		}
		catch (Exception e)
		{
			throw new ServiceException(String.format("There was a problem removing favorite merchant: customerId %s merchantId %s",
					customerId, merchantId));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getFavoriteMerchants(final UUID customerId, final SearchOptions searchOpts) throws ServiceException
	{
		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.FavoriteMerchants, null, searchOpts,
					true);

			final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
			query.setParameter("customerId", customerId);
			QueryHelper.applyOffsetLimit(query, searchOpts);

			return query.list();

		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem getting favorite merchant for customerId " + customerId, ex);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void createDealOfferPurchase(final UUID customerId, final UUID dealOfferId) throws ServiceException
	{
		try
		{
			final SQLQuery query = getCurrentSession().createSQLQuery(
					"insert into public.deal_offer_purchase (customer_id,deal_offer_id) values (:customerId,:dealOfferId)");

			query.setParameter("customerId", customerId, PostgresUUIDType.INSTANCE);
			query.setParameter("dealOfferId", dealOfferId, PostgresUUIDType.INSTANCE);

			query.executeUpdate();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem creating dealOfferPurchase customerId %s dealOfferId %s",
					customerId, dealOfferId));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Merchant> getMerchantAcquires(final UUID customerId, final Integer categoryId, final SearchOptions searchOpts)
			throws ServiceException
	{
		try
		{
			final String newSql = QueryHelper.buildQuery(QueryType.MerchantAcquiresByCatId, null, searchOpts,
					true);

			final Query query = sessionFactory.getCurrentSession().createQuery(newSql);
			query.setParameter("customerId", customerId);
			query.setParameter("categoryId", categoryId);
			QueryHelper.applyOffsetLimit(query, searchOpts);
			return query.list();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getMerchantAcquires customerId %s categoryId %s", customerId,
					categoryId, ex));
		}
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void createGiftRequest(final GiftRequest giftRequest) throws ServiceException
	{
		final DealAcquireImpl dac = (DealAcquireImpl) getCurrentSession().load(DealAcquireImpl.class,
				giftRequest.getDealAcquire().getId());

		final AcquireStatusType currentAcquireStatus = AcquireStatusType.valueOf(dac.getAcquireStatus()
				.getStatus());

		if (currentAcquireStatus == AcquireStatusType.REDEEMED)
		{
			throw new ServiceException(Type.DEAL_ALREADY_REDEEMED, "dealAcquireId: " + dac.getId());
		}

		// Can only redeem a deal that is in a valid state!
		if (currentAcquireStatus != AcquireStatusType.ACCEPTED_CUSTOMER_SHARE &&
				currentAcquireStatus != AcquireStatusType.ACCEPTED_MERCHANT_SHARE &&
				currentAcquireStatus != AcquireStatusType.PURCHASED)
		{
			throw new ServiceException(Type.GIFTING_NOT_ALLOWED,
					String.format("acquireStatus %s for dealAcquireId %s", currentAcquireStatus, dac.getId()));
		}

		if (dac.getCustomer().getId() != giftRequest.getFromCustomer().getId())
		{
			throw new ServiceException(Type.CUSTOMER_DOES_NOT_OWN_DEAL, "gifteeId: " + giftRequest.getFromCustomer().getId()
					+ ", owningCustimerId: " + dac.getCustomer().getId());
		}

		try
		{
			dac.setAcquireStatus(ServiceFactory.get().getTaloolService()
					.getAcquireStatus(AcquireStatusType.PENDING_ACCEPT_CUSTOMER_SHARE));

			daoDispatcher.save(dac);

			daoDispatcher.save(giftRequest);

		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem in giftRequest", ex);
		}
	}

	@Override
	public GiftRequest getGiftRequest(final UUID giftRequestId) throws ServiceException
	{
		try
		{
			return daoDispatcher.find(GiftRequestImpl.class, giftRequestId);
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem getGiftRequest %s", giftRequestId), ex);
		}
	}

	private GiftOwnership validateGiftOwnership(final UUID giftRequestId, final UUID customerId) throws ServiceException
	{
		final GiftRequest giftRequest = daoDispatcher.find(GiftRequestImpl.class, giftRequestId);
		if (giftRequest == null)
		{
			throw new ServiceException(String.format("giftRequestId %s does not exist", giftRequestId));
		}

		final GiftOwnership giftOwnership = new GiftOwnership();
		giftOwnership.giftRequest = giftRequest;

		final Customer receivingCustomer = getCustomerById(customerId);

		if (receivingCustomer == null)
		{
			throw new ServiceException(String.format("customerId %s does not exist", customerId));
		}

		giftOwnership.receivingCustomer = receivingCustomer;

		if (giftRequest instanceof EmailGiftRequestImpl)
		{
			if (!((EmailGiftRequestImpl) giftRequest).getToEmail().equals(receivingCustomer.getEmail()))
			{
				throw new ServiceException(String.format(
						"receivingCustomerId %s with email %s not the gift receiver for giftRequestId %s",
						receivingCustomer.getId(), receivingCustomer.getEmail(), receivingCustomer));
			}
		}
		else if (giftRequest instanceof FacebookGiftRequestImpl)
		{
			final SocialNetwork facebook = ServiceFactory.get().getTaloolService().
					getSocialNetwork(SocialNetwork.NetworkName.Facebook);

			if (!((FacebookGiftRequestImpl) giftRequest).getToFacebookId().
					equals(receivingCustomer.getSocialAccounts().get(facebook).getLoginId()))
			{
				throw new ServiceException(String.format(
						"receivingCustomerId %s with facebookId %s is not the gift receiver for giftRequestId %s",
						receivingCustomer.getId(), receivingCustomer.getSocialAccounts().get(facebook).getLoginId(), receivingCustomer));
			}

		}
		else if (giftRequest instanceof TaloolGiftRequestImpl)
		{
			if (!((TaloolGiftRequestImpl) giftRequest).getToCustomer().getId().equals(receivingCustomer.getId()))
			{
				throw new ServiceException(String.format(
						"receivingCustomerId %s is not the gift receiver for giftRequestId %s", receivingCustomer.getId()));
			}
		}

		return giftOwnership;
	}

	@Override
	@Transactional(propagation = Propagation.NESTED, rollbackFor = ServiceException.class)
	public void acceptGift(final UUID giftRequestId, final UUID receipientCustomerId) throws ServiceException
	{
		final GiftOwnership giftOwnership = validateGiftOwnership(giftRequestId, receipientCustomerId);

		final DealAcquire dac = giftOwnership.giftRequest.getDealAcquire();

		final AcquireStatus status = ServiceFactory.get().getTaloolService()
				.getAcquireStatus(AcquireStatusType.ACCEPTED_CUSTOMER_SHARE);

		dac.setAcquireStatus(status);
		dac.setSharedByCustomer(dac.getCustomer());
		dac.setCustomer(giftOwnership.receivingCustomer);

		// update deal acquire
		daoDispatcher.save(dac);

		// update gift request
		giftOwnership.giftRequest.setRequestStatus(RequestStatus.ACCEPTED);
		daoDispatcher.save(giftOwnership.giftRequest);

	}

	@Override
	@Transactional(propagation = Propagation.NESTED, rollbackFor = ServiceException.class)
	public void rejectGift(final UUID giftRequestId, final UUID receipientCustomerId) throws ServiceException
	{
		final GiftOwnership giftOwnership = validateGiftOwnership(giftRequestId, receipientCustomerId);

		final DealAcquire dac = giftOwnership.giftRequest.getDealAcquire();

		final AcquireStatus status = ServiceFactory.get().getTaloolService()
				.getAcquireStatus(AcquireStatusType.REJECTED_CUSTOMER_SHARE);

		dac.setAcquireStatus(status);

		// update deal acquire
		daoDispatcher.save(dac);

		// update gift request
		giftOwnership.giftRequest.setRequestStatus(RequestStatus.REJECTED);
		daoDispatcher.save(giftOwnership.giftRequest);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void remove(CustomerSocialAccount cas) throws ServiceException
	{
		try
		{
			daoDispatcher.remove(cas);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem in removing CustomerSocialAccount", ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeSocialAccount(final UUID customerId, final SocialNetwork socialNetwork) throws ServiceException
	{
		try
		{
			final Query query = getCurrentSession().getNamedQuery("deleteCustomerSocialAccount");
			query.setParameter("customerId", customerId);
			query.setParameter("socialNetworkId", socialNetwork.getId());
			query.executeUpdate();
		}
		catch (Exception ex)
		{
			throw new ServiceException(String.format("Problem in removing SocialAccount %s for customerId %s", socialNetwork.getName(),
					customerId.toString()), ex);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final CustomerSocialAccount socialAccount) throws ServiceException
	{
		try
		{
			daoDispatcher.save(socialAccount);
		}
		catch (Exception ex)
		{
			throw new ServiceException("Problem saving CustomerSocialAccount", ex);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GiftRequest> getGifts(final UUID customerId, final RequestStatus[] requestStatus)
			throws ServiceException
	{
		List<GiftRequest> gifts = null;

		try
		{
			Query query = (Query) getCurrentSession().getNamedQuery("getGifts");
			query.setParameter("customerId", customerId, PostgresUUIDType.INSTANCE);
			query.setParameterList("requestStatus", requestStatus);

			gifts = query.list();

			// query = (Query) getCurrentSession().getNamedQuery("giftsByEmail");
			// query.setParameter("customerId", customerId);
			// query.setParameterList("requestStatus", requestStatus);

			// dealAcquires.addAll(query.list());

		}
		catch (Exception e)
		{
			throw new ServiceException("Problem getGiftedDealAcquires", e);
		}

		return gifts;

	}
}
