package com.talool.core;

import java.io.Serializable;
import java.util.UUID;

/**
 * Simple Bean object for activation code summaries for a deal offer
 * 
 * @author clintz
 * 
 */
public class ActivationSummary implements Serializable
{
	private static final long serialVersionUID = 355337005000488130L;
	private UUID dealOfferId;
	private String title;
	private int totalCodes;
	private Integer totalActivations;

	public ActivationSummary()
	{}

	public String getTitle()
	{
		return title;
	}

	public int getTotalCodes()
	{
		return totalCodes;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setTotalCodes(int totalCodes)
	{
		this.totalCodes = totalCodes;
	}

	public UUID getDealOfferId()
	{
		return dealOfferId;
	}

	public void setDealOfferId(UUID dealOfferId)
	{
		this.dealOfferId = dealOfferId;
	}

	public int getTotalActivations()
	{
		return totalActivations;
	}

	public void setTotalActivations(Integer totalActivations)
	{
		if (totalActivations == null)
		{
			this.totalActivations = 0;
		}
		else
		{
			this.totalActivations = totalActivations;
		}

	}

}
