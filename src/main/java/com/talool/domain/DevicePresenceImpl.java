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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.talool.core.DevicePresence;
import com.vividsolutions.jts.geom.Geometry;

/**
 * A customer presence hibernate impl
 * 
 * @author clintz
 * 
 */

@Entity
@Table(name = "device_presence", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class DevicePresenceImpl implements DevicePresence {
  @Id
  @GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
  @GeneratedValue(generator = "uuid_gen")
  @Type(type = "pg-uuid")
  @Column(name = "device_presence_id", unique = true, nullable = false)
  private UUID id;

  @Type(type = "pg-uuid")
  @Column(name = "customer_id", nullable = false, insertable = true, updatable = false)
  private UUID customerId;

  @Type(type = "geomType")
  @Column(name = "location", nullable = true)
  private com.vividsolutions.jts.geom.Geometry location;

  @Column(name = "device_id", nullable = false)
  private String deviceId;

  @Column(name = "device_type", nullable = true)
  private String deviceType;

  @Column(name = "device_os_version", nullable = true)
  private String deviceOsVersion;

  @Column(name = "talool_version", nullable = true)
  private String taloolVersion;

  @Column(name = "user_agent", nullable = false)
  private String userAgent;

  @Column(name = "ip", nullable = true)
  private String ip;

  @Column(name = "city", nullable = true)
  private String city;

  @Column(name = "state", nullable = true)
  private String stateCode;

  @Column(name = "zip", nullable = true)
  private String zip;

  @Column(name = "device_token", nullable = true)
  private String deviceToken;

  @Column(name = "country", nullable = true)
  private String country;

  @Column(name = "update_dt", unique = false, insertable = false, updatable = false)
  private Date updated;

  @Override
  public UUID getCustomerId() {
    return customerId;
  }

  @Override
  public void setCustomerId(final UUID customerId) {
    this.customerId = customerId;
  }

  @Override
  public String getIp() {
    return ip;
  }

  @Override
  public void setIp(final String ip) {
    this.ip = ip;
  }

  @Override
  public String getCity() {
    return city;
  }

  @Override
  public void setCity(final String city) {
    this.city = city;

  }

  @Override
  public String getStateCode() {
    return stateCode;
  }

  @Override
  public void setStateCode(final String stateCode) {
    this.stateCode = stateCode;

  }

  @Override
  public String getZip() {
    return zip;
  }

  @Override
  public void setZip(final String zip) {
    this.zip = zip;
  }

  @Override
  public String getCountry() {
    return country;
  }

  @Override
  public void setCountry(final String country) {
    this.country = country;
  }

  @Override
  public Geometry getLocation() {
    return location;
  }

  @Override
  public void setLocation(final Geometry location) {
    this.location = location;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (!(obj instanceof DevicePresence)) {
      return false;
    }

    final DevicePresenceImpl other = (DevicePresenceImpl) obj;

    return new EqualsBuilder().append(getCustomerId(), other.getCustomerId()).append(getLocation(), other.getLocation())
        .append(getIp(), other.getIp()).append(getCity(), other.getCity()).append(getStateCode(), other.getStateCode()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(getCustomerId()).append(getLocation()).append(getIp()).append(getCity()).append(getStateCode())
        .hashCode();
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public String getDeviceId() {
    return deviceId;
  }

  @Override
  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  @Override
  public String getDeviceType() {
    return deviceType;
  }

  @Override
  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  @Override
  public String getTaloolVersion() {
    return taloolVersion;
  }

  @Override
  public void setTaloolVersion(String taloolVersion) {
    this.taloolVersion = taloolVersion;
  }

  @Override
  public String getUserAgent() {
    return userAgent;
  }

  @Override
  public void setUserAgent(final String userAgent) {
    this.userAgent = userAgent;
  }

  @Override
  public Date getUpdated() {
    return updated;
  }

  @Override
  public String getDeviceToken() {
    return deviceToken;
  }

  @Override
  public void setDeviceToken(String deviceToken) {
    this.deviceToken = deviceToken;
  }

  @Override
  public String getDeviceOsVersion() {
    return deviceOsVersion;
  }

  @Override
  public void setDeviceOsVersion(String deviceOsVersion) {
    this.deviceOsVersion = deviceOsVersion;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

}
