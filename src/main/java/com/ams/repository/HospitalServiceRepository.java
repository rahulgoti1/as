package com.ams.repository;

import com.ams.domain.HospitalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the AdminInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HospitalServiceRepository extends JpaRepository<HospitalService, Long> {

}
