package com.talool.messaging.job;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final int DEFAULT_MAX_THREAD_POOL_SIZE = 4;
	private static final long DEFAULT_SLEEP_TIME_IN_MILLS = 12000l;
	private static MessagingJobManager INSTANCE;

	private MessagingService messagingService;

	// daemon threads and shutdown hooks are registered.
	private ExecutorService service = MoreExecutors.getExitingExecutorService(new ThreadPoolExecutor(DEFAULT_THREAD_POOL_SIZE,
			DEFAULT_MAX_THREAD_POOL_SIZE, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), 30, TimeUnit.SECONDS);

	private MessagingJobManagerThread messagingManagerThread;

	private volatile boolean isRunning = false;

	private class MessagingJobManagerThread extends Thread
	{
		MessagingJobManagerThread(String name)
		{
			super(name);
		}

		@Override
		public void run()
		{
			while (isRunning)
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
							// don't capture the future. for now the job is responsible for everything
							submitTask(new MerchantGiftTask((MerchantGiftJob) job));
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
					LOG.warn("MessagingJob Thread interupted.  A shutdown may be coming");
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
		isRunning = true;
		messagingManagerThread.start();

		// adding shutdown hook to gracefully stop messaging manager
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				// prevent new tasks from being submitted
				service.shutdown();
				isRunning = false;
				messagingManagerThread.interrupt();
			}
		});
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
	public Future<? extends MessagingJob> submitTask(final Callable<? extends MessagingJob> task)
	{
		Future<? extends MessagingJob> future = service.submit(task);
		return future;
	}

}
