package com.talool.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernatespatial.GeometryUserType;

import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantLocation;
import com.talool.core.MerchantMedia;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "merchant_location", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@TypeDef(name = "geomType", typeClass = GeometryUserType.class)
public class MerchantLocationImpl implements MerchantLocation
{
	private static final long serialVersionUID = 3716227130006204917L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "my_loc_seq")
	@SequenceGenerator(name = "my_loc_seq", sequenceName = "merchant_location_merchant_location_id_seq")
	@Column(name = "merchant_location_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = MerchantImpl.class)
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;

	@Column(name = "merchant_location_name", unique = false, nullable = true, length = 64)
	private String locationName;

	@Column(name = "email", unique = true, nullable = true, length = 64)
	private String email;

	@Column(name = "website_url", unique = false, nullable = true, length = 128)
	private String websiteUrl;

	@Column(name = "phone", unique = true, nullable = true, length = 48)
	private String phone;

	@Column(name = "address1", unique = false, nullable = true, length = 64)
	private String address1;

	@Column(name = "address2", unique = false, nullable = true, length = 64)
	private String address2;

	@Column(name = "city", unique = false, nullable = false, length = 64)
	private String city;

	@Column(name = "state_province_county", unique = false, nullable = true, length = 64)
	private String stateProvinceCounty;

	@Column(name = "zip", unique = false, nullable = true, length = 64)
	private String zip;

	@Column(name = "country", unique = false, length = 3)
	private String country;

	@Column(name = "valid_email")
	private Boolean isValidEmail;

	@Type(type = "geomType")
	@Column(name = "geom", nullable = true)
	private com.vividsolutions.jts.geom.Geometry geometry;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = MerchantMediaImpl.class, cascade = CascadeType.ALL)
	@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
	@JoinColumn(name = "logo_url_id")
	private MerchantMedia logo;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = MerchantMediaImpl.class, cascade = CascadeType.ALL)
	@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
	@JoinColumn(name = "merchant_image_id")
	private MerchantMedia merchantImage;

	@Transient
	private Double distanceInMeters;

	@Embedded
	private CreatedUpdated createdUpdated;

	@OneToOne(targetEntity = MerchantAccountImpl.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by_merchant_account_id", nullable = false)
	private MerchantAccount createdByMerchantAccount;

	@OneToOne(targetEntity = MerchantImpl.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by_merchant_id", nullable = false)
	private Merchant createdByMerchant;

	@Column(name = "created_by_merchant_id", insertable = false, updatable = false)
	private UUID createdByMerchantId;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	@Override
	public String getLocationName()
	{
		return locationName;
	}

	@Override
	public void setLocationName(String name)
	{
		this.locationName = name;
	}

	@Override
	public String getWebsiteUrl()
	{
		return websiteUrl;
	}

	@Override
	public void setWebsiteUrl(String websiteUrl)
	{
		this.websiteUrl = websiteUrl;
	}

	@Override
	public MerchantMedia getLogo()
	{
		return logo;
	}

	@Override
	public void setLogo(MerchantMedia logo)
	{
		this.logo = logo;
	}

	@Override
	public String getPhone()
	{
		return phone;
	}

	@Override
	public void setPhone(String phone)
	{
		this.phone = phone;
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
	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	@Override
	public String getAddress2()
	{
		return address2;
	}

	@Override
	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	@Override
	public String getCity()
	{
		return city;
	}

	@Override
	public void setCity(String city)
	{
		this.city = city;
	}

	@Override
	public String getStateProvinceCounty()
	{
		return stateProvinceCounty;
	}

	@Override
	public void setStateProvinceCounty(String stateProvinceCounty)
	{
		this.stateProvinceCounty = stateProvinceCounty;
	}

	@Override
	public String getZip()
	{
		return zip;
	}

	@Override
	public void setZip(String zip)
	{
		this.zip = zip;
	}

	@Override
	public String getCountry()
	{
		return country;
	}

	@Override
	public void setCountry(String country)
	{
		this.country = country;
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

		if (!(obj instanceof MerchantLocationImpl))
		{
			return false;
		}

		final MerchantLocationImpl other = (MerchantLocationImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getMerchant(), other.getMerchant())
				.append(getLocationName(), other.getLocationName())
				.append(getAddress1(), other.getAddress1()).append(getAddress2(), other.getAddress2())
				.append(getCity(), other.getCity()).append(getStateProvinceCounty(), other.getStateProvinceCounty())
				.append(getCountry(), other.getCountry()).append(getZip(), other.getZip()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getMerchant()).append(getLocationName()).
				append(getAddress1()).append(getAddress2()).
				append(getCity()).append(getStateProvinceCounty()).
				append(getCountry()).append(getZip()).
				hashCode();
	}

	@Override
	public String getEmail()
	{
		return email;
	}

	@Override
	public void setEmail(final String email)
	{
		this.email = email;
	}

	@Override
	public Geometry getGeometry()
	{
		return geometry;
	}

	@Override
	public void setGeometry(Geometry geometry)
	{
		this.geometry = geometry;
	}

	public void setDistanceInMeters(final Double meters)
	{
		distanceInMeters = meters;
	}

	@Override
	public Double getDistanceInMeters()
	{
		return distanceInMeters;
	}

	@Override
	public Merchant getMerchant()
	{
		return merchant;
	}

	@Override
	public void setMerchant(Merchant merchant)
	{
		this.merchant = merchant;
	}

	@Override
	public MerchantMedia getMerchantImage()
	{
		return merchantImage;
	}

	@Override
	public void setMerchantImage(MerchantMedia merchantImage)
	{
		this.merchantImage = merchantImage;
	}

	@Override
	public String getAddress1()
	{
		return address1;
	}

	@Override
	public String getNiceCityState()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(city).append(", ").append(stateProvinceCounty);
		return sb.toString();
	}

	public MerchantAccount getCreatedByMerchantAccount()
	{
		return createdByMerchantAccount;
	}

	public void setCreatedByMerchantAccount(MerchantAccount createdByMerchantAccount)
	{
		this.createdByMerchantAccount = createdByMerchantAccount;
		this.createdByMerchant = createdByMerchantAccount.getMerchant();
	}

	public Merchant getCreatedByMerchant()
	{
		return createdByMerchant;
	}

	@Override
	public UUID getCreatedByMerchantId()
	{
		return createdByMerchantId;
	}

	@Override
	public boolean isEmailValid()
	{
		return (isValidEmail == null || isValidEmail == true) ? true : false;
	}

	@Override
	public void setIsEmailValid(boolean isValid)
	{
		this.isValidEmail = isValid;
	}
}
