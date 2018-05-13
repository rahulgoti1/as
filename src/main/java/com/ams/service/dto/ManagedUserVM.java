package com.ams.service.dto;

import javax.validation.constraints.Size;

/**
 * View Model extending the UserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends UserDTO {

  public static final int PASSWORD_MIN_LENGTH = 5;

  public static final int PASSWORD_MAX_LENGTH = 20;

  @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
  private String password;

  public ManagedUserVM() {
    // Empty constructor needed for Jackson.
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "ManagedUserVM{" +
        "} " + super.toString();
  }
}
