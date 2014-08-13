package com.talool.domain.job;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import com.talool.core.Customer;
import com.talool.core.MerchantAccount;
import com.talool.domain.CustomerImpl;
import com.talool.domain.MerchantAccountImpl;
import com.talool.messaging.job.JobState;
import com.talool.messaging.job.MessagingJob;
import com.talool.messaging.job.RecipientStatus;

/**
 * A Hibernate mapped Messaging Job Implementation
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "messaging_job", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class MessagingJobImpl implements MessagingJob
{
	private static final long serialVersionUID = -4134065711388463064L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_mj_seq")
	@SequenceGenerator(name = "my_mj_seq", sequenceName = "messaging_job_messaging_job_id_seq")
	@Column(name = "messaging_job_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = MerchantAccountImpl.class, cascade = CascadeType.ALL)
	@Fetch(value = FetchMode.JOIN)
	@JoinColumn(name = "created_by_merchant_account_id")
	private final MerchantAccount createdByMerchantAccount;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = CustomerImpl.class, cascade = CascadeType.ALL)
	@Fetch(value = FetchMode.JOIN)
	@JoinColumn(name = "from_customer_id")
	private final Customer fromCustomer;

	@Type(type = "jobState")
	@Column(name = "job_state", nullable = false, columnDefinition = "job_state")
	private JobState jobState;

	@Column(name = "create_dt", unique = false, insertable = false, updatable = false)
	private Date created;

	@Column(name = "scheduled_start_dt", unique = false, insertable = true, updatable = true)
	private Date scheduledStartDate;

	@Column(name = "job_notes", unique = false, nullable = true, length = 128)
	private String notes;

	@Column(name = "sends")
	private Long sends = 0l;

	@Column(name = "email_opens")
	private Long emailOpens = 0l;

	@Column(name = "gift_opens")
	private Long giftOpens = 0l;

	@Column(name = "users_targeted")
	private Long usersTargeted = 0l;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "messagingJob", targetEntity = RecipientStatusImpl.class)
	private List<RecipientStatus> messagingReceipientStatuses;

	public MessagingJobImpl(final MerchantAccount createdByMerchantAccount, final Customer fromCustomer, final Date scheduledStartDate,
			final String notes)
	{
		this.createdByMerchantAccount = createdByMerchantAccount;
		this.fromCustomer = fromCustomer;
		this.jobState = JobState.STOPPED;
		this.notes = notes;
		this.scheduledStartDate = scheduledStartDate;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public MerchantAccount getCreatedByMerchantAccount()
	{
		return createdByMerchantAccount;
	}

	@Override
	public Customer getFromCustomer()
	{
		return fromCustomer;
	}

	@Override
	public JobState getJobState()
	{
		return jobState;
	}

	@Override
	public Date getCreated()
	{
		return created;
	}

	@Override
	public Date getScheduledStartDate()
	{
		return scheduledStartDate;
	}

	@Override
	public List<RecipientStatus> getMessagingReceipientStatuses()
	{
		return messagingReceipientStatuses;
	}

	@Override
	public String getJobNotes()
	{
		return notes;
	}

	@Override
	public void setJobNotes(final String notes)
	{
		this.notes = notes;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
