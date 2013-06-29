package com.talool.bundles;

import java.util.Locale;
import java.util.ResourceBundle;

import org.rythmengine.Rythm;

/**
 * 
 * @author clintz
 * 
 */
public final class BundleUtil
{
	public static String getValue(final BundleType bundleType, final Locale locale, final String key)
	{
		ResourceBundle labels =
				ResourceBundle.getBundle(bundleType.getBundleName(), locale);
		return labels.getString(key);
	}

	public static String render(final BundleType bundleType, final Locale locale, final String key, Object... vals)
	{
		final String message = getValue(bundleType, locale, key);

		if (message == null)
		{
			return null;
		}

		return Rythm.render(message, vals);

	}

}
