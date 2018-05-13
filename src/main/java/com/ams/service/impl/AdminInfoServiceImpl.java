package com.ams.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ams.domain.AdminInfo;
import com.ams.repository.AdminInfoRepository;
import com.ams.search.AdminInfoSearchRepository;
import com.ams.service.AdminInfoService;
import com.ams.service.dto.AdminInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing AdminInfo.
 */
@Service
@Transactional
public class AdminInfoServiceImpl implements AdminInfoService {

  private final Logger log = LoggerFactory.getLogger(AdminInfoServiceImpl.class);

  private final AdminInfoRepository adminInfoRepository;

  private final AdminInfoSearchRepository adminInfoSearchRepository;

  public AdminInfoServiceImpl(AdminInfoRepository adminInfoRepository, AdminInfoSearchRepository adminInfoSearchRepository) {
    this.adminInfoRepository = adminInfoRepository;
    this.adminInfoSearchRepository = adminInfoSearchRepository;
  }

  /**
   * Save a adminInfo.
   *
   * @param adminInfoDTO the entity to save
   * @return the persisted entity
   */
  @Override
  public AdminInfoDTO save(AdminInfoDTO adminInfoDTO) {
    log.debug("Request to save AdminInfo : {}", adminInfoDTO);
    AdminInfo adminInfo = new AdminInfo(adminInfoDTO);
    adminInfo = adminInfoRepository.save(adminInfo);
    AdminInfoDTO result = new AdminInfoDTO(adminInfo);
    adminInfoSearchRepository.save(adminInfo);
    return result;
  }

  /**
   * Get all the adminInfos.
   *
   * @param pageable the pagination information
   * @return the list of entities
   */
  @Override
  @Transactional(readOnly = true)
  public Page<AdminInfoDTO> findAll(Pageable pageable) {
    log.debug("Request to get all AdminInfos");
    return adminInfoRepository.findAll(pageable)
        .map(AdminInfoDTO::new);
  }

  /**
   * Get one adminInfo by id.
   *
   * @param id the id of the entity
   * @return the entity
   */
  @Override
  @Transactional(readOnly = true)
  public AdminInfoDTO findOne(Long id) {
    log.debug("Request to get AdminInfo : {}", id);
    AdminInfo adminInfo = adminInfoRepository.findOne(id);
    return new AdminInfoDTO(adminInfo);
  }

  /**
   * Delete the adminInfo by id.
   *
   * @param id the id of the entity
   */
  @Override
  public void delete(Long id) {
    log.debug("Request to delete AdminInfo : {}", id);
    adminInfoRepository.delete(id);
    adminInfoSearchRepository.delete(id);
  }

  /**
   * Search for the adminInfo corresponding to the query.
   *
   * @param query    the query of the search
   * @param pageable the pagination information
   * @return the list of entities
   */
  @Override
  @Transactional(readOnly = true)
  public Page<AdminInfoDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of AdminInfos for query {}", query);
    Page<AdminInfo> result = adminInfoSearchRepository.search(queryStringQuery(query), pageable);
    return result.map(AdminInfoDTO::new);
  }
}
