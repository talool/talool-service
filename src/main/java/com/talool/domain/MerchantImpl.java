package com.talool.domain;

import java.util.ArrayList;
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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
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
@NamedQuery(name = "loadPrimaryLocation", query = "FROM MerchantLocationImpl WHERE merchant_id = ? AND isPrimary=true")
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
	@JoinColumn(name = "merchant_parent_id")
	private Merchant parent;

	@Column(name = "merchant_name", unique = false, nullable = false, length = 64)
	private String name;

	@OneToMany(targetEntity = MerchantLocationImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "merchant")
	@Where(clause = "is_primary = 'true'")
	private List<MerchantLocation> primaryLocations = new ArrayList<MerchantLocation>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = TagImpl.class)
	@JoinTable(name = "merchant_tag", joinColumns = { @JoinColumn(name = "merchant_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "tag_id", nullable = false, updatable = false) })
	private Set<Tag> tags = new HashSet<Tag>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = MerchantAccountImpl.class)
	@JoinColumn(name = "merchant_id")
	private Set<MerchantAccount> merchantAccounts = new HashSet<MerchantAccount>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "merchant", targetEntity = MerchantLocationImpl.class)
	private List<MerchantLocation> locations = new ArrayList<MerchantLocation>();

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
		// Yes for now we have a single collection representing a single primary
		// location - Properties will make this go away

		MerchantLocation mloc = null;

		if (CollectionUtils.isNotEmpty(primaryLocations))
		{
			mloc = primaryLocations.get(0);
		}

		return mloc;
	}

	@Override
	public void setPrimaryLocation(final MerchantLocation merchantLocation)
	{
		// Yes for now we have a single collection representing a single primary
		// location - Properties will make this go away
		merchantLocation.setIsPrimary(true);
		merchantLocation.setMerchant(this);
		primaryLocations.add(merchantLocation);
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

	@Override
	public List<MerchantLocation> getLocations()
	{
		return this.locations;
	}

}
