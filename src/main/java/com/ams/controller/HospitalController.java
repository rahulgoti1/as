package com.ams.controller;

import com.ams.controller.util.HeaderUtil;
import com.ams.controller.util.PaginationUtil;
import com.ams.domain.Hospital;
import com.ams.exception.InternalServerErrorException;
import com.ams.security.AuthoritiesConstants;
import com.ams.service.HospitalService;
import com.ams.service.dto.HospitalDTO;
import com.ams.service.dto.HospitalServiceDTO;
import com.ams.service.dto.HospitalSpecialityDTO;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

  private final Logger log = LoggerFactory.getLogger(HospitalController.class);

  private final HospitalService hospitalService;

  public HospitalController(HospitalService hospitalService) {
    this.hospitalService = hospitalService;
  }

  @PostMapping()
  @Secured(AuthoritiesConstants.ADMIN)
  public ResponseEntity<Hospital> createHospital(@Valid @RequestBody HospitalDTO hospitalDTO) throws URISyntaxException, IOException, ParseException {
    log.debug("REST request to save Hospital : {}", hospitalDTO);
    Hospital hospital = hospitalService.createHospitalInfo(hospitalDTO, true);
    return ResponseEntity.created(new URI("/api/hospital/" + String.valueOf(hospital.getId())))
        .headers(HeaderUtil.createAlert("hospitalManagement.created", String.valueOf(hospital.getId())))
        .body(hospital);
  }

  @GetMapping()
  public ResponseEntity<List<Hospital>> getAllHospitals(Pageable pageable) {
    log.debug("REST request to get Hospitals : {}");
    final Page<Hospital> hospitals = hospitalService.getAllHospitals(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(hospitals, "/api/hospital");
    return new ResponseEntity<>(hospitals.getContent(), headers, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Hospital> getHospitalById(@PathVariable Long id) {
    log.debug("REST request to get Hospital by Id : {}", id);
    final Optional<Hospital> hospital = hospitalService.getHospitalById(id);
    if (hospital.isPresent()) {
      return ResponseEntity.ok().body(hospital.get());
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @GetMapping("/me")
  public ResponseEntity<List<Hospital>> getHospital(Pageable pageable) {
    log.debug("REST request to get Hospital created by Logged user:");
    final Page<Hospital> page = hospitalService.getAllHospitalsByLogin(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/hospital");
    return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
  }

  @GetMapping("/files/{path:.+}/{filename:.+}")
  public ResponseEntity<Resource> getFile(@PathVariable String path, @PathVariable String filename) {
    log.debug("REST request to get file of Hospital: Folder {}, File {}", path, filename);
    Resource file = hospitalService.loadFile(path, filename);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }

  @GetMapping("/files/{id}")
  public ResponseEntity<List<String>> getAllFile(@PathVariable Long id) {
    log.debug("REST request to get files of Hospital: {}", id);
    List<String> allFile = hospitalService.loadAllFile(id);
    if (allFile == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok().body(allFile);
  }

  @DeleteMapping("/{id}")
  @Secured(AuthoritiesConstants.ADMIN)
  public ResponseEntity<Void> deleteHospitalById(@PathVariable Long id) {
    log.debug("REST request to delete Hospital: {}", id);
    hospitalService.deleteHospital(id);
    return ResponseEntity.ok().headers(HeaderUtil.createAlert("hospitalManagement.deleted", String.valueOf(id))).build();
  }

  @PutMapping()
  @Secured(AuthoritiesConstants.ADMIN)
  public ResponseEntity<Hospital> updateHospitalById(@Valid @RequestBody HospitalDTO hospitalDTO) throws IOException, ParseException {
    log.debug("REST request to update Hospital: {}", hospitalDTO.getId());
    Optional<Hospital> hospitalById = hospitalService.getHospitalById(hospitalDTO.getId());

    if (!hospitalById.isPresent()) {
      throw new InternalServerErrorException("No hospital was found for this id");
    }
    Hospital hospital = hospitalService.updateHospital(hospitalDTO);
    return ResponseEntity.ok().headers(HeaderUtil.createAlert("hospitalManagement.updated", String.valueOf(hospital.getId())))
        .body(hospital);
  }

  @GetMapping("/_search/{query}")
  public List<Hospital> search(@PathVariable String query) {
    return hospitalService.search(query);
  }


  @PostMapping("services/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  public void saveService(@PathVariable Long id, @Valid @RequestBody HospitalServiceDTO hospitalServiceDTO) {
    log.debug("REST request to Save Hospital Service by Id : {}", id);
    hospitalService.saveService(id, hospitalServiceDTO);
  }

  @PostMapping("specialities/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  public void saveSpecialities(@PathVariable Long id, @Valid @RequestBody HospitalSpecialityDTO hospitalSpecialityDTO) {
    log.debug("REST request to Save Hospital Specialities by Id : {}", id);
    hospitalService.saveSpecialities(id, hospitalSpecialityDTO);
  }

  @PostMapping("documents/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  public void saveSpecialities(@PathVariable Long id, @RequestParam("files") MultipartFile[] files) throws IOException {
    log.debug("REST request to Save Hospital Documents by Id : {}", id);
    hospitalService.saveDocuments(id, files);
  }

}
