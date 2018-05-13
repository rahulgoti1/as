package com.ams.controller;

import com.ams.domain.User;
import com.ams.exception.EmailAlreadyUsedException;
import com.ams.exception.InternalServerErrorException;
import com.ams.exception.InvalidOTPException;
import com.ams.exception.InvalidPasswordException;
import com.ams.exception.LoginAlreadyUsedException;
import com.ams.repository.UserRepository;
import com.ams.security.SecurityUtils;
import com.ams.service.MailService;
import com.ams.service.UserService;
import com.ams.service.dto.KeyAndPasswordVM;
import com.ams.service.dto.ManagedUserVM;
import com.ams.service.dto.UserDTO;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountController {

  private final Logger log = LoggerFactory.getLogger(AccountController.class);

  private final UserRepository userRepository;

  private final UserService userService;

  private final MailService mailService;

  public AccountController(UserRepository userRepository, UserService userService, MailService mailService) {

    this.userRepository = userRepository;
    this.userService = userService;
    this.mailService = mailService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
    if (!checkPasswordLength(managedUserVM.getPassword())) {
      throw new InvalidPasswordException();
    }
    userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).ifPresent(u -> {
      throw new LoginAlreadyUsedException();
    });
    userRepository.findOneByEmailIgnoreCase(managedUserVM.getEmail()).ifPresent(u -> {
      throw new EmailAlreadyUsedException();
    });
    User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
    mailService.sendActivationEmail(user);
  }

  @GetMapping("/activate")
  public void activateAccount(@RequestParam(value = "key") String key) {
    Optional<User> user = userService.activateRegistration(key);
    if (!user.isPresent()) {
      throw new InternalServerErrorException("No user was found for this reset key");
    }
  }

  @GetMapping("/authenticate")
  public String isAuthenticated(HttpServletRequest request) {
    log.debug("REST request to check if the current user is authenticated");
    return request.getRemoteUser();
  }

  @GetMapping("/account")
  public UserDTO getAccount() {
    return userService.getUserWithAuthorities()
        .map(UserDTO::new)
        .orElseThrow(() -> new InternalServerErrorException("User could not be found"));
  }

  @PostMapping("/account")
  public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
    final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
    Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
    if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
      throw new EmailAlreadyUsedException();
    }
    Optional<User> user = userRepository.findOneByLogin(userLogin);
    if (!user.isPresent()) {
      throw new InternalServerErrorException("User could not be found");
    }
    userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
        userDTO.getLangKey());
  }

  @PostMapping(path = "/account/change-password")
  public void changePassword(@RequestBody String password) {
    if (!checkPasswordLength(password)) {
      throw new InvalidPasswordException();
    }
    userService.changePassword(password);
  }

  @PostMapping(path = "/account/reset-password/init")
  public void requestPasswordReset(@RequestBody String mobile) {
    log.debug("Rest request for password reset of :{}", mobile);
    userService.requestPasswordReset(mobile);
  }

  @PostMapping(path = "/account/reset-password/finish")
  public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
    log.debug("Rest request for password finish of :{}", keyAndPassword.getMobile());
    if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
      throw new InvalidPasswordException();
    }
    boolean flag = userService.completePasswordReset(keyAndPassword);
    if (!flag) {
      throw new InvalidOTPException();
    }
  }

  private static boolean checkPasswordLength(String password) {
    return !StringUtils.isEmpty(password) &&
        password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
        password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
  }
}
