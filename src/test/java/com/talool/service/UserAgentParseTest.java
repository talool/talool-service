package com.talool.service;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;

import com.talool.utils.MobileUserAgentParser;
import com.talool.utils.MobileUserAgentParser.MobileUserAgent;



public class UserAgentParseTest {

  @Test
  public void testTaloolAndroid() {
    String iosUserAgent = "Talool/1.3.1 (iPhone; CPU iPhone OS 8_0_2 like Mac OS X)";
    String iosUserAgent2 = "Talool/1.3.1 (iPhone; CPU iPhone OS 7_1_2 like Mac OS X)";
    String androidAgent = "Talool/1.1.8 (Linux; Android 4.4.2; Samsung SPH-L720)";
    String androidAgent2 = "Talool/1.1.8 (Linux; Android 4.4.2; Samsung SCH-I435)";

    String androidAgent3 = "Talool/1.1.8 (Linux; Android 4.4.4; LGE Nexus 4)";
    String cocoaUnknown = "Cocoa/THTTPClient";

    MobileUserAgent mua = MobileUserAgentParser.parse(iosUserAgent);
    Assert.assertEquals("1.3.1", mua.getAppVersion());
    Assert.assertEquals("iPhone", mua.getOsName());
    Assert.assertEquals("8_0_2", mua.getOsVersion());

    mua = MobileUserAgentParser.parse(iosUserAgent2);
    Assert.assertEquals("1.3.1", mua.getAppVersion());
    Assert.assertEquals("iPhone", mua.getOsName());
    Assert.assertEquals("7_1_2", mua.getOsVersion());

    mua = MobileUserAgentParser.parse(androidAgent);
    Assert.assertEquals("1.1.8", mua.getAppVersion());
    Assert.assertEquals("Android", mua.getOsName());
    Assert.assertEquals("4.4.2", mua.getOsVersion());

    mua = MobileUserAgentParser.parse(androidAgent2);
    Assert.assertEquals("1.1.8", mua.getAppVersion());
    Assert.assertEquals("Android", mua.getOsName());
    Assert.assertEquals("4.4.2", mua.getOsVersion());

    mua = MobileUserAgentParser.parse(androidAgent3);
    Assert.assertEquals("1.1.8", mua.getAppVersion());
    Assert.assertEquals("Android", mua.getOsName());
    Assert.assertEquals("4.4.4", mua.getOsVersion());

    mua = MobileUserAgentParser.parse(cocoaUnknown);
    Assert.assertNull(mua);


  }


  @Test
  public void testMe() {

    // LinkedHashMap<String, Integer> lm = new LinkedHashMap<String, Integer>(100, .75f, true);
    // lm.put("ann", 3);
    // lm.put("chris", 3);
    // lm.put("brian", 2);
    // lm.put("chris", 3);
    // lm.put("brian", 2);
    // lm.put("chris", 3);
    // lm.put("brian", 2);
    // lm.put("ann", 3);
    //
    // for (Entry<String, Integer> en : lm.entrySet()) {
    // System.out.println(en.getKey() + "; " + en.getValue());
    // }


    LinkedList<String> list = new LinkedList();

    list.add("chris");
    list.add("billy");
    list.add("john");

    System.out.println(list.poll());

    CountDownLatch latch = new CountDownLatch(3);



  }
}
