package com.talool.domain.job;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

/**
 * A Hibernate mapped Messaging Job Implementation
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "messaging_job", catalog = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "job_type", discriminatorType = DiscriminatorType.STRING, length = 2)
@DiscriminatorValue("MJ")
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
	private MerchantAccount createdByMerchantAccount;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = CustomerImpl.class, cascade = CascadeType.ALL)
	@Fetch(value = FetchMode.JOIN)
	@JoinColumn(name = "from_customer_id")
	private Customer fromCustomer;

	@Type(type = "jobState")
	@Column(name = "job_state", nullable = false, columnDefinition = "job_state")
	private JobState jobState;

	@Column(name = "create_dt", unique = false, insertable = false, updatable = false)
	private Date created;

	@Column(name = "scheduled_start_dt", unique = false, insertable = true, updatable = true)
	private Date scheduledStartDate;

	@Column(name = "running_update_dt", unique = false, insertable = true, updatable = true)
	private Date runningUpdateTime;

	@Column(name = "job_notes", unique = false, nullable = true, length = 128)
	private String notes;

	// stats below should not update on saves. Only HQL queries should increment
	@Column(name = "sends", updatable = false)
	private Integer sends = 0;

	@Column(name = "email_opens", updatable = false)
	private Integer emailOpens = 0;

	@Column(name = "gift_opens", updatable = false)
	private Integer giftOpens = 0;

	@Column(name = "users_targeted", updatable = false)
	private Integer usersTargeted = 0;

	public MessagingJobImpl()
	{}

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

	@Override
	public Date getRunningUpdateTime()
	{
		return runningUpdateTime;
	}

	@Override
	public void setJobState(JobState jobState)
	{
		this.jobState = jobState;
	}

	@Override
	public void setRunningUpdateTime(Date date)
	{
		this.runningUpdateTime = date;
	}

	@Override
	public Integer getUsersTargeted()
	{
		return usersTargeted;
	}

	@Override
	public void setUsersTargerted(Integer usersTargeted)
	{
		this.usersTargeted = usersTargeted;
	}

	@Override
	public void setSends(Integer sends)
	{
		this.sends = sends;
	}

	@Override
	public Integer getSends()
	{
		return sends;
	}
}
