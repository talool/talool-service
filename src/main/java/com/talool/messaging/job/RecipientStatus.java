package com.talool.messaging.job;

import java.io.Serializable;

import com.talool.core.Customer;
import com.talool.core.Identifiable;

/**
 * Messaging Job Details detailing the customer targeted . This exists to give
 * some durability to the job so duplicate messages are not sent. Consider this
 * a transient object that can be deleted post job success (Finished)
 * 
 * @author clintz
 * 
 */
public interface RecipientStatus extends Identifiable, Serializable
{
	public Long getMessagingJobId();

	public Customer getCustomer();

	public DeliveryStatus getDeliveryStatus();
}
