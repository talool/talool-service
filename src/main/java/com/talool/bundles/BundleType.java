package com.talool.bundles;

/**
 * 
 * @author clintz
 * 
 */
public enum BundleType
{
	ACTIVITY("ActivityBundle"),
	ERRORS("ErrorsBundle");

	private String bundleName;

	BundleType(String bundleName)
	{
		this.bundleName = bundleName;
	}

	public String getBundleName()
	{
		return bundleName;
	}

	@Override
	public String toString()
	{
		return bundleName;
	}
}