package com.talool.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Category;
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
@Cache(region = "Merchant", usage = CacheConcurrencyStrategy.READ_WRITE)
public class MerchantImpl implements Merchant
{
	private static final Logger LOG = LoggerFactory.getLogger(MerchantImpl.class);
	private static final long serialVersionUID = -4505114813841857043L;

	@Transient
	private MerchantLocation currentLocation;

	@Id
	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "merchant_id", unique = true, nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = MerchantImpl.class)
	@JoinColumn(name = "merchant_parent_id")
	private Merchant parent;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = CategoryImpl.class)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "merchant_name", unique = false, nullable = false, length = 64)
	private String name;

	@Column(name = "is_discoverable")
	private boolean isDiscoverable = true;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = TagImpl.class)
	@JoinTable(name = "merchant_tag", joinColumns = { @JoinColumn(name = "merchant_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "tag_id", nullable = false, updatable = false) })
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Fetch(FetchMode.SUBSELECT)
	@BatchSize(size = 5)
	private Set<Tag> tags = new HashSet<Tag>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = MerchantAccountImpl.class)
	@JoinColumn(name = "merchant_id")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<MerchantAccount> merchantAccounts = new HashSet<MerchantAccount>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "merchant", targetEntity = MerchantLocationImpl.class)
	@OrderBy("createdUpdated.created")
	@Fetch(FetchMode.SUBSELECT)
	@BatchSize(size = 5)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<MerchantLocation> locations = new LinkedHashSet<MerchantLocation>();

	@Embedded
	private CreatedUpdated createdUpdated;

	@Embedded
	private Properties props = new Properties();

	// @Version
	// private Long version;

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

		return new EqualsBuilder().append(getName(), other.getName()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getName()).hashCode();
	}

	@Override
	public MerchantLocation getPrimaryLocation()
	{
		if (CollectionUtils.isNotEmpty(locations))
		{
			return locations.iterator().next();
		}

		return null;
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

	@Override
	public Set<MerchantLocation> getLocations()
	{
		return this.locations;
	}

	@Override
	public void addLocation(MerchantLocation mloc)
	{
		mloc.setMerchant(this);
		locations.add(mloc);
	}

	public void setLocations(Set<MerchantLocation> locations)
	{
		this.locations = locations;
	}

	public MerchantLocation getCurrentLocation()
	{
		if (currentLocation == null)
		{
			return getPrimaryLocation();
		}

		return currentLocation;
	}

	public void setCurrentLocation(final MerchantLocation loc)
	{
		currentLocation = loc;
	}

	@Override
	public Category getCategory()
	{
		return category;
	}

	@Override
	public void setCategory(final Category category)
	{
		this.category = category;
	}

	@Override
	public boolean isDiscoverable()
	{
		return isDiscoverable;
	}

	@Override
	public void setIsDiscoverable(boolean isDiscoverable)
	{
		this.isDiscoverable = isDiscoverable;
	}

	@Override
	public Properties getProperties()
	{
		return props;
	}

	// public Long getVersion()
	// {
	// return version;
	// }

}
