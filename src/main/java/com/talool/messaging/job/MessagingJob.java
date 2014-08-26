package com.talool.messaging.job;

import java.io.Serializable;
import java.util.Date;

import com.talool.core.Customer;
import com.talool.core.Identifiable;
import com.talool.core.MerchantAccount;
import com.talool.core.PropertyEntity;

/**
 * Messaging Job
 * 
 * @author clintz
 * 
 */
public interface MessagingJob extends Identifiable, Serializable, PropertyEntity
{
	/**
	 * Gets the merchant account that created the job
	 * 
	 * @return
	 */
	public MerchantAccount getCreatedByMerchantAccount();

	/**
	 * Gets the customer used for job sending
	 * 
	 * @return
	 */
	public Customer getFromCustomer();

	/**
	 * Gets the status of the job
	 * 
	 * @return
	 */
	public JobState getJobState();

	/**
	 * Sets the jobState
	 * 
	 * @param jobState
	 */
	public void setJobState(final JobState jobState);

	/**
	 * Gets the created date of the job
	 * 
	 * @return
	 */
	public Date getCreated();

	/**
	 * Gets the scheduled start date. This will never be null.
	 * 
	 * @return
	 */
	public Date getScheduledStartDate();

	/**
	 * Gets the job notes
	 * 
	 * @return notes
	 */
	public String getJobNotes();

	/**
	 * Sets the job notes
	 */
	public void setJobNotes(final String notes);

	/**
	 * Gets the last running update time. If the job hasn't started yet, this will be null.
	 * 
	 * @return
	 */
	public Date getRunningUpdateTime();

	/**
	 * Sets the last running update time. If the job hasn't started yet, this will be null.
	 * 
	 * @return
	 */
	public void setRunningUpdateTime(final Date date);

	public Integer getUsersTargeted();

	public void setUsersTargerted(final Integer usersTargeted);

	public void setSends(final Integer sends);

	public Integer getSends();

}
