package com.talool.messaging.job;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * MessagingJobManager is responsible for submitting a
 * 
 * @author clintz
 * 
 */
public final class MessagingJobManager
{
	private static final Logger LOG = LoggerFactory.getLogger(MessagingJobManager.class);
	private static final int DEFAULT_THREAD_POOL_SIZE = 4;
	private static final MessagingJobManager INSTANCE = new MessagingJobManager();

	private ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE));

	private MessagingJobManager()
	{
		LOG.info("Starting MessagingJobPool with poolSize " + DEFAULT_THREAD_POOL_SIZE);
	};

	public static MessagingJobManager get()
	{
		return INSTANCE;
	}

	private class MessagingJobTask implements Callable<MessagingJob>
	{
		final MessagingJob messagingJob;

		MessagingJobTask(final MessagingJob messagingJob)
		{
			this.messagingJob = messagingJob;
		}

		@Override
		public MessagingJob call() throws Exception
		{
			LOG.info("Call() starting job");
			return messagingJob;
		}

	}

	/**
	 * Submits a job to the pool and returns a future with an updated MessagingJob
	 * 
	 * @param messagingJob
	 * @return a listenable future
	 */
	public ListenableFuture<MessagingJob> submitJob(final MessagingJob messagingJob, final FutureCallback<MessagingJob> callback)
	{
		ListenableFuture<MessagingJob> future = service.submit(new MessagingJobTask(messagingJob));
		Futures.addCallback(future, callback);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Submitted job " + messagingJob.toString());
		}

		return future;
	}
}
