package com.talool.utils;

import java.util.Comparator;

import com.talool.core.DealAcquire;

/**
 * 
 * @author clintz
 * 
 */
public class DealAcquireComparator implements Comparator<DealAcquire>
{
	public enum ComparatorType
	{
		DealTitle
	};

	private ComparatorType type;

	public DealAcquireComparator(final ComparatorType type)
	{
		this.type = type;
	}

	@Override
	public int compare(DealAcquire o1, DealAcquire o2)
	{
		int compare = 0;
		switch (type)
		{
			case DealTitle:
				compare = o1.getDeal().getTitle().compareTo(o2.getDeal().getTitle());
				break;
		}

		return compare;

	}

}
