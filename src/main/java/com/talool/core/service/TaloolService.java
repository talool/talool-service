package com.talool.core.service;

import java.util.List;
import java.util.Set;

import com.talool.core.AccountType;
import com.talool.core.SocialNetwork;
import com.talool.core.Tag;

/**
 * 
 * @author clintz
 */
public interface TaloolService extends MerchantService, CustomerService
{
	public SocialNetwork getSocialNetwork(final String name) throws ServiceException;

	public boolean emailExists(final AccountType accountType, final String email)
			throws ServiceException;

	public Tag getTag(String tagName) throws ServiceException;

	public List<Tag> getTags() throws ServiceException;

	public void addTags(List<Tag> tags) throws ServiceException;

	public Set<Tag> getOrCreateTags(String... tags) throws ServiceException;

	public void refresh(Object obj) throws ServiceException;

	public Long sizeOfCollection(final Object collection) throws ServiceException;

}
