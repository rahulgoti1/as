package com.ams.repository;

import com.ams.domain.User;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findOneByActivationKey(String activationKey);

  List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime);

  Optional<User> findOneByResetKey(String resetKey);

  Optional<User> findOneByEmailIgnoreCase(String email);

  Optional<User> findOneByLogin(String login);

  @EntityGraph(attributePaths = "authorities")
  Optional<User> findOneWithAuthoritiesById(Long id);

  @EntityGraph(attributePaths = "authorities")
  Optional<User> findOneWithAuthoritiesByLogin(String login);

  @EntityGraph(attributePaths = "authorities")
  Optional<User> findOneWithAuthoritiesByEmail(String email);

  Page<User> findAllByLoginNot(Pageable pageable, String login);

  Optional<User> findOneByMobile(String mobile);

  Optional<User> findOneById(Long id);

  Page<User> findAllByAuthoritiesNameAndLoginNot(Pageable pageable, String name, String login);

  List<User> findAllByAuthoritiesNameAndLoginNot(String name, String login);

  Optional<User> findOneByMobileAndMobileKey(String mobile, String mobileKey);

  Optional<User> findOneByEmailAndMobileKey(String email, String mobileKey);

  List<User> findAllByActivatedIsFalseAndResetDateBefore(Date date);

  @Modifying(clearAutomatically = true)
  @Query(value = " UPDATE ams.user SET activated = true \n " +
      " WHERE activated = false AND reset_date < DATE_ADD(SYSDATE(), INTERVAL -1 DAY) ", nativeQuery = true)
  int updateInactiveUserByEveryDay();

}
