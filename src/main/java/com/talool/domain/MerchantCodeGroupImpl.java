package com.talool.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Merchant;
import com.talool.core.MerchantCode;
import com.talool.core.MerchantCodeGroup;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "merchant_code_group", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class MerchantCodeGroupImpl implements MerchantCodeGroup, Serializable
{
	private static final long serialVersionUID = 1845264234458656347L;

	private static final Logger LOG = LoggerFactory.getLogger(MerchantCodeGroupImpl.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_mcg_seq")
	@SequenceGenerator(name = "my_mcg_seq", sequenceName = "merchant_code_group_merchant_code_group_id_seq")
	@Column(name = "merchant_code_group_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = MerchantImpl.class)
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;

	@Column(name = "created_by_merchant_account_id", nullable = false)
	private Long createdBymerchantAccountId;

	@Type(type = "pg-uuid")
	@Column(name = "publisher_id", nullable = false)
	private UUID publisherId;

	@Column(name = "code_group_title", nullable = false, length = 64)
	private String codeGroupTitle;

	@Column(name = "code_group_notes", length = 128)
	private String codeGroupNotes;

	@Column(name = "total_codes", nullable = false)
	private Short totalCodes;

	@Column(name = "create_dt", insertable = false, updatable = false)
	private Date created;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "merchantCodeGroup", targetEntity = MerchantCodeImpl.class)
	private Set<MerchantCode> codes = new HashSet<MerchantCode>();

	public MerchantCodeGroupImpl()
	{
	}
	
	public MerchantCodeGroupImpl(final Merchant merchant)
	{
		this.merchant = merchant;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public Long getCreatedByMerchantAccountId()
	{
		return createdBymerchantAccountId;
	}

	@Override
	public String getCodeGroupTitle()
	{
		return codeGroupTitle;
	}

	@Override
	public String getCodeGroupNotes()
	{
		return codeGroupNotes;
	}

	@Override
	public Date getCreated()
	{
		return created;
	}

	@Override
	public Set<MerchantCode> getCodes()
	{
		return codes;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setCodeGroupTitle(String codeGroupTitle)
	{
		this.codeGroupTitle = codeGroupTitle;
	}

	public void setCodeGroupNotes(String codeGroupNodes)
	{
		this.codeGroupNotes = codeGroupNodes;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public void setCodes(Set<MerchantCode> codes)
	{
		this.codes = codes;
	}

	public Long getCreatedBymerchantAccountId()
	{
		return createdBymerchantAccountId;
	}

	public void setCreatedBymerchantAccountId(Long createdBymerchantAccountId)
	{
		this.createdBymerchantAccountId = createdBymerchantAccountId;
	}

	@Override
	public Short getTotalCodes()
	{
		return totalCodes;
	}

	public void setTotalCodes(Short totalCodes)
	{
		this.totalCodes = totalCodes;
	}

	@Override
	public UUID getPublisherId()
	{
		return publisherId;
	}

	public void setPublisherId(final UUID publisherId)
	{
		this.publisherId = publisherId;
	}

	@Override
	public Merchant getMerchant()
	{
		return merchant;
	}

}
