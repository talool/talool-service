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
import com.talool.core.DealOfferPurchase;
import com.talool.core.Merchant;
import com.talool.core.MerchantCodeGroup;
import com.talool.core.PropertyEntity;
import com.talool.core.RefundResult;
import com.talool.core.Tag;
import com.talool.core.social.SocialNetwork;
import com.talool.domain.Properties;
import com.talool.domain.PropertyCriteria;
import com.talool.service.HibernateService;

/**
 * 
 * @author clintz
 */
public interface TaloolService extends MerchantService, HibernateService, RequestHeaderSupport {
  public SocialNetwork getSocialNetwork(final SocialNetwork.NetworkName name) throws ServiceException;

  public boolean emailExists(final AccountType accountType, final String email) throws ServiceException;

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

  public CategoryTag createCategoryTag(final String categoryName, final String tagName) throws ServiceException;

  public Long sizeOfCollection(final Object collection) throws ServiceException;

  public List<DealAcquireHistory> getDealAcquireHistory(final UUID dealAcquireId, final boolean chronological) throws ServiceException;

  public List<DealAcquireHistory> getDealAcquireHistoryByGiftId(final UUID giftId, final boolean chronological) throws ServiceException;

  public List<DealAcquire> getRedeemedDealAcquires(final UUID merchantId, final String redemptionCode) throws ServiceException;

  public void createActivationCodes(final UUID dealOfferId, final int totalCodes) throws ServiceException;

  public List<String> getActivationCodes(final UUID dealOfferId) throws ServiceException;

  public List<ActivationSummary> getActivationSummaries(final UUID merchantId) throws ServiceException;

  public void deleteCustomer(final UUID customerId) throws ServiceException;

  /**
   * Copies all deals of an existing deal offer and assigns them to the newly copied deal offer. The
   * deal offer is set to in-active and the title of the new deal offer is
   * "copy of {orginalDealOfferTitle}"
   * 
   * @param dealOffer
   * @return
   * @throws ServiceException
   */
  public DealOffer deepCopyDealOffer(final UUID dealOfferId) throws ServiceException;

  public void setIsCustomerEmailValid(final String email, boolean isValid) throws ServiceException;

  public void setIsMerchantEmailValid(final String email, boolean isValid) throws ServiceException;

  /**
   * Gets the unique property keys
   * 
   * @param entityInterfaceClass
   * @return
   * @throws ServiceException
   */
  public List<String> getUniqueProperyKeys(final Class<? extends PropertyEntity> entity) throws ServiceException;

  /**
   * Gets all entity objects that match the property key/value
   * 
   * @param type
   * @param propertykey
   * @param propertyValue
   * @return
   * @throws ServiceException
   */
  public <T extends PropertyEntity> List<? extends T> getEntityByProperty(final Class<T> type, final String propertykey, final String propertyValue)

  throws ServiceException;

  /**
   * Gets all entity objects that match the every property key/value in the map
   * 
   * @param type
   * @param properties
   * @return
   * @throws ServiceException
   */
  public <T extends PropertyEntity> List<? extends T> getEntityByProperties(final Class<T> type, final PropertyCriteria propertyCriteria)
      throws ServiceException;

  /**
   * Retrieves a list of dealOfferPurchase UUIDs by dealOfferId and propertyCriteria
   * 
   * @param dealOfferId
   * @param propertyCriteria - can be null or have no filters
   * @return a list of dealOfferPurchase UUIDs
   * @throws ServiceException
   */
  public List<UUID> getDealOfferPurchaseIds(final UUID custoemrId, final UUID dealOfferId, final PropertyCriteria propertyCriteria)
      throws ServiceException;

  /**
   * Creates a group of merchant codes
   * 
   * @param merchantId
   * @param createdByMerchantAccountId
   * @param codeGroupTitle
   * @param codeGroupNotes
   * @param totalCodes
   * @return
   * @throws ServiceExeption
   */
  public MerchantCodeGroup createMerchantCodeGroup(final Merchant merchant, final Long createdByMerchantAccountId, final UUID publisherId,
      final String codeGroupTitle, final String codeGroupNotes, final short totalCodes) throws ServiceException;

  public MerchantCodeGroup getMerchantCodeGroupForCode(final String code) throws ServiceException;

  public long getDailyTrackingCodeCountByPublisher(final UUID publisherId) throws ServiceException;

  public boolean isMerchantCodeValid(final String code, final UUID dealOfferId) throws ServiceException;

  public <T extends PropertyEntity> void saveProperties(final T entity, final Properties properties) throws ServiceException;

  public void processBraintreeNotification(final String btSignatureParam, final String btPayloadParam) throws ServiceException;

  /**
   * Refunds or voids the transaction associated with the dealOfferPurchase and optionally will
   * remove the dealAquires.
   * 
   * @param dealOfferPurchase
   * @param removeDealAcquires
   * @return the RefundType
   * @throws ServiceException
   */
  public RefundResult refundOrVoid(final DealOfferPurchase dealOfferPurchase, final boolean removeDealAcquires) throws ServiceException;

}
