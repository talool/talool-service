/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author clintz
 * 
 */
public enum Sex
{
	Male("M"), Female("F"), Unknown("U");

	private static Map<String, Sex> lookup = new HashMap<String, Sex>();

	static
	{
		for (final Sex stype : Sex.values())
		{
			lookup.put(stype.letter, stype);

		}
	}

	private String letter;

	private Sex(String letter)
	{
		this.letter = letter;
	}

	public String getLetter()
	{
		return letter;
	}

	public static Sex valueByLetter(String oneLetterSex)
	{
		final Sex sex = lookup.get(oneLetterSex);
		return sex == null ? Sex.Unknown : sex;
	}
}
