package com.talool.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantLocation;
import com.talool.core.Tag;
import com.talool.core.service.ServiceException;
import com.talool.service.ServiceFactory;

/**
 * 
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "merchant", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class MerchantImpl implements Merchant
{
	private static final Logger LOG = LoggerFactory.getLogger(MerchantImpl.class);
	private static final long serialVersionUID = -4505114813841857043L;

	@Id
	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "merchant_id", unique = true, nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = MerchantImpl.class)
	@JoinColumn(name = "merchant_parent_id", columnDefinition = "character (36)")
	private Merchant parent;

	@Column(name = "merchant_name", unique = false, nullable = false, length = 64)
	private String name;

	@OneToOne(targetEntity = MerchantLocationImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "primary_location_id")
	private MerchantLocation primaryLocation;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = TagImpl.class)
	@JoinTable(name = "merchant_tag", joinColumns = { @JoinColumn(name = "merchant_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "tag_id", nullable = false, updatable = false) })
	private Set<Tag> tags = new HashSet<Tag>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = MerchantAccountImpl.class)
	@JoinColumn(name = "merchant_id")
	private Set<MerchantAccount> merchantAccounts = new HashSet<MerchantAccount>();

	@Embedded
	private CreatedUpdated createdUpdated;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	public Merchant getParent()
	{
		return parent;
	}

	public void setParent(Merchant parent)
	{
		this.parent = parent;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
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

		if (!(obj instanceof MerchantImpl))
		{
			return false;
		}

		final MerchantImpl other = (MerchantImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getName(), other.getName())
				.append(getPrimaryLocation(), other.getPrimaryLocation()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getName()).append(getPrimaryLocation()).hashCode();
	}

	@Override
	public MerchantLocation getPrimaryLocation()
	{
		return primaryLocation;
	}

	@Override
	public void setPrimaryLocation(MerchantLocation merchantLocation)
	{
		this.primaryLocation = merchantLocation;

	}

	@Override
	public Set<Tag> getTags()
	{
		return tags;
	}

	@Override
	public void addTag(final Tag tag)
	{
		tags.add(tag);
	}

	@Override
	public Set<MerchantAccount> getMerchantAccounts()
	{
		return merchantAccounts;
	}

	@Override
	public Long getNumberOfMerchantAccounts()
	{
		Long size = null;
		try
		{
			size = ServiceFactory.get().getTaloolService().sizeOfCollection(merchantAccounts);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}

		return size;
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

}
