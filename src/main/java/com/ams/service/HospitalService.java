package com.ams.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ams.controller.HospitalController;
import com.ams.domain.Hospital;
import com.ams.domain.HospitalDocument;
import com.ams.domain.HospitalSpecialities;
import com.ams.exception.ErrorConstants;
import com.ams.exception.FieldValidationException;
import com.ams.exception.InternalServerErrorException;
import com.ams.exception.InvalidFileUploadException;
import com.ams.repository.HospitalRepository;
import com.ams.repository.HospitalServiceRepository;
import com.ams.repository.HospitalSpecialitiesRepository;
import com.ams.search.HospitalSearchRepository;
import com.ams.service.dto.HospitalDTO;
import com.ams.service.dto.HospitalServiceDTO;
import com.ams.service.dto.HospitalSpecialityDTO;
import com.ams.service.util.RandomUtil;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class HospitalService {

  private final Logger log = LoggerFactory.getLogger(HospitalService.class);

  private HospitalRepository hospitalRepository;
  private HospitalSearchRepository hospitalSearchRepository;
  private StorageService storageService;
  private UserJWTService userJWTService;
  private HospitalServiceRepository hospitalServiceRepository;
  private HospitalSpecialitiesRepository hospitalSpecialitiesRepository;

  public HospitalService(HospitalRepository hospitalRepository, HospitalSearchRepository hospitalSearchRepository, StorageService storageService, UserJWTService userJWTService, HospitalServiceRepository hospitalServiceRepository, HospitalSpecialitiesRepository hospitalSpecialitiesRepository) {
    this.hospitalRepository = hospitalRepository;
    this.hospitalSearchRepository = hospitalSearchRepository;
    this.storageService = storageService;
    this.userJWTService = userJWTService;
    this.hospitalServiceRepository = hospitalServiceRepository;
    this.hospitalSpecialitiesRepository = hospitalSpecialitiesRepository;
  }

  public Hospital createHospital(HospitalDTO hospitalDTO, boolean isNew) throws IOException {

    Hospital hospital = new Hospital(hospitalDTO);
    if (!isNew) {
      hospital.setId(hospitalDTO.getId());
    }

    List<HospitalDocument> hospitalDocuments = new ArrayList<>();
    HospitalDocument hospitalDocument;
    int count = 1;

    /**
     * Set Hospital Specialities
     */
//    if (hospitalDTO.getHospitalSpecialities() != null && hospitalDTO.getHospitalSpecialities().length > 0) {
//      for (String s : Arrays.asList(hospitalDTO.getHospitalSpecialities())) {
//        if ("".equals(s.trim())) {
//          throw new FieldValidationException(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, "Hospital Speciality can not be blank ", "specialitiesFailed");
//        }
//        hospital.addHospitalSpecialities(new com.ams.domain.HospitalSpecialities().addSpecialityName(s));
//      }
//    }

    /**
     * Set Hospital Service
     */
//    if (hospitalDTO.getHospitalService() != null && hospitalDTO.getHospitalService().length > 0) {
//      for (String s : Arrays.asList(hospitalDTO.getHospitalService())) {
//        if ("".equals(s.trim())) {
//          throw new FieldValidationException(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, "Hospital Service can not be null or blank ", "serviceFailed");
//        }
//        hospital.addHospitalServices(new com.ams.domain.HospitalService().addServiceName(s));
//      }
//    } else {
//      throw new FieldValidationException(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, "Hospital Service can not be null or blank ", "serviceFailed");
//    }

    /**
     * Validate Uploaded Files
     */
//    if (hospitalDTO.getFiles() != null && hospitalDTO.getFiles().length > 0) {
//      List<MultipartFile> multipartFiles = Arrays.asList(hospitalDTO.getFiles());
//      if (!storageService.validateFiles(multipartFiles)) {
//        throw new InvalidFileUploadException();
//      }
//    } else {
//      throw new FieldValidationException(ErrorConstants.INVALID_FILE_UPLOAD, "Hospital image can not be null ", "imageFailed");
//    }

//    if (hospitalDTO.getFiles() != null && hospitalDTO.getFiles().length > 0) {
//      List<MultipartFile> multipartFiles = Arrays.asList(hospitalDTO.getFiles());
//
//      /**
//       * Path :- First Five Character of HospitalName With Random String
//       */
//      String firstFiveHospitalName = hospitalDTO.getName().substring(0, Math.min(hospitalDTO.getName().length(), 5));
//      String fileRootPath = firstFiveHospitalName.concat("_" + RandomUtil.generateKeyWithSize(5));
//
//      for (MultipartFile file : multipartFiles) {
//        hospitalDocument = new HospitalDocument();
//        hospitalDocument.setContentType(file.getContentType());
//        hospitalDocument.setOriginalName(file.getOriginalFilename());
//
//        /**
//         * Image Name :- First Five Character of HospitalName With _Index
//         */
//        String[] fileFrags = file.getOriginalFilename().split("\\.");
//        hospitalDocument.setName(firstFiveHospitalName.concat("_").concat("" + count++).concat(".").concat(fileFrags[fileFrags.length - 1]));
//        hospitalDocument.setPath(fileRootPath);
//        hospital.addHospitalDocuments(hospitalDocument);
//        hospitalDocuments.add(hospitalDocument);
//      }
//    }

    /**
     * Persist Hospital Object
     */
    hospital.setUserId(userJWTService.getLoginId());
    hospital = hospitalRepository.save(hospital);
    hospitalSearchRepository.save(hospital);
    hospitalDTO.setId(hospital.getId());

    /**
     * Store Images
     */
//    if (hospitalDocuments != null && hospitalDocuments.size() > 0) {
//      List<MultipartFile> multipartFiles = Arrays.asList(hospitalDTO.getFiles());
//      count = 0;
//      for (HospitalDocument doc : hospitalDocuments) {
//        storageService.saveUploadedFile(multipartFiles.get(count++), doc.getPath(), doc.getName());
//      }
//    }

    return hospital;
  }

  @Transactional(readOnly = true)
  public Page<Hospital> getAllHospitalsByLogin(Pageable pageable) {
    return hospitalRepository.findAllByUserId(pageable, userJWTService.getLoginId());
  }

  @Transactional(readOnly = true)
  public Page<Hospital> getAllHospitals(Pageable pageable) {
    return hospitalRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public Optional<Hospital> getHospitalById(Long id) {
    return hospitalRepository.findOneById(id);
  }

  public Resource loadFile(String path, String filename) {
    return storageService.loadFile(path, filename);
  }

  public List<String> loadAllFile(Long id) {
    Optional<Hospital> hospitals = hospitalRepository.findOneById(id);
    if (hospitals.isPresent()) {
      List<HospitalDocument> hospitalDocuments = hospitals.get().getHospitalDocuments();
      return hospitalDocuments.stream().map(hospitalDocument ->
          MvcUriComponentsBuilder.fromMethodName(HospitalController.class, "getFile", hospitalDocument.getPath(), hospitalDocument.getName()).build().toString())
          .collect(Collectors.toList());
    } else {
      return null;
    }
  }

  public void deleteHospital(Long id) {
    hospitalRepository.delete(id);
  }

  public Hospital updateHospital(HospitalDTO hospitalDTO) throws IOException, ParseException {
    return this.createHospitalInfo(hospitalDTO, false);
  }

  public List<Hospital> search(String query) {
    return StreamSupport
        .stream(hospitalSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
  }

  public HospitalServiceDTO saveService(Long hospitalId, HospitalServiceDTO hospitalServiceDTO) {
    Optional<Hospital> hospitalOptional = hospitalRepository.findOneById(hospitalId);

    if (!hospitalOptional.isPresent()) {
      throw new InternalServerErrorException("Hospital not found for given id");
    }
    hospitalOptional.get().getHospitalServices().clear();

    Set<com.ams.domain.HospitalService> hospitalServices = hospitalServiceDTO.getHospitalServices().stream()
        .map(hospitalServiceRepository::findOne)
        .collect(Collectors.toSet());
    if (hospitalServices == null || hospitalServices.contains(null)) {
      throw new FieldValidationException(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, "Invalid Hospital Service provided", "serviceFailed");
    }
    hospitalOptional.get().setHospitalServices(hospitalServices);
    return hospitalServiceDTO;
  }

  public HospitalSpecialityDTO saveSpecialities(Long hospitalId, HospitalSpecialityDTO hospitalSpecialityDTO) {
    Optional<Hospital> hospitalOptional = hospitalRepository.findOneById(hospitalId);

    if (!hospitalOptional.isPresent()) {
      throw new InternalServerErrorException("Hospital not found for given id");
    }
    hospitalOptional.get().getHospitalSpecialities().clear();
    Set<HospitalSpecialities> hospitalSpecialities = hospitalSpecialityDTO.getHospitalSpecialities().stream()
        .map(hospitalSpecialitiesRepository::findOne)
        .collect(Collectors.toSet());

    if (hospitalSpecialities == null || hospitalSpecialities.contains(null)) {
      throw new FieldValidationException(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, "Invalid Hospital Speciality provided ", "specialityFailed");
    }
    hospitalOptional.get().setHospitalSpecialities(hospitalSpecialities);
    return hospitalSpecialityDTO;
  }

  public void saveDocuments(Long hospitalId, MultipartFile[] files) throws IOException {

    Optional<Hospital> hospitalOptional = hospitalRepository.findOneById(hospitalId);
    HospitalDocument hospitalDocument;
    int count = 1;

    if (!hospitalOptional.isPresent()) {
      throw new InternalServerErrorException("Hospital not found for given id");
    }

    /**
     * Validate Uploaded Files
     */
    if (files != null && files.length > 0) {
      List<MultipartFile> multipartFiles = Arrays.asList(files);
      if (!storageService.validateFiles(multipartFiles)) {
        throw new InvalidFileUploadException();
      }
    } else {
      throw new FieldValidationException(ErrorConstants.INVALID_FILE_UPLOAD, "Hospital image can not be null ", "imageFailed");
    }

    hospitalOptional.get().getHospitalDocuments().clear();
    if (files != null && files.length > 0) {
      List<MultipartFile> multipartFiles = Arrays.asList(files);

      /**
       * Path :- First Five Character of HospitalName With Random String
       */
      String firstFiveHospitalName = hospitalOptional.get().getName().substring(0, Math.min(hospitalOptional.get().getName().length(), 5));
      String fileRootPath = firstFiveHospitalName.concat("_" + RandomUtil.generateKeyWithSize(5));

      for (MultipartFile file : multipartFiles) {
        hospitalDocument = new HospitalDocument();
        hospitalDocument.setContentType(file.getContentType());
        hospitalDocument.setOriginalName(file.getOriginalFilename());

        /**
         * Image Name :- First Five Character of HospitalName With _Index
         */
        String[] fileFrags = file.getOriginalFilename().split("\\.");
        hospitalDocument.setName(firstFiveHospitalName.concat("_").concat("" + count++).concat(".").concat(fileFrags[fileFrags.length - 1]));
        hospitalDocument.setPath(fileRootPath);
        hospitalOptional.get().addHospitalDocuments(hospitalDocument);

        /**
         * Store Images
         */
        storageService.saveUploadedFile(file, hospitalDocument.getPath(), hospitalDocument.getName());
      }
    }
  }

  public Hospital createHospitalInfo(HospitalDTO hospitalDTO, boolean isNew) throws IOException, ParseException {

    Hospital hospital = new Hospital(hospitalDTO);
    DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    Date fromAt = formatter.parse(hospitalDTO.getFromAt());
    Date endAt = formatter.parse(hospitalDTO.getEndAt());

    hospital.setFromAt(fromAt);
    hospital.setEndAt(endAt);

    if (!isNew) {
      hospital.setId(hospitalDTO.getId());
    }

    /**
     * Persist Hospital Object
     */
    hospital.setUserId(userJWTService.getLoginId());
    hospital = hospitalRepository.save(hospital);
    hospitalSearchRepository.save(hospital);
    hospitalDTO.setId(hospital.getId());

    return hospital;
  }

}
