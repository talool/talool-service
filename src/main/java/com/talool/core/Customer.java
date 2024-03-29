package com.talool.core;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.SocialNetwork;

/**
 * 
 * @author clintz
 * 
 */
public interface Customer extends IdentifiableUUID, Serializable, TimeAware {
  public String getFullName();

  public String getFirstName();

  public void setFirstName(String firstName);

  public String getLastName();

  public void setLastName(String lastName);

  public String getEmail();

  public void setEmail(String email);

  /**
   * Gets the encrypted password
   * 
   * @return
   */
  public String getPassword();

  /**
   * Encrptys and sets the password
   * 
   * @param password
   */
  public void setPassword(String password);

  public void setResetPasswordCode(String code);

  public String getResetPasswordCode();

  public void setResetPasswordExpires(Date expires);

  public Date getResetPasswordExpires();

  public Sex getSex();

  public void setSex(Sex sex);

  public Date getBirthDate();

  public void setBirthDate(Date birthDate);

  public Map<SocialNetwork, CustomerSocialAccount> getSocialAccounts();

  public void addSocialAccount(CustomerSocialAccount socialAccount);

  public void removeSocialAccount(CustomerSocialAccount socialAccount);

  /**
   * Returns true if the email has no known associated issues determined from MTA agents. For
   * example, if the email address bounced false would be returned. Othwerwise it is believed to be
   * avalid address
   * 
   * @return
   */
  public boolean isEmailValid();

  public void setIsEmailValid(final boolean isValid);

  public UUID getWhiteLabelMerchantId();

  public void setWhiteLabelMerchantId(final UUID whiteLabelMerchantId);


}
