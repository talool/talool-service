package com.talool.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.talool.core.DealOffer;
import com.talool.service.ServiceConfig;
import com.talool.service.ServiceFactory;
import com.talool.stats.DealOfferMetadata;
import com.talool.stats.DealOfferMetrics;

/**
 * A cache for deal offer metrics and metadata
 * 
 * @author clintz
 * 
 */
public final class DealOfferMetadataCache
{
	private static final Logger LOG = LoggerFactory.getLogger(DealOfferMetadataCache.class);

	private static DealOfferMetadataCache instance;

	private volatile Map<UUID, DealOfferMetadata> dealOfferCache;

	private CacheRefreshThread cacheRefreshThread;

	public static DealOfferMetadataCache get()
	{
		return instance;
	}

	private class CacheRefreshThread extends Thread
	{

		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					final List<DealOffer> dealOffers = ServiceFactory.get().getTaloolService().getDealOffers();
					boolean hasDealOffers = !CollectionUtils.isEmpty(dealOffers);

					LOG.info("caching " + (hasDealOffers ? dealOffers.size() : "0" + " deal offers"));

					if (hasDealOffers)
					{
						final Map<UUID, DealOfferMetadata> newDealOfferCache = new HashMap<UUID, DealOfferMetadata>();
						final Map<UUID, DealOfferMetrics> metrics = ServiceFactory.get().getTaloolService().getDealOfferMetrics();

						for (DealOffer dealOffer : dealOffers)
						{
							final DealOfferMetrics dealOfferMetrics = metrics.get(dealOffer.getId());
							newDealOfferCache.put(dealOffer.getId(), new DealOfferMetadata(dealOffer, dealOfferMetrics));

							if (LOG.isDebugEnabled())
							{
								if (dealOfferMetrics == null)
								{
									LOG.info(String.format("%s has no dealOfferMetrics yet (probably no merchants for dealOffer yet)", dealOffer.getTitle()));
								}
								else
								{
									LOG.info(String.format("%s has %d merchants and %d deals", dealOffer.getTitle(),
											dealOfferMetrics.getLongMetrics().get(DealOfferMetrics.MetricType.TotalMerchants.toString()), dealOfferMetrics.getLongMetrics()
													.get(DealOfferMetrics.MetricType.TotalDeals.toString())));
								}

							}
						}

						dealOfferCache = newDealOfferCache;
					}

				}
				catch (Exception e)
				{
					LOG.error("Problem building DealOfferMetricCache", e);
				}

				try
				{
					Thread.sleep(ServiceConfig.get().getTagCacheRefreshInSecs() * 1000);
				}
				catch (InterruptedException e)
				{
					LOG.error("Problem sleeping", e);
				}

			}

		}
	}

	public DealOfferMetadata getDealOfferMetrics(final UUID dealOfferId)
	{
		return dealOfferCache.get(dealOfferId);
	}

	public synchronized static DealOfferMetadataCache createInstance()
	{
		if (instance == null)
		{
			instance = new DealOfferMetadataCache();
		}

		return instance;

	}

	private DealOfferMetadataCache()
	{
		dealOfferCache = new HashMap<UUID, DealOfferMetadata>();
		cacheRefreshThread = new CacheRefreshThread();
		cacheRefreshThread.setName("DealOfferMetricCacheRefreshThread");
		cacheRefreshThread.setDaemon(true);
		cacheRefreshThread.start();
	}

}
