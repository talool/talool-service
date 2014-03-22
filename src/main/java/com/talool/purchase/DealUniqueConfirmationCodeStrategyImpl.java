package com.talool.purchase;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;

import com.talool.core.purchase.UniqueCodeStrategy;

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
public class DealUniqueConfirmationCodeStrategyImpl implements UniqueCodeStrategy
{
	private int codeLength = 6;

	private char[] ignoredChars;

	private final char[] symbols;

	public DealUniqueConfirmationCodeStrategyImpl(final int codeLength, final char... ignoredChars)
	{
		final Set<Character> ignoredSet = new HashSet<Character>();
		this.ignoredChars = ignoredChars;
		int totalChars = 0;
		int charIdx = 0;

		if (ignoredChars != null)
		{
			for (char chr : ignoredChars)
			{
				ignoredSet.add(chr);
			}
			totalChars = 36 - ignoredChars.length;

		}
		else
		{
			totalChars = 36;
		}

		this.symbols = new char[totalChars];

		for (int idx = 0; idx < 10; ++idx)
		{
			char chr = (char) ('0' + idx);
			if (ignoredSet.contains(chr))
			{
				continue;
			}
			else
			{
				symbols[charIdx++] = chr;
			}
		}

		for (int idx = 10; idx < 36; ++idx)
		{
			char chr = (char) ('A' + idx - 10);
			if (ignoredSet.contains(chr))
			{
				continue;
			}
			else
			{
				symbols[charIdx++] = chr;
			}
		}
	}

	@Override
	public String generateCode()
	{
		String code = null;
		try
		{
			code = RandomStringUtils.random(codeLength, 0, symbols.length - 1, true, true, symbols,
					SecureRandom.getInstance("SHA1PRNG"));
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

		return code;
	}

	public int getCodeLength()
	{
		return codeLength;
	}

	public char[] getSymbols()
	{
		return symbols;
	}

	public static void main(String args[])
	{
		DealUniqueConfirmationCodeStrategyImpl codeGen = new DealUniqueConfirmationCodeStrategyImpl(7, 'O', '0', 'M', 'A', 'B', 'C');

		for (char c : codeGen.getSymbols())
		{
			System.out.println(c + ",");
		}

		for (int i = 0; i < 100; i++)
		{
			System.out.println(codeGen.generateCode());
		}

	}
}
