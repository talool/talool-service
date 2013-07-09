package com.talool.core.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.talool.core.AccountType;
import com.talool.core.Category;
import com.talool.core.CategoryTag;
import com.talool.core.DealAcquire;
import com.talool.core.DealAcquireHistory;
import com.talool.core.Tag;
import com.talool.core.social.SocialNetwork;
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

}
