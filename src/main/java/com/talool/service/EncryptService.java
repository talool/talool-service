package com.talool.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.EncoderException;
/**
 * PasswordEncryptService for SHA/MD2/MD5 encryption
 * 
 * Borrwed Internet code and refactored, trimmedm, and modified for Java5 enums,
 * singleton, etc.
 * 
 * convertBase16to32 and convertBase32to16 was written by chris.lintz
 * exclusively These functions were written with performance in mind. They could
 * easily be modifed to account for a generic base conversion (base between 0
 * and 36), but the trade off is performance and/or more memory caching
 * BigIntegers.
 * 
 * @author clintz
 * @todo replace this with jasypt/hibernate integration or create custom
 *       UserType with this service functionality
 * 
 */
public class EncryptService
{
	public static enum MESSAGE_DIGEST
	{
		SHA, MD5, MD2
	}
	private static int BASE36 = 36;
	private static int BASE16 = 16;
	private static Map<Character, BigInteger> charMap = new HashMap<Character, BigInteger>(BASE36);
	private static Map<Integer, BigInteger> base36posMap = new HashMap<Integer, BigInteger>(BASE36);
	private static Map<Integer, BigInteger> base16posMap = new HashMap<Integer, BigInteger>(BASE16);
	private static String HEX[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c",
			"d", "e", "f" };

	private static String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	static
	{

		for (int i = 0; i < BASE36; i++)
			charMap.put(chars.charAt(i), new BigInteger(String.valueOf(i)));
		BigInteger bi = new BigInteger(String.valueOf(BASE36));
		for (int i = 0; i < BASE36; i++)
			base36posMap.put(i, bi.pow(i));
		bi = new BigInteger(String.valueOf(BASE16));
		for (int i = 0; i < BASE16; i++)
			base16posMap.put(i, bi.pow(i));

	}

	public static String convertBase16to36(String value) throws EncoderException
	{
		BigInteger bi = BigInteger.ZERO;

		// flip the string around so positions are correct for 36^0, 36^1, etc
		StringBuilder sb = new StringBuilder(value.toUpperCase());
		String flippedVal = sb.reverse().toString();

		for (int i = 0; i < flippedVal.length(); i++)
		{
			bi = bi.add(charMap.get(flippedVal.charAt(i)).multiply(base16posMap.get(i))); // i.e.
			// for
			// character
			// n,
			// 23 *
			// 36^n
		}

		sb.setLength(0);
		// now we have decimal form, need to convert the decimal to base
		BigInteger base = new BigInteger(String.valueOf(BASE36));

		do
		{
			BigInteger remainder = (bi.mod(base));
			sb.append(chars.substring(remainder.intValue(), remainder.intValue() + 1));
			bi = bi.subtract(remainder).divide(base);
		}
		while (!bi.equals(BigInteger.ZERO));

		return sb.reverse().toString();

	};

	public static String convertBase36to16(String value) throws EncoderException
	{
		BigInteger bi = BigInteger.ZERO;

		// flip the string around so positions are correct for 36^0, 36^1, etc
		StringBuilder sb = new StringBuilder(value.toUpperCase());
		String flippedVal = sb.reverse().toString();

		for (int i = 0; i < flippedVal.length(); i++)
		{
			bi = bi.add(charMap.get(flippedVal.charAt(i)).multiply(base36posMap.get(i))); // i.e.
			// for
			// character
			// n,
			// 23 *
			// 36^n
		}

		return bi.toString(16);

	}

	private EncryptService()
	{}

	public static String MD5(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException
	{
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] sha1hash = new byte[32];
		md.update(text.getBytes("UTF-8"), 0, text.length());
		sha1hash = md.digest();
		return byteArrayToHexString(sha1hash);
	}

	static String byteArrayToHexString(byte in[])
	{

		byte ch = 0x00;
		int i = 0;
		if (in == null || in.length <= 0)
			return null;

		StringBuilder out = new StringBuilder(in.length * 2);

		while (i < in.length)
		{
			ch = (byte) (in[i] & 0xF0);
			ch = (byte) (ch >>> 4);
			ch = (byte) (ch & 0x0F);
			out.append(HEX[(int) ch]);
			ch = (byte) (in[i] & 0x0F);
			out.append(HEX[(int) ch]);
			i++;
		}

		String rslt = new String(out);
		return rslt;
	}
}
