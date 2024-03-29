/**
 * Copyright 2011, Comcast Corporation. This software and its contents are Comcast confidential and
 * proprietary. It cannot be used, disclosed, or distributed without Comcast's prior written
 * permission. Modification of this software is only allowed at the direction of Comcast
 * Corporation. All allowed modifications must be provided to Comcast Corporation.
 */
package com.talool.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;

/**
 * @author clintz
 * 
 */
@Embeddable
public class Properties implements Serializable {
  private static final long serialVersionUID = -4835611475842607243L;

  @Type(type = "hstore")
  @Column(name = "properties", columnDefinition = "hstore")
  private Map<String, String> properties = new HashMap<String, String>();

  public Map<String, String> getAllProperties() {
    return properties;
  }

  public String getAsString(final String key) {
    return properties.get(key);
  }

  public Integer getAsInt(final String key) {
    String val = properties.get(key);
    return val == null ? null : Integer.valueOf(properties.get(key));
  }

  public Float getAsFloat(final String key) {
    String val = properties.get(key);
    return val == null ? null : Float.valueOf(val);
  }

  public boolean getAsBool(final String key) {
    return Boolean.valueOf(properties.get(key));
  }

  public Double getAsDouble(final String key) {
    String val = properties.get(key);
    return val == null ? null : Double.valueOf(val);
  }

  public Long getAsLong(final String key) {
    String val = properties.get(key);
    return val == null ? null : Long.valueOf(val);
  }

  public Short getAsShort(final String key) {
    String val = properties.get(key);
    return val == null ? null : Short.valueOf(val);
  }

  public void createOrReplace(final String key, final int value) {
    properties.put(key, String.valueOf(value));
  }

  public void createOrReplace(final String key, final String value) {
    properties.put(key, value);
  }

  public void createOrReplace(final String key, final float value) {
    properties.put(key, String.valueOf(value));
  }

  public void createOrReplace(final String key, final long value) {
    properties.put(key, String.valueOf(value));
  }

  public void createOrReplace(final String key, final boolean value) {
    properties.put(key, String.valueOf(value));
  }

  public void createOrReplace(final String key, final double value) {
    properties.put(key, String.valueOf(value));
  }

  public void remove(final String key) {
    properties.remove(key);
  }

  /**
   * Returns true if the property exists (not null)
   * 
   * @param key
   * @return
   */
  public boolean exists(final String key) {
    return properties.get(key) != null;
  }

  public String dumpProperties() {
    StringBuilder sb = new StringBuilder();
    for (Entry<String, String> entry : properties.entrySet()) {
      sb.append(entry.getKey()).append(" -> ").append(entry.getValue());
    }
    return sb.toString();

  }

}
