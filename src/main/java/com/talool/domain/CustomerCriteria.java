package com.talool.domain;

import java.util.Date;
import java.util.UUID;

import org.hibernate.Query;
import org.hibernate.Session;

import com.talool.core.Sex;

/**
 * 
 * @author dmccuen
 * 
 */
public class CustomerCriteria
{
	private Sex sex;
	private Date olderThan;
	private Date youngerThan;
	private UUID dealOfferId;
	
	public Query getQuery(Session session)
	{
		StringBuilder sb = new StringBuilder("from CustomerImpl c, DealOfferPurchaseImpl dbp where dbp.customer.id=c.id ");
		
		if (dealOfferId != null)
		{
			sb.append(" and dbp.dealOffer.id = :dealOfferId ");
		}
		if (olderThan != null)
		{
			sb.append(" and c.birthDate < :olderThan ");
		}
		if (youngerThan != null)
		{
			sb.append(" and c.birthDate > :youngerThan ");
		}
		if (sex != null)
		{
			sb.append(" and c.sex = :sex ");
		}
		
		Query query = session.createQuery(sb.toString());
		
		if (dealOfferId != null)
		{
			query.setParameter("dealOfferId", dealOfferId);
		}
		if (olderThan != null)
		{
			query.setParameter("olderThan", olderThan);
		}
		if (youngerThan != null)
		{
			query.setParameter("youngerThan", youngerThan);
		}
		if (sex != null)
		{
			query.setParameter("sex", sex);
		}
		
		
		return query;
	}

	public void filterSex(Sex s)
	{
		sex = s;
	}
	
	public void filterAge(Date olderThan, Date youngerThan)
	{
		if (olderThan != null)
		{
			this.olderThan = olderThan;
		}
		
		if (youngerThan != null)
		{
			this.youngerThan = youngerThan;
		}
	}
	
	public void filterPurchase(UUID dealOfferId)
	{
		this.dealOfferId = dealOfferId;
	}

}
