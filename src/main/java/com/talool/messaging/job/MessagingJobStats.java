package com.talool.messaging.job;

import java.io.Serializable;

import com.talool.core.Identifiable;

/**
 * Messaging Job Analytics
 * 
 * @author clintz
 * 
 */
public interface MessagingJobStats extends Identifiable, Serializable
{
	public Long getJobId();

	public Long getSends();

	public Long getEmailOpens();

	public Long getGiftOpens();

	public Long getUsersTargeted();
}
