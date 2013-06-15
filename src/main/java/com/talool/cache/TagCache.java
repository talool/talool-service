package com.talool.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.talool.core.Category;
import com.talool.core.Tag;
import com.talool.service.ServiceConfig;
import com.talool.service.ServiceFactory;

/**
 * 
 * @author clintz
 * 
 */
public final class TagCache
{
	private static final Logger LOG = LoggerFactory.getLogger(TagCache.class);
	private static TagCache instance;

	private volatile Map<Category, List<Tag>> categoryTagMap = new HashMap<Category, List<Tag>>();
	private volatile Map<String, Category> categoryNameMap = new HashMap<String, Category>();
	private volatile Map<String, Category> tagNameCategoryMap = new HashMap<String, Category>();
	private volatile List<Category> categories = new ArrayList<Category>();

	private TagRefreshThread tagRefreshThread;

	private class TagRefreshThread extends Thread
	{
		public TagRefreshThread()
		{
			super(TagRefreshThread.class.getSimpleName());
		}

		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					final Map<Category, List<Tag>> _categoryTagMap = ServiceFactory.get().getTaloolService()
							.getCategoryTags();

					final Map<String, Category> _categoryNameMap = new HashMap<String, Category>();

					final Map<String, Category> _tagNameCategoryMap = new HashMap<String, Category>();

					int totalTags = 0;

					if (MapUtils.isNotEmpty(_categoryTagMap))
					{

						for (final Category cat : _categoryTagMap.keySet())
						{
							totalTags += _categoryTagMap.get(cat).size();
							_categoryNameMap.put(normalizeName(cat.getName()), cat);

							for (final Tag tag : _categoryTagMap.get(cat))
							{
								_tagNameCategoryMap.put(normalizeName(tag.getName()), cat);
							}

						}
					}

					// set new maps
					categoryTagMap = _categoryTagMap;
					categoryNameMap = _categoryNameMap;
					categories = ImmutableList.<Category> builder().addAll(_categoryTagMap.keySet()).build();
					tagNameCategoryMap = ImmutableMap.<String, Category> builder()
							.putAll(_tagNameCategoryMap)
							.build();

					if (LOG.isDebugEnabled())
					{
						LOG.debug(String.format("Refreshed %d categories and %d total tags. Refreshing in %d secs", categoryTagMap
								.keySet()
								.size(), totalTags, ServiceConfig.get().getTagCacheRefreshInSecs()));

					}

				}
				catch (Exception e)
				{
					LOG.error("Problem refreshing categoryTagMap", e);
				}

				try
				{
					Thread.sleep(ServiceConfig.get().getTagCacheRefreshInSecs() * 1000);
				}
				catch (InterruptedException e)
				{
					LOG.error("Thread interuppted: " + e.getLocalizedMessage(), e);
				}
			}
		}

	}

	private static String normalizeName(String tagName)
	{
		if (tagName == null)
		{
			return null;
		}
		return tagName.toLowerCase();
	}

	public List<Tag> getTagsByCategoryName(final String categoryName)
	{
		final Category category = categoryNameMap.get(normalizeName(categoryName));
		if (category == null)
		{
			return null;
		}

		return categoryTagMap.get(category);
	}

	public Category getCategoryByTagName(final String tagName)
	{
		return tagNameCategoryMap.get(normalizeName(tagName));
	}

	public List<Category> getCategories()
	{
		return categories;
	}

	private TagCache(final int refreshInSecs)
	{
		tagRefreshThread = new TagRefreshThread();
		tagRefreshThread.setDaemon(true);
		tagRefreshThread.start();
	}

	public static TagCache get()
	{
		return instance;
	}

	public synchronized static TagCache createInstance(final int refreshInSecs)
	{
		if (instance == null)
		{
			instance = new TagCache(refreshInSecs);
		}

		return instance;

	}

}
