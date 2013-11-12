package com.talool.core;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 
 * @author clintz
 * 
 */
public class DealOfferSummary implements IdentifiableUUID, Serializable, TimeAware
{
	private static final long serialVersionUID = -642661768420105839L;

	private final UUID id;
	private final DealType dealType;
	private final String title;
	private final String summary;
	private final String imageUrl;
	private final Double price;
	private final Double distanceInMeters;
	private final Date expires;
	private final boolean isActive;
	private final int dealCount;
	private final Date created;
	private final Date updated;

	private DealOfferSummary(final DealOfferSummaryBuilder builder)
	{
		this.id = builder.id;
		this.dealType = builder.dealType;
		this.title = builder.title;
		this.summary = builder.summary;
		this.imageUrl = builder.imageUrl;
		this.price = builder.price;
		this.distanceInMeters = builder.distanceInMeters;
		this.expires = builder.expires;
		this.isActive = builder.isActive;
		this.dealCount = builder.dealCount;
		this.created = builder.created;
		this.updated = builder.updated;
	}

	public static class DealOfferSummaryBuilder
	{
		private final UUID id;
		private final DealType dealType;
		private final String title;
		private final boolean isActive;
		private String summary;
		private String imageUrl;
		private Double price;
		private Double distanceInMeters;
		private Date expires;

		private int dealCount;
		private Date created;
		private Date updated;

		public DealOfferSummaryBuilder(final UUID dealOfferId, final DealType dealType, final String title, final boolean isActive)
		{
			id = dealOfferId;
			this.dealType = dealType;
			this.title = title;
			this.isActive = isActive;
		}

		public DealOfferSummaryBuilder summary(final String summary)
		{
			this.summary = summary;
			return this;
		}

		public DealOfferSummaryBuilder imageUrl(final String imageUrl)
		{
			this.imageUrl = imageUrl;
			return this;
		}

		public DealOfferSummaryBuilder price(final Double price)
		{
			this.price = price;
			return this;
		}

		public DealOfferSummaryBuilder distanceInMeters(final Double distanceInMeters)
		{
			this.distanceInMeters = distanceInMeters;
			return this;
		}

		public DealOfferSummaryBuilder expires(final Date expires)
		{
			this.expires = expires;
			return this;
		}

		public DealOfferSummaryBuilder dealCount(final int dealCount)
		{
			this.dealCount = dealCount;
			return this;
		}

		public DealOfferSummaryBuilder created(final Date created)
		{
			this.created = created;
			return this;
		}

		public DealOfferSummaryBuilder updated(final Date updated)
		{
			this.updated = updated;
			return this;
		}

		public DealOfferSummary build()
		{
			return new DealOfferSummary(this);
		}

	}

	@Override
	public UUID getId()
	{
		return id;
	}

	public DealType getDealType()
	{
		return dealType;
	}

	public String getTitle()
	{
		return title;
	}

	public String getSummary()
	{
		return summary;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	public Double getPrice()
	{
		return price;
	}

	public Date getExpires()
	{
		return expires;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public int getDealCount()
	{
		return dealCount;
	}

	@Override
	public Date getCreated()
	{
		return created;
	}

	@Override
	public Date getUpdated()
	{
		return updated;
	}

	public Double getDistanceInMeters()
	{
		return distanceInMeters;
	}

}
