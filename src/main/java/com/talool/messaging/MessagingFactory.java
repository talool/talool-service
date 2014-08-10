package com.talool.messaging;

import com.talool.domain.job.MessagingJobBuilder;

/**
 * Factory for creating Messaging related objects
 * 
 * @author clintz
 * 
 */
public class MessagingFactory
{
	public static MessagingJobBuilder newMessagingJobBuilder()
	{
		return new MessagingJobBuilder();
	}
}
