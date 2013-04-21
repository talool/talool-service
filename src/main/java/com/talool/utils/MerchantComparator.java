package com.talool.utils;

import java.util.Comparator;

import com.talool.core.Merchant;

/**
 * 
 * @author clintz
 * 
 */
public class MerchantComparator implements Comparator<Merchant>
{
	public enum ComparatorType
	{
		Name
	};

	private ComparatorType type;

	public MerchantComparator(final ComparatorType type)
	{
		this.type = type;
	}

	@Override
	public int compare(Merchant o1, Merchant o2)
	{
		int compare = 0;
		switch (type)
		{
			case Name:
				compare = o1.getName().compareTo(o2.getName());
				break;
		}

		return compare;

	}

}
