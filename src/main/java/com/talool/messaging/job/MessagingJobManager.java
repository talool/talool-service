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
import com.talool.messaging.job.task.AbstractMessagingTask;
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
	private static final int TERMINATION_TIMEOUT_IN_SECS = 30;
	private static final int DEFAULT_THREAD_POOL_SIZE = 4;
	private static final int DEFAULT_MAX_THREAD_POOL_SIZE = 4;
	private static final long DEFAULT_SLEEP_TIME_IN_MILLS = 24000l;
	private static MessagingJobManager INSTANCE;

	private MessagingService messagingService;

	// daemon threads and shutdown hooks are registered.
	private ExecutorService jobPool = MoreExecutors.getExitingExecutorService(new ThreadPoolExecutor(DEFAULT_THREAD_POOL_SIZE,
			DEFAULT_MAX_THREAD_POOL_SIZE, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), TERMINATION_TIMEOUT_IN_SECS,
			TimeUnit.SECONDS);

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
					final List<MessagingJob> messagingJobs = messagingService.getJobsToProcess();

					for (final MessagingJob job : messagingJobs)
					{
						final AbstractMessagingTask<? extends MessagingJob> task = MessagingTaskFactory.createMessagingTask(job);
						if (task == null)
						{
							LOG.error("Messaging job type not known. messagingJobId " + job.getId());
							continue;
						}
						if (LOG.isDebugEnabled())
						{
							LOG.debug("Submitting MerchantGiftJobTask with jobId:" + job.getId());
						}
						// don't capture the future. for now the job is responsible for everything
						submitTask(task);
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

			LOG.info("MessagingJobManager thread ended");
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
				LOG.info("Shutting down MessagingJobManager.  No new tasks will be submitted..");
				jobPool.shutdown();
				isRunning = false;
				messagingManagerThread.interrupt();
				try
				{
					// wait for tasks to terminate. the executor is already and ExitingExecutorService, but we want
					// to capture the LOG info messages by waiting anyways
					if (!jobPool.awaitTermination(TERMINATION_TIMEOUT_IN_SECS, TimeUnit.SECONDS))
					{
						// cancel currently executing tasks
						jobPool.shutdownNow();
						LOG.info("MessagingJobManager jobPool shutdownNow - tasks may end in bad state.");
					}
					else
					{
						LOG.info("MessagingJobManager and jobPool shutdown gracefully.");
					}
				}
				catch (InterruptedException ie)
				{
					// re-cancel if current thread also interrupted
					jobPool.shutdownNow();
					// Preserve interrupt status
					Thread.currentThread().interrupt();
				}

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
		Future<? extends MessagingJob> future = jobPool.submit(task);
		return future;
	}

}
