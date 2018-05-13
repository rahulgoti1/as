package com.ams.repository;

import com.ams.domain.Hospital;
import com.ams.domain.HospitalSpecialities;
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
public interface HospitalSpecialitiesRepository extends JpaRepository<HospitalSpecialities, Long> {

}
