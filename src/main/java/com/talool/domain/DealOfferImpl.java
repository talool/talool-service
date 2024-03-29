package com.talool.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.talool.core.DealOffer;
import com.talool.core.DealType;
import com.talool.core.Merchant;
import com.talool.core.MerchantAccount;
import com.talool.core.MerchantMedia;
import com.talool.persistence.GenericEnumUserType;
import com.talool.persistence.HstoreUserType;

/**
 * DealOffer Impl
 * 
 * TODO Create Money object and replace price
 * 
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "deal_offer", catalog = "public")
@TypeDefs({
    @TypeDef(name = "dealType", typeClass = GenericEnumUserType.class, parameters = {@Parameter(name = "enumClass",
        value = "com.talool.core.DealType")}), @TypeDef(name = "hstore", typeClass = HstoreUserType.class)})
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class DealOfferImpl implements DealOffer {
  private static final long serialVersionUID = 5159454091663842874L;

  @Id
  @GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
  @GeneratedValue(generator = "uuid_gen")
  @Type(type = "pg-uuid")
  @Column(name = "deal_offer_id", unique = true, nullable = false)
  private UUID id;

  @OneToOne(targetEntity = MerchantAccountImpl.class, fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "created_by_merchant_account_id")
  private MerchantAccount createdByMerchantAccount;

  @OneToOne(targetEntity = MerchantAccountImpl.class, fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "updated_by_merchant_account_id")
  private MerchantAccount updatedByMerchantAccount;

  @OneToOne(targetEntity = MerchantImpl.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;

  @Column(name = "location_name", unique = false, nullable = true, length = 64)
  private String locationName;

  @ManyToOne(fetch = FetchType.EAGER, targetEntity = MerchantMediaImpl.class, cascade = CascadeType.ALL)
  @Fetch(value = FetchMode.JOIN)
  @JoinColumn(name = "deal_offer_logo_id")
  private MerchantMedia dealOfferLogo;

  @ManyToOne(fetch = FetchType.EAGER, targetEntity = MerchantMediaImpl.class, cascade = CascadeType.ALL)
  @Fetch(value = FetchMode.JOIN)
  @JoinColumn(name = "deal_offer_background_id")
  private MerchantMedia dealOfferBackground;

  @ManyToOne(fetch = FetchType.EAGER, targetEntity = MerchantMediaImpl.class, cascade = CascadeType.ALL)
  @Fetch(value = FetchMode.JOIN)
  @JoinColumn(name = "deal_offer_icon_id")
  private MerchantMedia dealOfferIcon;

  @Column(name = "summary", unique = false, nullable = true, length = 256)
  private String summary;

  @Column(name = "code", unique = false, nullable = true, length = 128)
  private String code;

  @Column(name = "is_active", unique = false, nullable = true)
  private boolean isActive = true;

  @Column(name = "price", unique = false, nullable = true)
  private Float price;

  @Column(name = "title", unique = false, nullable = true, length = 256)
  private String title;

  @Type(type = "dealType")
  @Column(name = "deal_type", nullable = false, columnDefinition = "deal_type")
  private DealType dealType;

  @Type(type = "geomType")
  @Column(name = "geom", nullable = true)
  private com.vividsolutions.jts.geom.Geometry geometry;

  @Embedded
  private CreatedUpdated createdUpdated;

  @Embedded
  private Properties props;

  @Column(name = "scheduled_start_dt", nullable = true)
  private Date scheduledStartDate;

  @Column(name = "scheduled_end_dt", nullable = true)
  private Date scheduledEndDate;

  public DealOfferImpl() {}

  public DealOfferImpl(final Merchant merchant, final MerchantAccount createdByMerchantAccount) {
    this.createdByMerchantAccount = createdByMerchantAccount;
    this.merchant = merchant;
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public Date getCreated() {
    return createdUpdated.getCreated();
  }

  @Override
  public Date getUpdated() {
    return createdUpdated.getUpdated();
  }

  @Override
  public void setSummary(String summary) {
    this.summary = summary;
  }

  @Override
  public String getSummary() {
    return summary;
  }

  @Override
  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public boolean isActive() {
    return isActive;
  }

  @Override
  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  public DealOffer copy() {
    final DealOfferImpl newDealOffer = new DealOfferImpl(this.merchant, this.createdByMerchantAccount);
    newDealOffer.setActive(this.isActive);
    newDealOffer.setCode(this.code);
    newDealOffer.setDealOfferBackground(this.dealOfferBackground);
    newDealOffer.setDealOfferIcon(this.dealOfferIcon);
    newDealOffer.setDealOfferLogo(this.dealOfferLogo);
    newDealOffer.setDealType(this.dealType);

    newDealOffer.setScheduledStartDate(this.scheduledStartDate);
    newDealOffer.setScheduledEndDate(this.scheduledEndDate);

    newDealOffer.setGeometry(this.geometry);
    newDealOffer.setLocationName(this.locationName);
    newDealOffer.setPrice(this.price);
    newDealOffer.setSummary(this.summary);
    newDealOffer.setTitle(this.title);
    newDealOffer.setUpdatedByMerchantAccount(this.updatedByMerchantAccount);

    Properties properties = getProperties();
    Properties propsNew = new Properties();
    for (Entry<String, String> entry : properties.getAllProperties().entrySet()) {
      propsNew.createOrReplace(entry.getKey(), entry.getValue());

    }

    newDealOffer.props = propsNew;

    return newDealOffer;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (!(obj instanceof DealImpl)) {
      return false;
    }

    final DealOfferImpl other = (DealOfferImpl) obj;

    if (getId() != other.getId()) {
      return false;
    }

    return new EqualsBuilder().append(getSummary(), other.getSummary()).append(getCreatedByMerchantAccount(), other.getCreatedByMerchantAccount())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(getSummary()).append(getCreatedByMerchantAccount()).hashCode();
  }

  @Override
  public void setDealOfferLogo(final MerchantMedia image) {
    this.dealOfferLogo = image;
  }

  @Override
  public MerchantMedia getDealOfferLogo() {
    return dealOfferLogo;
  }

  @Override
  public MerchantAccount getCreatedByMerchantAccount() {
    return createdByMerchantAccount;
  }

  @Override
  public void setPrice(Float price) {
    this.price = price;

  }

  @Override
  public Float getPrice() {
    return price;
  }

  @Override
  public void setDealType(DealType dealType) {
    this.dealType = dealType;

  }

  @Override
  public DealType getType() {
    return dealType;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

  @Override
  public Merchant getMerchant() {
    return merchant;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getCreatedByEmail() {
    if (createdByMerchantAccount != null) {
      return createdByMerchantAccount.getEmail();
    }
    return null;

  }

  @Override
  public String getCreatedByMerchantName() {
    if (createdByMerchantAccount != null) {
      return createdByMerchantAccount.getMerchant().getName();
    }
    return null;
  }

  @Override
  public MerchantAccount getUpdatedByMerchantAccount() {
    return updatedByMerchantAccount;
  }

  @Override
  public String getUpdatedByEmail() {
    if (updatedByMerchantAccount != null) {
      return updatedByMerchantAccount.getEmail();
    }
    return null;
  }

  @Override
  public String getUpdatedByMerchantName() {
    if (updatedByMerchantAccount != null) {
      return updatedByMerchantAccount.getMerchant().getName();
    }
    return null;
  }

  @Override
  public void setUpdatedByMerchantAccount(MerchantAccount merchantAccount) {
    this.updatedByMerchantAccount = merchantAccount;
  }

  @Override
  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  @Override
  public String getLocationName() {
    return locationName;
  }

  @Override
  public void setLocationName(String name) {
    this.locationName = name;
  }

  public com.vividsolutions.jts.geom.Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(com.vividsolutions.jts.geom.Geometry geometry) {
    this.geometry = geometry;
  }

  @Override
  public void setDealOfferBackground(final MerchantMedia dealOfferBackgroundImage) {
    this.dealOfferBackground = dealOfferBackgroundImage;
  }

  @Override
  public MerchantMedia getDealOfferBackground() {
    return dealOfferBackground;
  }

  @Override
  public void setDealOfferIcon(final MerchantMedia dealOfferIcon) {
    this.dealOfferIcon = dealOfferIcon;
  }

  @Override
  public MerchantMedia getDealOfferIcon() {
    return dealOfferIcon;
  }

  @Override
  public Properties getProperties() {
    if (props == null) {
      props = new Properties();
    }
    return props;
  }

  @Override
  public Date getScheduledStartDate() {
    return scheduledStartDate;
  }

  @Override
  public Date getScheduledEndDate() {
    return scheduledEndDate;
  }

  @Override
  public void setScheduledStartDate(Date date) {
    this.scheduledStartDate = date;

  }

  @Override
  public void setScheduledEndDate(Date date) {
    this.scheduledEndDate = date;
  }

  @Override
  public boolean isCurrentlyScheduled() {
    if (scheduledStartDate != null && scheduledEndDate != null) {
      final long now = Calendar.getInstance().getTime().getTime();
      return (now >= scheduledStartDate.getTime()) && (now < scheduledStartDate.getTime());
    }

    return false;
  }

  @Override
  public boolean isFree() {
    return dealType == DealType.FREE_BOOK || price == 0;
  }

}
