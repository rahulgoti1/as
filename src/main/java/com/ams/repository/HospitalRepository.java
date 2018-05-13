package com.ams.repository;

import com.ams.domain.Hospital;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the AdminInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

  Page<Hospital> findAllByUserId(Pageable pageable, Long userId);

  Optional<Hospital> findOneById(Long id);

}
