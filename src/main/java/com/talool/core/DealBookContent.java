package com.talool.core;

import java.io.Serializable;

/**
 * DealBookContent
 * 
 * @author clintz
 * 
 */
public interface DealBookContent extends Identifiable, Serializable, TimeAware
{
	public DealBook getDealBook();

	public Deal getMerchantDeal();

	public void setPageNumber(Integer pageNumber);

	public Integer getPageNumber();

}
