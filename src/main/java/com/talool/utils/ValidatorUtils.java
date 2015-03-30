package com.talool.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validation Utils
 * 
 * @author clintz
 * 
 */
public final class ValidatorUtils {
  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

  /**
   * Validates a user name which can only be alpha-numeric with "_" or "-"
   * 
   * @param userName
   * @return
   */
  public static boolean isValidUsername(final String userName) {
    Matcher m = USERNAME_PATTERN.matcher(userName);
    return m.find() ? true : false;
  }
}
