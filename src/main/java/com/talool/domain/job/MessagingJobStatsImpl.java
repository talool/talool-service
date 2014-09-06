package com.talool.domain.job;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.talool.messaging.job.MessagingJobStats;

/**
 * A Hibernate mapped MessagingJobStats Implementation
 * 
 * @author clintz
 * 
 */

public class MessagingJobStatsImpl implements MessagingJobStats
{
	private static final long serialVersionUID = -1650156637015368504L;

	private Long sends = 0l;

	private Long emailOpens = 0l;

	private Long giftOpens = 0l;

	private Long usersTargeted = 0l;

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
