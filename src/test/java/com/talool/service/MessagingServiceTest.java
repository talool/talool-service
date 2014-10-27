package com.talool.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DevicePresence;
import com.talool.core.DomainFactory;
import com.talool.core.FactoryManager;
import com.talool.core.Location;
import com.talool.core.MerchantAccount;
import com.talool.core.service.InvalidInputException;
import com.talool.core.service.ServiceException;
import com.talool.messaging.MessagingFactory;
import com.talool.messaging.job.MerchantGiftJob;

@TestExecutionListeners(TransactionalTestExecutionListener.class)
// Rolls back transactions by default
public class MessagingServiceTest extends HibernateFunctionalTestBase {

  private static final Logger LOG = LoggerFactory.getLogger(MessagingServiceTest.class);

  private DomainFactory domainFactory;

  @Before
  public void setup() {
    domainFactory = FactoryManager.get().getDomainFactory();
  }

  @Test
  public void testSchedulingJob() throws ServiceException, InvalidInputException {
    try {
      // List<MessagingJob> jobs =
      // ServiceFactory.get().getMessagingService().getMessagingJobsByMerchantAccount(2l);

      List<Customer> targetedCustomers = new ArrayList<Customer>();
      // targetedCustomers.add(customerService.getCustomerByEmail("douglasmccuen@yahoo.com"));
      // targetedCustomers.add(customerService.getCustomerByEmail("doug@talool.com"));
      targetedCustomers.add(customerService.getCustomerByEmail("christopher.justin@gmail.com"));
      targetedCustomers.add(customerService.getCustomerByEmail("chris@talool.com"));
      // targetedCustomers.add(customerService.getCustomerByEmail("chris@talool.com"));

      Customer fromCustomer = customerService.getCustomerByEmail("chris@talool.com");

      Deal deal = taloolService.getDeal(UUID.fromString("5a2f1b65-53f6-4db7-9a66-5dbdabf2f932"));

      MerchantAccount merchantAccount = taloolService.getMerchantAccountById(2l); // chris@talool.com

      MerchantGiftJob job =
          MessagingFactory.newMerchantGiftJob(deal.getMerchant(), merchantAccount, fromCustomer, deal, new Date(), "some job notes");

      ServiceFactory.get().getMessagingService().scheduleMessagingJob(job, targetedCustomers);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }



  public void testDevicePresence() throws ServiceException {
    Location location = domainFactory.newLocation(-105.281686, 40.017663);
    List<DevicePresence> mobilePresences = new ArrayList<DevicePresence>();

    DevicePresence presence = FactoryManager.get().getDomainFactory().newMobilePresence();
    presence.setUserAgent("Talool/1.1.8 (Linux; Android 4.4.2; Samsung SPH-L720)");
    presence.setIp("71.237.43.59");
    presence.setDeviceId("sd98239090237823");
    presence.setCustomerId(UUID.fromString("d26b2473-56db-42ff-bc62-eb67aa7f96b9"));
    presence.setLocation(FactoryManager.get().getDomainFactory().newPoint(location));
    mobilePresences.add(presence);

    ServiceFactory.get().getMessagingService().updateDevicePresences(mobilePresences);
  }


}
