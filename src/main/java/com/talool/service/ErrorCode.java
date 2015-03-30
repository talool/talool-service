package com.talool.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author clintz
 * 
 */

public enum ErrorCode {
  UNKNOWN(0, "Unknown"), VALID_EMAIL_REQUIRED(100, "Valid email required"), PASS_REQUIRED(101, "Password is required"), PASS_CONFIRM_MUST_MATCH(102,
      "Password confirmation does not match"), PASS_RESET_CODE_REQUIRED(103, "Password reset code required"), PASS_RESET_CODE_EXPIRED(104,
      "Password reset code expired"), PASS_RESET_CODE_INVALID(105, "Password reset code invalid"), VALID_EMAIL_OPTIONAL(107,
      "If you choose to use an email address, it must be a valid email"), VALID_USERNAME_OPTIONAL(108,
      "If you choose to use a username, it can only contain letters, numbers, hyphens and dashes"),

  ACCOUNT_ALREADY_TAKEN(1000, "Account already taken"), INVALID_USERNAME_OR_PASSWORD(1001, "Invalid account or password"), CUSTOMER_DOES_NOT_OWN_DEAL(
      1002, "Customer does not own deal"), DEAL_ALREADY_REDEEMED(1003, "Deal already redeemed"), GIFTING_NOT_ALLOWED(1004,
      "Cannot gift deal due to acquire status"), CUSTOMER_NOT_FOUND(1005, "Customer not found"), EMAIL_REQUIRED(1006, "Email is required"), EMAIL_OR_PASS_INVALID(
      1007, "Invalid account or password"), NOT_GIFT_RECIPIENT(1008, "Not the gift recipient"), GIFT_ALREADY_ACCEPTED(1009, "Gift already accepted"),

  LIMIT_ONE_PURCHASE_PER_CUSTOMER(1010, "Limit one purchase per customer has been reached"),


  GENERAL_PROCESSOR_ERROR(1500, "Unknown processor error"),

  MAIL_TEMPLATE_NOT_FOUND(2004, "Mail template not found"), ACTIVIATION_CODE_NOT_FOUND(3000, "Activiation code not found"), ACTIVIATION_CODE_ALREADY_ACTIVATED(
      3001, "Activiation code already activated"),

  GEOCODER_ERROR(4001, "We're having trouble getting the lat/long from Google.  Please try again later."), GEOCODER_OVER_QUERY_LIMIT(4002,
      "We've exceeded our limit for daily geocoder requests.  Please try again tomorrow."), MERCHANT_LOCATION_GEOMETRY_NULL(4003,
      "We couldn't locate that address on the map.  Please review the address and try again."),

  MERCHANT_CODE_IS_NOT_VALID(4004, "Code is not valid"),

  DEAL_MOVED_NOT_DELETED(4005, "Deal was moved, not deleted"), DEAL_CAN_NOT_BE_DELETED(4006, "Deal can not be deleted from this book"),

  BRAINTREE_INVALID_WEBHOOK_PARAMS(5000, "Invalid Webhook Params"), BRAINTREE_SUBMERCHANT_ID_NOT_FOUND(5001, "Sub Merchant ID not found");

  private final int code;
  private final String message;
  private static Map<Integer, ErrorCode> errorCodeMap = new HashMap<Integer, ErrorCode>();

  static {
    for (ErrorCode ec : ErrorCode.values()) {
      errorCodeMap.put(ec.getCode(), ec);
    }

  }

  public static ErrorCode findByCode(final int code) {
    return errorCodeMap.get(code);
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }


}
