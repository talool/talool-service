package com.talool.messaging;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.talool.core.DevicePresence;
import com.talool.geo.MaxMindUtil;

/**
 * A singleton DevicePresence manager responsible for efficiently updating device presence
 * 
 * 
 * @author clintz
 * 
 */
public final class DevicePresenceManager {
  private static final Logger LOG = LoggerFactory.getLogger(DevicePresenceManager.class);
  private static final DevicePresenceManager INSTANCE = new DevicePresenceManager();
  private static final int MAX_BATCH_SIZE = 100;
  private static final long SLEEP_TIME_IN_MILLS = 30000; // 30 secs

  private volatile boolean isRunning = false;
  private DevicePresenceManagerThread geoLocationManagerThread;
  private final ConcurrentLinkedQueue<DevicePresence> queue = new ConcurrentLinkedQueue<DevicePresence>();


  private class DevicePresenceManagerThread extends Thread {
    DevicePresenceManagerThread(String name) {
      super(name);
    }

    @Override
    public void run() {
      DevicePresence devicePresence = null;
      int elements = 0;
      final List<DevicePresence> customerLocations = new ArrayList<DevicePresence>();
      CityResponse cityResponse = null;

      while (isRunning) {
        while ((devicePresence = queue.poll()) != null && (elements <= MAX_BATCH_SIZE)) {
          if (StringUtils.isNotEmpty(devicePresence.getIp())) {
            try {
              cityResponse = MaxMindUtil.get().lookup(devicePresence.getIp());
            } catch (UnknownHostException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            } catch (GeoIp2Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            if (cityResponse != null) {
              devicePresence.setCity(cityResponse.getCity().getName());
              devicePresence.setStateCode(cityResponse.getMostSpecificSubdivision().getIsoCode());
              devicePresence.setZip(cityResponse.getPostal().getCode());
              devicePresence.setCountry(cityResponse.getCountry().getIsoCode());
            }

            customerLocations.add(devicePresence);
            elements++;
          }

          elements = 0;

          updateDevicePresences(customerLocations);
          customerLocations.clear();

          try {
            Thread.sleep(SLEEP_TIME_IN_MILLS);
          } catch (InterruptedException e) {
            // purposely void
          }

        
      }

  public static DevicePresenceManager get() {
    return INSTANCE;
  }

  private DevicePresenceManager() {
    geoLocationManagerThread = new DevicePresenceManagerThread("GeoLocationManagerThread");
    isRunning = true;
    geoLocationManagerThread.start();
  }



  void updateDevicePresences(final List<DevicePresence> devicePresences) {

  }


  /**
   * Non-blocking/asynchronous update of a DevicePresence. This method will also decorate with
   * MaxMind location data if a valid IP address is set
   * 
   * @param devicePresence
   */
  public void updateDevicePresence(final DevicePresence devicePresence) {
    queue.add(devicePresence);
  }

}
