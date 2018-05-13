package com.ams.service;

import com.ams.domain.User;
import com.ams.exception.UserNotFoundException;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Created by grahul on 10-05-2018.
 */
@Service
public class UserJWTService {

  private final AdminService adminService;

  public UserJWTService(AdminService adminService) {
    this.adminService = adminService;
  }

  public Long getLoginId() {
    Optional<User> userWithAuthorities = adminService.getUserWithAuthorities();
    if (userWithAuthorities.isPresent()) {
      return userWithAuthorities.get().getId();
    } else {
      throw new UserNotFoundException();
    }
  }


}
