package com.talool.domain;

import java.io.Serializable;
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
public class CustomerCriteria implements Serializable
{

	private static final long serialVersionUID = 2581391093684670689L;
	private Sex sex;
	private Date olderThan;
	private Date youngerThan;
	private UUID dealOfferId;
	
	private Query buildQuery(StringBuilder sb, Session session)
	{
		sb.append(" from CustomerImpl c, DealOfferPurchaseImpl dbp where dbp.customer.id=c.id ");
		
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
	
	public Query getQuery(Session session)
	{
		return buildQuery(new StringBuilder("select c"), session);
	}
	
	public Query getCountQuery(Session session)
	{
		return buildQuery(new StringBuilder("select count(c)"), session);
	}
	
	public void setAges(Date olderThan, Date youngerThan)
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

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Date getOlderThan() {
		return olderThan;
	}

	public void setOlderThan(Date olderThan) {
		this.olderThan = olderThan;
	}

	public Date getYoungerThan() {
		return youngerThan;
	}

	public void setYoungerThan(Date youngerThan) {
		this.youngerThan = youngerThan;
	}

	public UUID getDealOfferId() {
		return dealOfferId;
	}

	public void setDealOfferId(UUID dealOfferId) {
		this.dealOfferId = dealOfferId;
	}

}
