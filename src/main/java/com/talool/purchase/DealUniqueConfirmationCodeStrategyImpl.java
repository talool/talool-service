package com.talool.purchase;

import org.apache.commons.lang.RandomStringUtils;

import com.talool.core.purchase.RedemptionCodeStrategy;

/**
 * A simple upper-case alphanumeric confirmation code generator.
 * 
 * Deal uniqueness simply means this algorithm is intended to create codes in a
 * space that minimizes the collisions of codes within dealAcquires For example,
 * if a deal has 100,000 deal Acquires, it would be great to have codes unique
 * enough that the probability of collision is low.
 * 
 * This algorithm produces a string length codeLength with only upper case
 * alphanumeric characters. Therefore we have:
 * 
 * 36^6 = 2,176,782,336 possible outcomes, and 74,769 generations before a 20%
 * chance of collision . This very well minimizes the chance of collisions when
 * considering the number of dealAcquires possible for a given deal
 * 
 * @see http://en.wikipedia.org/wiki/Birthday_problem and
 * @author clintz
 * 
 */
public class DealUniqueConfirmationCodeStrategyImpl implements RedemptionCodeStrategy
{
	private int codeLength = 6;

	private static final char[] SYMBOLS = new char[36];

	static
	{
		for (int idx = 0; idx < 10; ++idx)
		{
			SYMBOLS[idx] = (char) ('0' + idx);
		}

		for (int idx = 10; idx < 36; ++idx)
		{
			SYMBOLS[idx] = (char) ('A' + idx - 10);
		}

	}

	public DealUniqueConfirmationCodeStrategyImpl(final int codeLength)
	{
		this.codeLength = codeLength;
	}

	@Override
	public String generateCode()
	{
		return RandomStringUtils.random(codeLength, 0, SYMBOLS.length - 1, true, true, SYMBOLS);
	}

	public int getCodeLength()
	{
		return codeLength;
	}

}
