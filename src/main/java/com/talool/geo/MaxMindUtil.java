package com.talool.geo;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;


/**
 * MaxMind wrapper for the GeoIP2 City DB
 * 
 * @see <a href="https://github.com/maxmind/GeoIP2-java">https://github.com/maxmind/GeoIP2-java</a>
 * @author clintz
 * 
 */
public class MaxMindUtil {
  private static final String DEFAULT_DB_PATH = "/usr/local/share/GeoIP/GeoIP2-City.mmdb";
  private static MaxMindUtil INSTANCE;
  private final DatabaseReader dbReader;

  private MaxMindUtil(final String dbPath) throws IOException {
    File database = new File(dbPath);
    dbReader = new DatabaseReader.Builder(database).build();
  }

  /**
   * Creates and instance using the cityDbPath
   * 
   * @param cityDbPath
   * @return
   * @throws IOException
   */
  public synchronized static MaxMindUtil createInstance(final String cityDbPath) throws IOException {
    if (INSTANCE == null) {
      INSTANCE = new MaxMindUtil(cityDbPath);
    }
    return INSTANCE;
  }

  /**
   * Creates and instance using the default path /usr/local/share/GeoIP/GeoIP2-City.mmdb
   * 
   * @return
   * @throws IOException
   */
  public synchronized static MaxMindUtil createInstance() throws IOException {
    return MaxMindUtil.createInstance(DEFAULT_DB_PATH);
  }

  public static MaxMindUtil get() {
    return INSTANCE;
  }

  public CityResponse lookup(final String ipAddress) throws UnknownHostException, IOException, GeoIp2Exception {
    return dbReader.city(InetAddress.getByName(ipAddress));
  }


  public static void main(String args[]) throws UnknownHostException, IOException, GeoIp2Exception {

    CityResponse response = MaxMindUtil.createInstance().lookup("71.237.43.59");


    System.out.println(response.getCity().getName());
    System.out.println(response.getPostal().getCode());
    System.out.println(response.getCountry());
    System.out.println(response.getMostSpecificSubdivision().getIsoCode()); // CO
    System.out.println(response.getMostSpecificSubdivision().getName()); // Colorado


  }

}
