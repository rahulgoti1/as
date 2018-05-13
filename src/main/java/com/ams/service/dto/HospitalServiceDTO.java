package com.ams.service.dto;

import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * Created by grahul on 09-05-2018.
 */
public class HospitalServiceDTO {

  @NotNull
  private List<Long> hospitalServices;

  public List<Long> getHospitalServices() {
    return hospitalServices;
  }

  public void setHospitalServices(List<Long> hospitalServices) {
    this.hospitalServices = hospitalServices;
  }
}
