package com.talool.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.FriendRequest;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "friend_equest", catalog = "public")
public class FriendRequestImpl implements FriendRequest
{
	private static final long serialVersionUID = -804410556508727956L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_friendr_seq")
	@SequenceGenerator(name = "my_friendr_seq", sequenceName = "friend_request_friend_request_id_seq")
	@Column(name = "friend_request_id", unique = true, nullable = false)
	private Long id;

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Access(AccessType.FIELD)
	@ManyToOne(targetEntity = DealImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "deal_id")
	private Deal deal;

	@Column(name = "friend_facebook_id", unique = false, nullable = true, length = 32)
	private String friendFacebookId;

	@Column(name = "friend_email", unique = false, nullable = true, length = 128)
	private String friendEmail;

	@Column(name = "created_dt", unique = false, insertable = false, updatable = false)
	private Date created;

	public FriendRequestImpl(final Customer customer, final String friendFacebookId,
			final String friendEmail, final Deal deal)
	{
		this.customer = customer;
		this.friendFacebookId = friendFacebookId;
		this.friendEmail = friendEmail;
		this.deal = deal;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public Customer getCustomer()
	{
		return customer;
	}

	@Override
	public String getFriendFacebookId()
	{
		return friendFacebookId;
	}

	@Override
	public String getFriendEmail()
	{
		return friendEmail;
	}

	@Override
	public Deal getDeal()
	{
		return deal;
	}

	@Override
	public Date getCreated()
	{
		return created;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj == null)
		{
			return false;
		}

		if (!(obj instanceof FriendRequestImpl))
		{
			return false;
		}

		final FriendRequestImpl other = (FriendRequestImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getCustomer(), other.getFriendFacebookId())
				.append(getFriendEmail(), other.getFriendEmail()).append(getDeal(), other.getDeal())
				.isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getCustomer()).append(getFriendFacebookId())
				.append(getFriendEmail()).append(getDeal()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
