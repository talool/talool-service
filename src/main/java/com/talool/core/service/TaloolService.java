package com.talool.core.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.talool.core.AccountType;
import com.talool.core.ActivationSummary;
import com.talool.core.Category;
import com.talool.core.CategoryTag;
import com.talool.core.DealAcquire;
import com.talool.core.DealAcquireHistory;
import com.talool.core.DealOffer;
import com.talool.core.Tag;
import com.talool.core.social.SocialNetwork;
import com.talool.domain.Properties;
import com.talool.service.HibernateService;

/**
 * 
 * @author clintz
 */
public interface TaloolService extends MerchantService, HibernateService
{
	public SocialNetwork getSocialNetwork(final SocialNetwork.NetworkName name) throws ServiceException;

	public boolean emailExists(final AccountType accountType, final String email)
			throws ServiceException;

	public Tag getTag(String tagName) throws ServiceException;

	public List<Tag> getTags() throws ServiceException;

	public void addTags(List<Tag> tags) throws ServiceException;

	public Set<Tag> getOrCreateTags(String... tags) throws ServiceException;

	public List<Category> getAllCategories() throws ServiceException;

	public Category getCategory(final String categoryName) throws ServiceException;

	public Category getCategory(final Integer categoryId) throws ServiceException;

	public Map<Category, List<Tag>> getCategoryTags() throws ServiceException;

	public void save(final Category category) throws ServiceException;

	public void save(final Tag category) throws ServiceException;

	public CategoryTag createCategoryTag(final String categoryName, final String tagName)
			throws ServiceException;

	public Long sizeOfCollection(final Object collection) throws ServiceException;

	public List<DealAcquireHistory> getDealAcquireHistory(final UUID dealAcquireId, final boolean chronological)
			throws ServiceException;

	public List<DealAcquireHistory> getDealAcquireHistoryByGiftId(final UUID giftId, final boolean chronological)
			throws ServiceException;

	public List<DealAcquire> getRedeemedDealAcquires(final UUID merchantId, final String redemptionCode) throws ServiceException;

	public void createActivationCodes(final UUID dealOfferId, final int totalCodes) throws ServiceException;

	public List<String> getActivationCodes(final UUID dealOfferId) throws ServiceException;

	public List<ActivationSummary> getActivationSummaries(final UUID merchantId) throws ServiceException;

	public void deleteCustomer(final UUID customerId) throws ServiceException;

	/**
	 * Copies all deals of an existing deal offer and assigns them to the newly
	 * copied deal offer. The deal offer is set to in-active and the title of the
	 * new deal offer is "copy of {orginalDealOfferTitle}"
	 * 
	 * @param dealOffer
	 * @return
	 * @throws ServiceException
	 */
	public DealOffer deepCopyDealOffer(final UUID dealOfferId) throws ServiceException;

	public void setIsCustomerEmailValid(final String email, boolean isValid) throws ServiceException;

	public void setIsMerchantEmailValid(final String email, boolean isValid) throws ServiceException;

	public enum PropertySupportedEntity
	{
		DealOffer, Merchant, MerchantLocation
	};

	/**
	 * Gets the unique property keys for the dealOffers
	 * 
	 * @param entityInterfaceClass
	 * @return
	 * @throws ServiceException
	 */
	public List<String> getUniqueProperyKeys(final PropertySupportedEntity entity) throws ServiceException;

	public void saveProperties(final PropertySupportedEntity entity, final UUID entityId, final Properties properties) throws ServiceException;

}
