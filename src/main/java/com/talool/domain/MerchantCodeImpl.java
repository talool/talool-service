package com.talool.domain;

import java.util.UUID;

import javax.persistence.CascadeType;
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

import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.MerchantCode;
import com.talool.core.MerchantCodeGroup;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "merchant_code", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class MerchantCodeImpl implements MerchantCode
{
	private static final Logger LOG = LoggerFactory.getLogger(MerchantCodeImpl.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_mc_seq")
	@SequenceGenerator(name = "my_mc_seq", sequenceName = "merchant_code_merchant_code_id_seq")
	@Column(name = "merchant_code_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = MerchantCodeGroupImpl.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "merchant_code_group_id", nullable = false)
	private MerchantCodeGroup merchantCodeGroup;

	@Column(name = "code", nullable = false, length = 10)
	private String code;

	@Column(name = "deal_offer_purchase_id", nullable = true)
	@Type(type = "pg-uuid")
	private UUID dealOfferPurchaseId;

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public MerchantCodeGroup getMerchantCodeGroupId()
	{
		return merchantCodeGroup;
	}

	@Override
	public String getCode()
	{
		return code;
	}

	@Override
	public UUID getDealOfferPurchaseId()
	{
		return dealOfferPurchaseId;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setMerchantCodeGroup(MerchantCodeGroup merchantCodeGroup)
	{
		this.merchantCodeGroup = merchantCodeGroup;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public void setDealOfferPurchaseId(UUID dealOfferPurchaseId)
	{
		this.dealOfferPurchaseId = dealOfferPurchaseId;
	}

}
