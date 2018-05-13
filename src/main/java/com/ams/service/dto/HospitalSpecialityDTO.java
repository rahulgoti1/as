package com.ams.service.dto;

import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * Created by grahul on 09-05-2018.
 */
public class HospitalSpecialityDTO {

  @NotNull
  private List<Long> hospitalSpecialities;

  public List<Long> getHospitalSpecialities() {
    return hospitalSpecialities;
  }

  public void setHospitalSpecialities(List<Long> hospitalSpecialities) {
    this.hospitalSpecialities = hospitalSpecialities;
  }
}
