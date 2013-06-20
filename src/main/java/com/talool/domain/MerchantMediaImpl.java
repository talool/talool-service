package com.talool.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.talool.core.MediaType;
import com.talool.core.MerchantMedia;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "merchant_media", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Cache(region = "MerchantMedia", usage = CacheConcurrencyStrategy.READ_ONLY)
public class MerchantMediaImpl implements MerchantMedia
{
	private static final long serialVersionUID = 6837136446205380362L;

	@Id
	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "merchant_media_id", unique = true, nullable = false, updatable = false)
	private UUID id;

	@Type(type = "pg-uuid")
	@Column(name = "merchant_id", unique = true, nullable = false, updatable = false)
	private UUID merchantId;

	@Column(name = "media_url", unique = false, nullable = false, length = 128, updatable = false)
	private String mediaUrl;

	@Type(type = "mediaType")
	@Column(name = "media_type", nullable = false, columnDefinition = "media_type", updatable = false)
	private MediaType mediaType;

	@Column(name = "create_dt", unique = false, insertable = false, updatable = false)
	private Date created;

	@Override
	public UUID getId()
	{
		return id;
	}

	@Override
	public UUID getMerchantId()
	{
		return merchantId;
	}

	@Override
	public void setMerchantId(UUID merchantId)
	{
		this.merchantId = merchantId;
	}

	@Override
	public String getMediaUrl()
	{
		return mediaUrl;
	}

	@Override
	public void setMediaUrl(String mediaUrl)
	{
		this.mediaUrl = mediaUrl;
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

		if (!(obj instanceof MerchantMediaImpl))
		{
			return false;
		}

		final MerchantMediaImpl other = (MerchantMediaImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getMediaUrl(), other.getMediaUrl())
				.append(getMerchantId(), other.getMerchantId()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getMediaUrl()).append(getMerchantId())
				.hashCode();
	}

	@Override
	public MediaType getMediaType()
	{
		return mediaType;
	}

	@Override
	public void setMediaType(MediaType mediaType)
	{
		this.mediaType = mediaType;
	}

	@Override
	public String getMediaName()
	{
		if (mediaUrl == null)
		{
			return null;
		}
		else
		{
			int slashIndex = mediaUrl.lastIndexOf('/');
			return mediaUrl.substring(slashIndex + 1);
		}
	}

}
