package com.talool.utils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser for Talool mobile device user-agent strings. Examples are:
 * 
 * Talool/1.3.1 (iPhone; CPU iPhone OS 8_0_2 like Mac OS X)
 * 
 * Talool/1.1.8 (Linux; Android 4.4.2; Samsung SPH-L720)
 * 
 * @author clintz
 * 
 */
public class MobileUserAgentParser {
  private static final Pattern MUA = Pattern.compile("Talool/(.*) \\((iPhone; CPU |Linux; )(Android|iPhone)[ ](OS )?([._0-9]*).*\\)");

  /**
   * An immutable class representing the various parts of a Talool mobile user-agent string
   * 
   * @author clintz
   * 
   */
  public static class MobileUserAgent implements Serializable {
    private static final long serialVersionUID = 1323115039055691286L;

    private String userAgent;
    private String appVersion;
    private String osName;
    private String osVersion;

    public MobileUserAgent(String userAgent, String appVersion, String osName, String osVersion) {
      super();
      this.userAgent = userAgent;
      this.appVersion = appVersion;
      this.osName = osName;
      this.osVersion = osVersion;
    }

    public String getUserAgent() {
      return userAgent;
    }

    public String getAppVersion() {
      return appVersion;
    }

    public String getOsName() {
      return osName;
    }

    public String getOsVersion() {
      return osVersion;
    }

  }

  public static MobileUserAgent parse(final String userAgent) {
    final Matcher matcher = MUA.matcher(userAgent);
    MobileUserAgent mua = null;

    if (matcher.matches()) {
      String appVersion = matcher.group(1);
      String osName = matcher.group(3);
      String osVersion = matcher.group(5);
      mua = new MobileUserAgent(userAgent, appVersion, osName, osVersion);
    }

    return mua;

  }
}
