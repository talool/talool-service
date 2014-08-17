package com.talool.messaging.job;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.talool.core.service.ServiceException;
import com.talool.messaging.job.task.MerchantGiftTask;
import com.talool.service.MessagingService;

/**
 * MessagingJobManager is responsible for submitting a
 * 
 * @author clintz
 * 
 */
public class MessagingJobManager
{
	private static final Logger LOG = LoggerFactory.getLogger(MessagingJobManager.class);
	private static final int DEFAULT_THREAD_POOL_SIZE = 4;
	private static final long DEFAULT_SLEEP_TIME_IN_MILLS = 12000l;
	private static MessagingJobManager INSTANCE;

	private MessagingService messagingService;

	private ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE));

	private MessagingJobManagerThread messagingManagerThread;

	private class MessagingJobManagerThread extends Thread
	{
		MessagingJobManagerThread(String name)
		{
			super(name);
		}

		@Override
		public void run()
		{
			while (true)
			{
				LOG.info("Starting MessagingJobPool with poolSize " + DEFAULT_THREAD_POOL_SIZE);

				try
				{
					List<MessagingJob> messagingJobs = messagingService.getJobsToProcess();

					for (MessagingJob job : messagingJobs)
					{
						if (job instanceof MerchantGiftJob)
						{
							if (LOG.isDebugEnabled())
							{
								LOG.debug("Submitting MerchantGiftJobTask with jobId:" + job.getId());
							}
							submitTask(new MerchantGiftTask((MerchantGiftJob) job), null);
						}
						else
						{
							LOG.error("Job Type not recognized - jobId:" + job.getId());
						}
					}
				}
				catch (ServiceException e)
				{
					e.printStackTrace();
				}

				try
				{
					sleep(DEFAULT_SLEEP_TIME_IN_MILLS);
				}
				catch (InterruptedException e)
				{
					// ignore
				}
			}
		}
	}

	public static synchronized MessagingJobManager createInstance(final MessagingService messagingService)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new MessagingJobManager(messagingService);
		}

		return INSTANCE;
	}

	private MessagingJobManager(final MessagingService messagingService)
	{
		this.messagingService = messagingService;

		LOG.info("Starting MessagingJobPool with poolSize " + DEFAULT_THREAD_POOL_SIZE);
		messagingManagerThread = new MessagingJobManagerThread(MessagingJobManagerThread.class.getSimpleName());
		messagingManagerThread.start();
	};

	public static MessagingJobManager get()
	{
		return INSTANCE;
	}

	/**
	 * Submits a job to the pool and returns a future with an updated MessagingJob
	 * 
	 * @param messagingJob
	 * @return a listenable future
	 */
	public ListenableFuture<? extends MessagingJob> submitTask(final Callable<? extends MessagingJob> task,
			final FutureCallback<MessagingJob> callback)
	{
		ListenableFuture<? extends MessagingJob> future = service.submit(task);
		if (callback != null)
		{
			Futures.addCallback(future, callback);
		}

		return future;
	}

}
