package com.ams.search;

import com.ams.domain.AdminInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the AdminInfo entity.
 */
public interface AdminInfoSearchRepository extends ElasticsearchRepository<AdminInfo, Long> {

  Long deleteByUserId(Long userId);

}
