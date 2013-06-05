package com.talool.domain.gift;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.talool.core.Customer;
import com.talool.core.gift.TaloolGift;
import com.talool.domain.CustomerImpl;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "gift")
@DiscriminatorValue("T")
public class TaloolGiftImpl extends GiftImpl implements TaloolGift
{
	private static final long serialVersionUID = -6197930806091256725L;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = CustomerImpl.class)
	@JoinColumn(name = "to_customer_id")
	private Customer toCustomer;

	@Override
	public Customer getToCustomer()
	{
		return toCustomer;
	}

	@Override
	public void setToCustomer(Customer toCustomer)
	{
		this.toCustomer = toCustomer;
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

		if (!(obj instanceof TaloolGiftImpl))
		{
			return false;
		}

		final TaloolGiftImpl other = (TaloolGiftImpl) obj;

		return super.equals(obj) && new EqualsBuilder().append(getToCustomer(), other.getToCustomer()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getToCustomer()).hashCode();
	}

}
