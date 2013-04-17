package com.talool.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Deal;
import com.talool.core.DealOffer;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.Tag;

/**
 * Deal Book implementation
 * 
 * TODO Verify hashcode/equals makes sense
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "deal", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@NamedQueries({ @NamedQuery(name = "allRelatedDeals", query = "select distinct d from DealImpl d where d.merchant.id=:merchantId OR d.dealOffer.createdByMerchantAccount.merchant.id=:merchantId OR d.dealOffer.merchant.id=:merchantId order by d.createdUpdated.created desc") })
public class DealImpl implements Deal
{
	private static final Logger LOG = LoggerFactory.getLogger(DealImpl.class);
	private static final long serialVersionUID = -452436060657087167L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_deal_seq")
	@SequenceGenerator(name = "my_deal_seq", sequenceName = "deal_deal_id_seq")
	@Column(name = "deal_id", unique = true, nullable = false)
	private Long id;

	@Access(AccessType.FIELD)
	@OneToOne(targetEntity = MerchantAccountImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "updated_by_merchant_account_id")
	private MerchantAccount updatedByMerchantAccount;

	@Access(AccessType.FIELD)
	@OneToOne(targetEntity = MerchantAccountImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_merchant_account_id")
	private MerchantAccount createdByMerchantAccount;

	@Access(AccessType.FIELD)
	@OneToOne(targetEntity = MerchantImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;

	@Access(AccessType.FIELD)
	@OneToOne(targetEntity = DealOfferImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "deal_offer_id")
	private DealOffer dealOffer;

	@Column(name = "title", unique = false, nullable = true, length = 256)
	private String title;

	@Column(name = "image_url", unique = false, nullable = true, length = 128)
	private String imageUrl;

	@Column(name = "summary", unique = false, nullable = true, length = 256)
	private String summary;

	@Column(name = "details", unique = false, nullable = true, length = 256)
	private String details;

	@Column(name = "code", unique = false, nullable = true, length = 128)
	private String code;

	@Column(name = "expires", unique = false, nullable = true)
	private Date expires;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = TagImpl.class)
	@JoinTable(name = "deal_tag", joinColumns = { @JoinColumn(name = "deal_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "tag_id", nullable = false, updatable = false) })
	private Set<Tag> tags = new HashSet<Tag>();

	@Column(name = "is_active", unique = false, nullable = true)
	private boolean isActive = true;

	@Embedded
	private CreatedUpdated createdUpdated;

	public DealImpl(MerchantAccount createdByMerchantAccount)
	{
		this.createdByMerchantAccount = createdByMerchantAccount;
	}

	public DealImpl()
	{}

	public DealImpl(final DealOffer dealOffer)
	{
		this.merchant = dealOffer.getMerchant();
		this.createdByMerchantAccount = dealOffer.getCreatedByMerchantAccount();
		this.updatedByMerchantAccount = dealOffer.getUpdatedByMerchantAccount();
		this.dealOffer = dealOffer;
	}

	@Override
	public MerchantAccount getCreatedByMerchantAccount()
	{
		return createdByMerchantAccount;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public Date getCreated()
	{
		return createdUpdated.getCreated();
	}

	@Override
	public Date getUpdated()
	{
		return createdUpdated.getUpdated();
	}

	@Override
	public Merchant getMerchant()
	{
		return merchant;
	}

	@Override
	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public String getTitle()
	{
		return title;
	}

	@Override
	public void setSummary(String summary)
	{
		this.summary = summary;
	}

	@Override
	public String getSummary()
	{
		return summary;
	}

	@Override
	public void setDetails(String details)
	{
		this.details = details;
	}

	@Override
	public String geDetails()
	{
		return details;
	}

	@Override
	public void setCode(String code)
	{
		this.code = code;

	}

	@Override
	public String getCode()
	{
		return code;
	}

	@Override
	public void setExpires(Date expires)
	{
		this.expires = expires;

	}

	@Override
	public Date getExpires()
	{
		return expires;
	}

	@Override
	public boolean isActive()
	{
		return isActive;
	}

	@Override
	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
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

		if (!(obj instanceof DealImpl))
		{
			return false;
		}

		final DealImpl other = (DealImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getDealOffer(), other.getDealOffer())
				.append(getTitle(), other.getTitle()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getTitle()).append(getDealOffer()).hashCode();
	}

	@Override
	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	@Override
	public String getImageUrl()
	{
		return imageUrl;
	}

	@Override
	public DealOffer getDealOffer()
	{
		return dealOffer;
	}

	@Override
	public void setDealOffer(DealOffer dealOffer)
	{
		this.dealOffer = dealOffer;
	}

	@Override
	public void setMerchant(Merchant merchant)
	{
		this.merchant = merchant;
	}

	@Override
	public Set<Tag> getTags()
	{
		return tags;
	}

	@Override
	public void addTag(Tag tag)
	{
		tags.add(tag);
	}

	@Override
	public void addTags(final List<Tag> _tags)
	{
		for (Tag tag : _tags)
		{
			tags.add(tag);
		}
	}

	@Override
	public void clearTags()
	{
		if (tags != null)
		{
			tags.clear();
		}
	}

	@Override
	public void setTags(Set<Tag> tags)
	{
		this.tags = tags;
	}

	@Override
	public String getCreatedByEmail()
	{
		if (createdByMerchantAccount != null)
		{
			return createdByMerchantAccount.getEmail();
		}
		return null;
	}

	@Override
	public String getCreatedByMerchantName()
	{
		if (createdByMerchantAccount != null)
		{
			return createdByMerchantAccount.getMerchant().getName();
		}
		return null;
	}

	@Override
	public MerchantAccount getUpdatedByMerchantAccount()
	{
		return updatedByMerchantAccount;
	}

	@Override
	public String getUpdatedByEmail()
	{
		if (updatedByMerchantAccount != null)
		{
			return updatedByMerchantAccount.getEmail();
		}
		return null;
	}

	@Override
	public String getUpdatedByMerchantName()
	{
		if (updatedByMerchantAccount != null)
		{
			return updatedByMerchantAccount.getMerchant().getName();
		}
		return null;
	}

	@Override
	public void setUpdatedByMerchantAccount(MerchantAccount merchantAccount)
	{
		this.updatedByMerchantAccount = merchantAccount;
	}

}
