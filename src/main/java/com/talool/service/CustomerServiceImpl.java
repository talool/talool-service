package com.talool.service;

import java.util.List;
import java.util.UUID;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.hibernate.DAODispatcher;
import com.googlecode.genericdao.search.Search;
import com.talool.core.AccountType;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealOfferPurchase;
import com.talool.core.FavoriteMerchant;
import com.talool.core.IdentifiableUUID;
import com.talool.core.Merchant;
import com.talool.core.Relationship;
import com.talool.core.SearchOptions;
import com.talool.core.service.CustomerService;
import com.talool.core.service.ServiceException;
import com.talool.domain.CustomerImpl;
import com.talool.domain.DealAcquireImpl;
import com.talool.domain.DealOfferPurchaseImpl;
import com.talool.domain.FavoriteMerchantImpl;
import com.talool.domain.RelationshipImpl;
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
	@Transactional(propagation = Propagation.REQUIRED)
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
			final String newSql = QueryHelper.buildQuery(QueryType.GetDealAcquires, null, searchOpts,
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
			final String newSql = QueryHelper.buildQuery(QueryType.GetMerchantAcquires, null, searchOpts,
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
			final Search search = new Search().addFilterEqual("customerId", customerId).addFilterEqual("merchantId", merchantId);
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
			final String newSql = QueryHelper.buildQuery(QueryType.GetFavoriteMerchants, null, searchOpts,
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

}
