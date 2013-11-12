package com.talool.cache;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * A cache for deal offers
 * 
 * @author clintz
 * 
 */
public final class DealOfferCache
{
	private static final Logger LOG = LoggerFactory.getLogger(DealOfferCache.class);

	private final DealOfferCache instance = new DealOfferCache();

	private final Cache<UUID, String> dealOfferCache;

	private DealOfferCache()
	{
		dealOfferCache = CacheBuilder.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(10, TimeUnit.MINUTES)
				.removalListener(new RemovalListener<UUID, String>()
				{

					@Override
					public void onRemoval(final RemovalNotification<UUID, String> notification)
					{
						LOG.info(String.format("removing dealOfferId %s from cache", notification.getKey()));
					}

				})
				.build();
	}

}
