package com.ams.service;

import com.ams.config.Constants;
import com.ams.domain.AdminInfo;
import com.ams.domain.Authority;
import com.ams.domain.User;
import com.ams.exception.ErrorConstants;
import com.ams.exception.FieldValidationException;
import com.ams.exception.InternalServerErrorException;
import com.ams.repository.AdminInfoRepository;
import com.ams.repository.AuthorityRepository;
import com.ams.repository.UserRepository;
import com.ams.security.AuthoritiesConstants;
import com.ams.security.SecurityUtils;
import com.ams.service.dto.AdminInfoDTO;
import com.ams.service.util.RandomUtil;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
@EnableScheduling
public class AdminService {

  private final Logger log = LoggerFactory.getLogger(AdminService.class);

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final AuthorityRepository authorityRepository;

  private final AdminInfoRepository adminInfoRepository;


  public AdminService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, AdminInfoRepository adminInfoRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authorityRepository = authorityRepository;
    this.adminInfoRepository = adminInfoRepository;
  }

  public AdminInfoDTO createUser(AdminInfoDTO userDTO) throws ParseException {
    User user = new User();
    AdminInfo adminInfo = null;
    String loginRole = null;

    user.setLogin(userDTO.getLogin());
    user.setFirstName(userDTO.getFirstName());
    user.setLastName(userDTO.getLastName());
    user.setEmail(userDTO.getEmail());
    user.setMobile(userDTO.getMobile());
    user.setIsMobileVerified("N");
    user.setIsEmailVerified("N");
    user.setResetAttempt(0);

    if (this.birthDateValidation(userDTO.getBirthdate())) {
      throw new FieldValidationException(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, "Birthdate can not be future date", "bithdateFailed");
    }

    if (userDTO.getLangKey() == null) {
      user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
    } else {
      user.setLangKey(userDTO.getLangKey());
    }
    if (userDTO.getAuthorities() != null) {

      Set<Authority> authorities = userDTO.getAuthorities().stream()
          .map(authorityRepository::findOne)
          .collect(Collectors.toSet());
      user.setAuthorities(authorities);

      /**
       * New User Role as Admin
       */
      if (SecurityUtils.isAdmin() && AuthoritiesConstants.ADMIN.equals(authorities.stream().findFirst().get().getName())) {
        loginRole = AuthoritiesConstants.ADMIN;
        adminInfo = new AdminInfo(userDTO);
        Optional<User> userWithAuthorities = this.getUserWithAuthorities();
        Long loginId = 0L;
        if (userWithAuthorities.isPresent()) {
          loginId = userWithAuthorities.get().getId();
        }
        user.setParentId(loginId);
        user.setCreatedBy(String.valueOf(loginId));
      }
    }
    String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
    user.setPassword(encryptedPassword);
    user.setResetKey(RandomUtil.generateResetKey());
    userDTO.setResetKey(user.getResetKey());
    user.setActivated(true);
    userRepository.save(user);
    //userSearchRepository.save(user);

    if (loginRole != null && loginRole.equals(AuthoritiesConstants.ADMIN)) {
      adminInfo.setUserId(user.getId());
      adminInfoRepository.save(adminInfo);
      //adminInfoSearchRepository.save(adminInfo);
    }
    userDTO.setId(user.getId());
    log.debug("Created Information for User: {}", user);
    return userDTO;
  }

  public Optional<AdminInfoDTO> updateUser(AdminInfoDTO userDTO) {
    return Optional.of(userRepository
        .findOne(userDTO.getId()))
        .map((User user) -> {
          AdminInfo adminInfoEntity = null;
          Long adminInfoId = null;
          user.setLogin(userDTO.getLogin());
          user.setFirstName(userDTO.getFirstName());
          user.setLastName(userDTO.getLastName());
          user.setEmail(userDTO.getEmail());
          user.setActivated(userDTO.isActivated());
          user.setLangKey(userDTO.getLangKey());

          if (AuthoritiesConstants.ADMIN.equals(user.getAuthorities().stream().findFirst().get().getName())) {
            Optional<AdminInfo> adminInfoOptional = adminInfoRepository.findOneByUserId(userDTO.getId());
            if (adminInfoOptional.isPresent()) {
              adminInfoEntity = adminInfoOptional.get();
              adminInfoId = adminInfoEntity.getId();
            }

            adminInfoEntity = new AdminInfo(userDTO);
            adminInfoEntity.setId(adminInfoId);
            adminInfoEntity.setUserId(userDTO.getId());

            /**
             * Update Child Table
             */
            adminInfoRepository.save(adminInfoEntity);
            //adminInfoSearchRepository.save(adminInfoEntity);
          }

          /**
           * Update Parent Table
           */
          userRepository.save(user);
          //userSearchRepository.save(user);
          log.debug("Changed Information for User: {}", user);
          return userDTO;
        });
  }

  public void deleteUser(String login) {
    Optional<User> user = userRepository.findOneByLogin(login);
    user.orElseThrow(() -> new InternalServerErrorException("No user was found for this id"));
    user.ifPresent(userEntity ->
    {
      if (AuthoritiesConstants.ADMIN.equals(userEntity.getAuthorities().stream().findFirst().get().getName())) {
        /**
         * Delete Child Entry
         */
        adminInfoRepository.deleteByUserId(userEntity.getId());
        //adminInfoSearchRepository.deleteByUserId(userEntity.getId());
      }
      /**
       * Delete Parent Entry
       */
      userRepository.delete(userEntity);
      //userSearchRepository.delete(userEntity);
      log.debug("Deleted User: {}", userEntity);
    });
  }

  @Transactional(readOnly = true)
  public Page<User> getAllManagedUsers(Pageable pageable) {
    return userRepository.findAllByAuthoritiesNameAndLoginNot(pageable, AuthoritiesConstants.ADMIN, SecurityUtils.getCurrentUserLogin().get());
  }

  /**
   * Schedule Cron Every 5 Mins
   */
  @Scheduled(cron = "0 0/5 * * * ?")
  public void setInactiveToActiveUser() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, -1);
    log.debug("Active use before Date : {}", calendar.getTime());

    List<User> users = userRepository.findAllByActivatedIsFalseAndResetDateBefore(calendar.getTime());
    log.debug("users => {}", users);

    if (users != null && !users.isEmpty()) {
      for (User user : users) {
        user.setActivated(true);
        user.setResetAttempt(0);
        user.setResetDate(null);
        user.setMobileKey(null);
        userRepository.save(user);
        log.debug("Updated users => {}", users);
      }
    }


  }

  @Transactional(readOnly = true)
  public Optional<User> getUserWithAuthoritiesByLogin(String login) {
    return userRepository.findOneWithAuthoritiesByLogin(login);
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserWithAuthorities() {
    return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
  }

  public boolean birthDateValidation(Date birthDay) throws ParseException {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = dateFormat.parse(dateFormat.format(new Date()));
    return birthDay.compareTo(today) >= 0;
  }
}
