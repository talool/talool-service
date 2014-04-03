package com.talool.core.service;

import java.util.Map;

/**
 * An interface for services that support same JVM level services where request
 * headers can passed and maintained in threadlocal for the remaining of the
 * request
 * 
 * @author clintz
 * 
 */
public interface RequestHeaderSupport
{
	public void setRequestHeaders(final Map<String, String> headers);
}
