package com.talool.core;

import java.util.Date;
import java.util.UUID;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Mobile presence including user-agent, IP, Lat/Long, city, state, country and zip
 * 
 * @author clintz
 * 
 */
public interface DevicePresence extends IdentifiableUUID {

  public UUID getCustomerId();


  public void setCustomerId(final UUID customerId);

  /**
   * Gets the mobile deviceId
   * 
   * @return
   */
  public String getDeviceId();

  /**
   * Sets the mobile deviceId
   * 
   * @param deviceId
   */
  public void setDeviceId(final String deviceId);

  public String getDeviceOsVersion();

  public void setDeviceOsVersion(final String deviceOsVersion);

  /**
   * Gets the mobile device type (Android, iOS, etc)
   * 
   * @return
   */
  public String getDeviceType();

  /**
   * Sets the mobile device type (Android, iOS, etc)
   * 
   * @param deviceType
   */
  public void setDeviceType(final String deviceType);

  /**
   * Gets the talool release version for the given device type
   * 
   * @return
   */
  public String getTaloolVersion();

  /**
   * Sets the talool release version for the given device type
   * 
   * @param taloolVersion
   */
  public void setTaloolVersion(final String taloolVersion);

  public String getUserAgent();

  public void setUserAgent(final String userAgent);

  public Geometry getLocation();

  public void setLocation(final Geometry location);

  public String getIp();

  public void setIp(final String ip);

  public String getCity();

  public void setCity(final String city);

  public String getStateCode();

  public void setStateCode(final String stateCode);

  public String getZip();

  public void setZip(final String zip);

  public String getCountry();

  public void setCountry(final String country);

  public Date getUpdated();

  /**
   * Gets the device token which if an Android device is a gcmDeviceToken and on iOS it is the
   * apnDeviceToken
   * 
   * @return
   */
  public String getDeviceToken();

  /**
   * Sets the device token which if an Android device is a gcmDeviceToken and on iOS it is the
   * apnDeviceToken
   * 
   * @param deviceToken
   */
  public void setDeviceToken(String deviceToken);

}
