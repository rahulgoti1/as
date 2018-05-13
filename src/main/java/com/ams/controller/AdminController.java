package com.ams.controller;

import com.ams.config.Constants;
import com.ams.controller.util.HeaderUtil;
import com.ams.controller.util.PaginationUtil;
import com.ams.domain.User;
import com.ams.exception.BadRequestAlertException;
import com.ams.exception.EmailAlreadyUsedException;
import com.ams.exception.ErrorConstants;
import com.ams.exception.FieldValidationException;
import com.ams.exception.InternalServerErrorException;
import com.ams.exception.LoginAlreadyUsedException;
import com.ams.exception.MobileAlreadyUsedException;
import com.ams.repository.UserRepository;
import com.ams.search.UserSearchRepository;
import com.ams.security.AuthoritiesConstants;
import com.ams.service.AdminService;
import com.ams.service.MailService;
import com.ams.service.dto.AdminInfoDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final Logger log = LoggerFactory.getLogger(AdminController.class);

  private final UserRepository userRepository;

  private final AdminService userService;

  private final MailService mailService;

  private final UserSearchRepository userSearchRepository;

  public AdminController(UserRepository userRepository, AdminService userService, MailService mailService, UserSearchRepository userSearchRepository) {

    this.userRepository = userRepository;
    this.userService = userService;
    this.mailService = mailService;
    this.userSearchRepository = userSearchRepository;
  }

  @PostMapping("/users")
  @Secured(AuthoritiesConstants.ADMIN)
  public ResponseEntity<AdminInfoDTO> createUser(@Valid @RequestBody AdminInfoDTO userDTO) throws URISyntaxException, ParseException {
    log.debug("REST request to save User : {}", userDTO);

    if (userDTO.getId() != null) {
      throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
      // Lowercase the user login before comparing with database
    } else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
      throw new LoginAlreadyUsedException();
    } else if (!StringUtils.isEmpty(userDTO.getMobile()) && userRepository.findOneByMobile(userDTO.getMobile()).isPresent()) {
      throw new MobileAlreadyUsedException();
    } else if (!StringUtils.isEmpty(userDTO.getEmail()) && userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
      throw new EmailAlreadyUsedException();
    } else {
      AdminInfoDTO newUser = userService.createUser(userDTO);

      /**
       * Account Creation Mail
       */
      User user = new User();
      user.setLogin(newUser.getLogin());
      user.setEmail(newUser.getEmail());
      user.setResetKey(newUser.getResetKey());
      user.setLangKey(newUser.getLangKey());
      mailService.sendCreationEmail(user);
      return ResponseEntity.created(new URI("/api/admin/users/" + newUser.getLogin()))
          .headers(HeaderUtil.createAlert("userManagement.created", newUser.getLogin()))
          .body(newUser);
    }
  }


  @PutMapping("/users")
  @Secured(AuthoritiesConstants.ADMIN)
  public ResponseEntity<AdminInfoDTO> updateUser(@Valid @RequestBody AdminInfoDTO userDTO) throws ParseException {
    log.debug("REST request to update User : {}", userDTO);

    Optional<User> existingUserOptional = userRepository.findOneById(userDTO.getId());

    if (!existingUserOptional.isPresent()) {
      throw new InternalServerErrorException("No user was found for this id");
    }

    Optional<User> existingMobileUser = userRepository.findOneByMobile(userDTO.getMobile());
    if (existingMobileUser.isPresent() && (!existingMobileUser.get().getId().equals(userDTO.getId()))) {
      throw new MobileAlreadyUsedException();
    }
    Optional<User> existingUser;
    if (userDTO.getEmail() != null && !userDTO.getEmail().equals("")) {
      existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
      if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
        throw new EmailAlreadyUsedException();
      }
    }
    existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
    if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
      throw new LoginAlreadyUsedException();
    }
    if (userService.birthDateValidation(userDTO.getBirthdate())) {
      throw new FieldValidationException(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, "Birthdate can not be future date", "bithdateFailed");
    }
    Optional<AdminInfoDTO> updatedUser = userService.updateUser(userDTO);

    return ResponseEntity.ok().headers(HeaderUtil.createAlert("userManagement.updated", userDTO.getLogin()))
        .body(updatedUser.get());
  }

  @GetMapping("/users")
  @Secured(AuthoritiesConstants.ADMIN)
  public ResponseEntity<List<User>> getAllUsers(Pageable pageable) {
    final Page<User> page = userService.getAllManagedUsers(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/admin/users");
    return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
  }

  @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
  @Secured(AuthoritiesConstants.ADMIN)
  public ResponseEntity<User> getUser(@PathVariable String login) {
    log.debug("REST request to get User : {}", login);
    Optional<User> user = userService.getUserWithAuthoritiesByLogin(login);
    if (user.isPresent()) {
      return ResponseEntity.ok().body(user.get());
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
  @Secured(AuthoritiesConstants.ADMIN)
  public ResponseEntity<Void> deleteUser(@PathVariable String login) {
    log.debug("REST request to delete User: {}", login);
    userService.deleteUser(login);
    return ResponseEntity.ok().headers(HeaderUtil.createAlert("userManagement.deleted", login)).build();
  }

  @PostMapping("/cron")
  @Secured(AuthoritiesConstants.ADMIN)
  public void setInactiveToActiveUser() {
    userService.setInactiveToActiveUser();
  }

}
