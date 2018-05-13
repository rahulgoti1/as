package com.ams.service;

import com.ams.domain.Authority;
import com.ams.domain.User;
import com.ams.exception.AccountInactiveException;
import com.ams.exception.InternalServerErrorException;
import com.ams.exception.MobileNotFoundException;
import com.ams.repository.AdminInfoRepository;
import com.ams.repository.AuthorityRepository;
import com.ams.repository.UserRepository;
import com.ams.security.AuthoritiesConstants;
import com.ams.security.SecurityUtils;
import com.ams.service.dto.KeyAndPasswordVM;
import com.ams.service.dto.UserDTO;
import com.ams.service.util.RandomUtil;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service class for managing users.
 */
@Service
@Transactional()
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthorityRepository authorityRepository;
  private final AdminInfoRepository adminInfoRepository;
  private final MailService mailService;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, AdminInfoRepository adminInfoRepository, MailService mailService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authorityRepository = authorityRepository;
    this.adminInfoRepository = adminInfoRepository;
    this.mailService = mailService;
  }

  public Optional<User> activateRegistration(String key) {
    log.debug("Activating user for activation key {}", key);
    return userRepository.findOneByActivationKey(key)
        .map(user -> {
          // activate given user for the registration key.
          user.setActivated(true);
          user.setActivationKey(null);
//          userSearchRepository.save(user);
          log.debug("Activated user: {}", user);
          return user;
        });
  }

  public boolean completePasswordReset(KeyAndPasswordVM keyAndPasswordVM) {
    log.debug("Reset user password for otp key {}", keyAndPasswordVM.getKey());
    Optional<User> userOptional = null;
    if (!StringUtils.isEmpty(keyAndPasswordVM.getMobile())) {
      userOptional = userRepository.findOneByMobile(keyAndPasswordVM.getMobile());
    }

    /**
     * Invalid Mobile
     */
    if (userOptional == null || !userOptional.isPresent()) {
      throw new InternalServerErrorException("No user was found for this login");
    }
    /**
     * User Inactive
     */
    else if (!userOptional.get().getActivated()) {
      throw new AccountInactiveException();
    }
    /**
     * Valid Mobile and OTP
     */
    else if (keyAndPasswordVM.getKey().equals(userOptional.get().getMobileKey())) {
      userOptional.get().setPassword(passwordEncoder.encode(keyAndPasswordVM.getNewPassword()));
      userOptional.get().setMobileKey(null);
      userOptional.get().setResetDate(null);
      userOptional.get().setActivated(true);
      userOptional.get().setResetAttempt(0);
      return true;
    }
    /**
     * Invalid OTP
     */
    else {
      userOptional.get().setActivated(true);
      Integer attemptCount = userOptional.get().getResetAttempt();
      if (attemptCount >= 2) {
        userOptional.get().setActivated(false);
      }
      userOptional.get().setResetDate(new Date());
      userOptional.get().setResetAttempt(attemptCount + 1);
      return false;
    }
  }

  public Optional<User> requestPasswordReset(String mobile) {
    Optional<User> userOptional = userRepository.findOneByMobile(mobile);
    if (!userOptional.isPresent()) {
      throw new MobileNotFoundException();
    } else if (!userOptional.get().getActivated()) {
      throw new AccountInactiveException();
    } else {
      User user = userOptional.get();
      user.setResetAttempt(0);
      user.setResetDate(new Date());
      user.setMobileKey(RandomUtil.generateKeyWithSize(6));

      /**
       * Message Sending Login
       */

      /**
       * Mail Sending Login
       */
      if (user.getEmail() != null && "Y".equals(user.getIsEmailVerified())) {
        mailService.sendPasswordResetOtp(user);
      }

      return userOptional;
    }
  }

  public User registerUser(UserDTO userDTO, String password) {

    User newUser = new User();
    Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
    Set<Authority> authorities = new HashSet<>();
    String encryptedPassword = passwordEncoder.encode(password);
    newUser.setLogin(userDTO.getLogin());
    // new user gets initially a generated password
    newUser.setPassword(encryptedPassword);
    newUser.setFirstName(userDTO.getFirstName());
    newUser.setLastName(userDTO.getLastName());
    newUser.setEmail(userDTO.getEmail());
    newUser.setLangKey(userDTO.getLangKey());
    // new user is not active
    newUser.setActivated(false);
    // new user gets registration key
    newUser.setActivationKey(RandomUtil.generateActivationKey());
    authorities.add(authority);
    newUser.setAuthorities(authorities);
    userRepository.save(newUser);
//    userSearchRepository.save(newUser);
    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * Update basic information (first name, last name, email, language) for the current user.
   *
   * @param firstName first name of user
   * @param lastName  last name of user
   * @param email     email id of user
   * @param langKey   language key
   */
  public void updateUser(String firstName, String lastName, String email, String langKey) {
    SecurityUtils.getCurrentUserLogin()
        .flatMap(userRepository::findOneByLogin)
        .ifPresent(user -> {
          user.setFirstName(firstName);
          user.setLastName(lastName);
          user.setEmail(email);
          user.setLangKey(langKey);
//          userSearchRepository.save(user);
          log.debug("Changed Information for User: {}", user);
        });
  }

  public void changePassword(String password) {
    SecurityUtils.getCurrentUserLogin()
        .flatMap(userRepository::findOneByLogin)
        .ifPresent(user -> {
          String encryptedPassword = passwordEncoder.encode(password);
          user.setPassword(encryptedPassword);
          log.debug("Changed password for User: {}", user);
        });
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserWithAuthorities() {
    return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
  }

}
