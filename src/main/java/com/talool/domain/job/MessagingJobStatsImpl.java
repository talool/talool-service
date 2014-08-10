package com.talool.domain.job;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.talool.messaging.job.MessagingJobStats;

/**
 * 
 * @author clintz
 * 
 */
public class MessagingJobStatsImpl implements MessagingJobStats
{
	private static final long serialVersionUID = -1650156637015368504L;
	private Long jobId;
	private Long id;
	private Long sends;
	private Long emailOpens;
	private Long giftOpens;
	private Long usersTargeted;

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public Long getJobId()
	{
		return jobId;
	}

	@Override
	public Long getSends()
	{
		return sends;
	}

	@Override
	public Long getEmailOpens()
	{
		return emailOpens;
	}

	@Override
	public Long getGiftOpens()
	{
		return giftOpens;
	}

	@Override
	public Long getUsersTargeted()
	{
		return usersTargeted;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
