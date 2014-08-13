package com.talool.messaging.job;

import java.io.Serializable;

/**
 * Messaging Job Analytics
 * 
 * @author clintz
 * 
 */
public interface MessagingJobStats extends Serializable
{
	public Long getSends();

	public Long getEmailOpens();

	public Long getGiftOpens();

	public Long getUsersTargeted();
}
