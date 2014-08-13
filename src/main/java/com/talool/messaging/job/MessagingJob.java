package com.talool.messaging.job;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.talool.core.Customer;
import com.talool.core.Identifiable;
import com.talool.core.MerchantAccount;

/**
 * Messaging Job
 * 
 * @author clintz
 * 
 */
public interface MessagingJob extends Identifiable, Serializable
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
	 * Gets the list of MessagingReceipientStatus. If the jobState is Finished,
	 * this list may have been cleaned and could be null.
	 * 
	 * @return
	 */
	public List<RecipientStatus> getMessagingReceipientStatuses();

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

}
