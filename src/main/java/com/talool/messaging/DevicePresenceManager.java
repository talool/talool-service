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
import com.talool.core.service.ServiceException;
import com.talool.geo.MaxMindUtil;
import com.talool.service.ServiceFactory;
import com.talool.utils.MobileUserAgentParser;
import com.talool.utils.MobileUserAgentParser.MobileUserAgent;


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

  private volatile boolean isRunning = false;
  private DevicePresenceManagerThread devicePresenceManagerThread;
  private final ConcurrentLinkedQueue<DevicePresence> queue = new ConcurrentLinkedQueue<DevicePresence>();


  private DevicePresenceManager() {
    devicePresenceManagerThread = new DevicePresenceManagerThread("devicePresenceManagerThread");
    isRunning = true;
    devicePresenceManagerThread.start();

    // adding shutdown hook to gracefully stop messaging manager
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // prevent new tasks from being submitted
        LOG.info("Shutting down MessagingJobManager.  No new tasks will be submitted..");
        isRunning = false;
      }
    });
  }

  public static DevicePresenceManager get() {
    return INSTANCE;
  }

  /**
   * Non-blocking/asynchronous update of a DevicePresence. This method will also decorate with
   * MaxMind location data if a valid IP address is set
   * 
   * 
   * @param devicePresence
   */
  public void updateDevicePresence(final DevicePresence devicePresence) {
    queue.add(devicePresence);
  }


  private class DevicePresenceManagerThread extends Thread {
    DevicePresenceManagerThread(String name) {
      super(name);
    }

    @Override
    public void run() {
      DevicePresence devicePresence = null;
      int elements = 0;
      final List<DevicePresence> devicePresences = new ArrayList<DevicePresence>();
      CityResponse cityResponse = null;

      while (isRunning) {
        try {
          while ((devicePresence = queue.poll()) != null && (elements <= MAX_BATCH_SIZE)) {
            if (StringUtils.isNotEmpty(devicePresence.getIp())) {
              try {
                cityResponse = MaxMindUtil.get().lookup(devicePresence.getIp());
                if (cityResponse != null) {
                  devicePresence.setCity(cityResponse.getCity().getName());
                  devicePresence.setStateCode(cityResponse.getMostSpecificSubdivision().getIsoCode());
                  devicePresence.setZip(cityResponse.getPostal().getCode());
                  devicePresence.setCountry(cityResponse.getCountry().getIsoCode());
                }
              } catch (UnknownHostException e) {
                LOG.error(e.getLocalizedMessage(), e);
              } catch (IOException e) {
                LOG.error(e.getLocalizedMessage(), e);
              } catch (GeoIp2Exception e) {
                LOG.error(e.getLocalizedMessage(), e);
              }
            } // end if

            if (devicePresence.getUserAgent() != null) {
              MobileUserAgent agent = MobileUserAgentParser.parse(devicePresence.getUserAgent());
              if (agent != null) {
                devicePresence.setTaloolVersion(agent.getAppVersion());
                devicePresence.setDeviceType(agent.getOsName());
                devicePresence.setDeviceOsVersion(agent.getOsName());
              }
            }

            devicePresences.add(devicePresence);
            elements++;
          } // end while

          try {
            ServiceFactory.get().getMessagingService().updateDevicePresences(devicePresences);
          } catch (ServiceException e) {
            LOG.error("Problem updating " + devicePresences.size() + " device presences", e);
          }

          elements = 0;
          devicePresences.clear();

          try {
            // sleeping for 5 seconds to simply try a larger batch on the queue poll
            sleep(5000);
          } catch (InterruptedException e) {
            // ignore
          }
        } catch (Exception e) {
          LOG.error("Problem inside devicePresence thread:", e);
        } finally {
          // for safety, lets make sure we don't memory leak on exceptions. its ok if we lost a
          // batch of updates
          elements = 0;
          devicePresences.clear();
        }
      } // end while isRunning

    }
  }
}
