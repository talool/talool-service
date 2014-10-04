package com.talool.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.talool.core.Location;

/**
 * 
 * @author clintz
 * 
 */
@Embeddable
public class LocationImpl implements Location {
  private static final long serialVersionUID = 6198741717898788920L;

  @Column(name = "longitude", unique = false, nullable = true)
  private Double longitude;

  @Column(name = "latitude", unique = false, nullable = true)
  private Double latitude;

  public LocationImpl() {}

  public LocationImpl(final Double longitude, final Double latitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  @Override
  public Double getLongitude() {
    return longitude;
  }

  @Override
  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  @Override
  public Double getLatitude() {
    return latitude;
  }

  @Override
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (!(obj instanceof LocationImpl)) {
      return false;
    }

    final LocationImpl other = (LocationImpl) obj;

    return new EqualsBuilder().append(getLongitude(), other.getLongitude()).append(getLatitude(), other.getLatitude()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(getLatitude()).append(getLongitude()).hashCode();
  }
}
