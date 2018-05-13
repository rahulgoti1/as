package com.ams.search;

import com.ams.domain.Hospital;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the AdminInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HospitalSearchRepository extends ElasticsearchRepository<Hospital, Long> {

  Page<Hospital> findAllByUserId(Pageable pageable, Long userId);

  Optional<Hospital> findOneById(Long id);

}
