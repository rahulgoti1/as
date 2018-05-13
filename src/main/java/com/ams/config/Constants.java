package com.ams.config;

/**
 * Application constants.
 */
public final class Constants {

  // Regex for acceptable logins
  public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

  public static final String SYSTEM_ACCOUNT = "system";
  public static final String ANONYMOUS_USER = "anonymoususer";
  public static final String DEFAULT_LANGUAGE = "en";

  public static final String MOBILE_REGEX = "^$|[0-9]{10}";
  public static final String TIME_REGEX = "^(2[0-3]|[01]?[0-9]):([0-5]?[0-9]):([0-5]?[0-9])$";

  private Constants() {
  }
}
