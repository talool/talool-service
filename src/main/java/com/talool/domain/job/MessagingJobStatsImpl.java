package com.talool.domain.job;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.talool.messaging.job.MessagingJob;
import com.talool.messaging.job.MessagingJobStats;

/**
 * A Hibernate mapped MessagingJobStats Implementation
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "messaging_job_stats", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class MessagingJobStatsImpl implements MessagingJobStats
{
	private static final long serialVersionUID = -1650156637015368504L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_mjs_seq")
	@SequenceGenerator(name = "my_mjs_seq", sequenceName = "messaging_job_stats_messaging_job_stats_id_seq'")
	@Column(name = "messaging_job_stats_id", unique = true, nullable = false)
	private Long id;

	@OneToOne(targetEntity = MessagingJobImpl.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "messaging_job_id")
	private MessagingJob messagingJob;

	@Column(name = "sends", nullable = false)
	private Long sends;

	@Column(name = "email_opens", nullable = false)
	private Long emailOpens;

	@Column(name = "gift_opens", nullable = false)
	private Long giftOpens;

	@Column(name = "users_targeted", nullable = false)
	private Long usersTargeted;

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public MessagingJob getMessagingJob()
	{
		return messagingJob;
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
