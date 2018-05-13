package com.ams.service;

import com.ams.service.dto.AdminInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing AdminInfo.
 */
public interface AdminInfoService {

  AdminInfoDTO save(AdminInfoDTO adminInfoDTO);

  Page<AdminInfoDTO> findAll(Pageable pageable);

  AdminInfoDTO findOne(Long id);

  void delete(Long id);

  Page<AdminInfoDTO> search(String query, Pageable pageable);
}
