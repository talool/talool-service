package com.talool.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * 
 * @author clintz
 * 
 */
public class PropertyCriteria
{
	private List<Filter> filters;

	public void setFilters(final Filter... filters)
	{
		this.filters = new ArrayList<Filter>(filters.length);
		this.filters.addAll(Arrays.asList(filters));
	}

	public static class Filter
	{
		String key;
		String val;
		Type type;
		List<Filter> groupedFilters;

		public enum Type
		{
			ValueEqual, ValueNotEqual, KeyExists, KeyDoesNotExist, KeyDoesNotExistOrPropertiesNull, AND, OR
		}

		Filter(String key, String val, Type type)
		{
			this.key = key;
			this.val = val;
			this.type = type;
		}

		public static Filter equal(String key, String value)
		{
			return new Filter(key, value, Type.ValueEqual);
		}

		public static Filter equal(String key, Integer value)
		{
			return new Filter(key, Integer.toString(value), Type.ValueEqual);
		}

		public static Filter equal(String key, Long value)
		{
			return new Filter(key, Long.toString(value), Type.ValueEqual);
		}

		public static Filter equal(String key, Boolean value)
		{
			return new Filter(key, Boolean.toString(value), Type.ValueEqual);
		}

		public static Filter equal(String key, Double value)
		{
			return new Filter(key, Double.toString(value), Type.ValueEqual);
		}

		public static Filter notEqual(String key, String value)
		{
			return new Filter(key, value, Type.ValueNotEqual);
		}

		public static Filter notEqual(String key, Integer value)
		{
			return new Filter(key, Integer.toString(value), Type.ValueNotEqual);
		}

		public static Filter notEqual(String key, Long value)
		{
			return new Filter(key, Long.toString(value), Type.ValueNotEqual);
		}

		public static Filter notEqual(String key, Boolean value)
		{
			return new Filter(key, Boolean.toString(value), Type.ValueNotEqual);
		}

		public static Filter notEqual(String key, Double value)
		{
			return new Filter(key, Double.toString(value), Type.ValueNotEqual);
		}

		public static Filter keyExists(String key)
		{
			return new Filter(key, null, Type.KeyExists);
		}

		/**
		 * Only filters entities with properties
		 * 
		 * @param key
		 * @return
		 */
		public static Filter keyDoesNotExists(String key)
		{
			return new Filter(key, null, Type.KeyDoesNotExist);
		}

		/**
		 * Returns entities where the key doesn't exist. Entities with null
		 * properites will be returned
		 * 
		 * @param key
		 * @return
		 */
		public static Filter keyDoesNotExistsOrPropertiesNull(final String key)
		{
			return new Filter(key, null, Type.KeyDoesNotExistOrPropertiesNull);
		}

		public static Filter and(final Filter... filters)
		{
			Filter filter = new Filter(null, null, Type.AND);
			filter.groupedFilters = new ArrayList<Filter>();
			filter.groupedFilters.addAll(Arrays.asList(filters));
			return filter;
		}

		public static Filter or(final Filter... filters)
		{
			Filter filter = new Filter(null, null, Type.OR);
			filter.groupedFilters = new ArrayList<Filter>();
			filter.groupedFilters.addAll(Arrays.asList(filters));
			return filter;
		}
	}

	private String buildFilterClause(final Filter filter, final String propertyColumnName)
	{
		final StringBuilder sb = new StringBuilder();

		switch (filter.type)
		{
			case AND:
			case OR:

				if (CollectionUtils.isNotEmpty(filter.groupedFilters))
				{
					sb.append(" ");

					if (filter.groupedFilters.size() == 1)
					{
						sb.append(buildFilterClause(filter.groupedFilters.get(0), propertyColumnName));
						sb.append(" ");
					}
					else
					{
						for (Filter f : filter.groupedFilters)
						{
							if (sb.toString().equals(" "))
							{
								sb.append(buildFilterClause(f, propertyColumnName));
							}
							else
							{
								sb.append(filter.type.toString()).append(" ").append(buildFilterClause(f, propertyColumnName));
							}

						}
					}
				}

				break;

			case ValueEqual:
				sb.append("hs_value(").append(propertyColumnName).append(",'").append(filter.key).append("') = '").append(filter.val).append("' ");
				break;

			case ValueNotEqual:
				sb.append("hs_value(").append(propertyColumnName).append(",'").append(filter.key).append("') != '").append(filter.val).append("' ");
				break;

			case KeyExists:
				sb.append("hs_key_exist(").append(propertyColumnName).append(",'").append(filter.key).append("') = true ");
				break;

			case KeyDoesNotExistOrPropertiesNull:
				sb.append("(").append(propertyColumnName).append(" is null OR ").
						append(" hs_key_exist(").append(propertyColumnName).append(",'").append(filter.key).append("') = false ) ");
				break;

			case KeyDoesNotExist:
				sb.append("hs_key_exist(").append(propertyColumnName).append(",'").append(filter.key).append("') = false ");
				break;

		}

		return sb.toString();
	}

	public String buildFilterClause(final String propertyColumnName)
	{
		final StringBuilder sb = new StringBuilder();

		for (Filter filter : filters)
		{
			sb.append(buildFilterClause(filter, propertyColumnName));

		}

		return sb.toString();

	}

	public static void main(String args[])
	{
		PropertyCriteria criteria = new PropertyCriteria();
		// criteria.addFilters(
		// Filter.or(Filter.equal("fundraising_book", "1"),
		// Filter.equal("fundraising_book", "2"),
		// Filter.equal("fundraising_book", "3")
		//
		// ));

		criteria.setFilters(Filter.and(Filter.equal("fundraising_book", "1"),
				Filter.notEqual("first_name", "chris")));

		String clause = criteria.buildFilterClause("props");

		System.out.println(clause);

	}
}
