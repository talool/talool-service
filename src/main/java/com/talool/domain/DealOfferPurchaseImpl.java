package com.talool.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Target;

import com.talool.core.Customer;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferPurchase;
import com.talool.core.Location;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "deal_offer_purchase", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class DealOfferPurchaseImpl implements DealOfferPurchase
{
	private static final long serialVersionUID = -8559023014062619642L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_dop_seq")
	@SequenceGenerator(name = "my_dop_seq", sequenceName = "deal_offer_purchase_deal_offer_purchase_id_seq")
	@Column(name = "deal_offer_purchase_id", unique = true, nullable = false)
	private Long id;

	@Access(AccessType.FIELD)
	@OneToOne(targetEntity = DealOfferImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "deal_offer_id")
	private DealOffer dealOffer;

	@Access(AccessType.FIELD)
	@OneToOne(targetEntity = CustomerImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Embedded
	@Target(LocationImpl.class)
	private Location location;

	@Column(name = "create_dt", unique = false, nullable = true, insertable = false, updatable = false)
	private Date created;

	public DealOfferPurchaseImpl()
	{}

	public DealOfferPurchaseImpl(final Customer customer, final DealOffer dealOffer)
	{
		this.customer = customer;
		this.dealOffer = dealOffer;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public DealOffer getDealOffer()
	{
		return dealOffer;
	}

	@Override
	public Customer getCustomer()
	{
		return customer;
	}

	@Override
	public Location getLocation()
	{
		return location;
	}

	@Override
	public void setLocation(Location location)
	{
		this.location = location;
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

		if (!(obj instanceof CustomerImpl))
		{
			return false;
		}

		final DealOfferPurchaseImpl other = (DealOfferPurchaseImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getDealOffer(), other.getDealOffer())
				.append(getCustomer(), other.getCustomer()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getDealOffer()).append(getCustomer()).hashCode();
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}
