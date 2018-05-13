package com.ams.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * Created by grahul on 09-05-2018.
 */
@Entity
@Table(name = "hospital_specialities")
@Document(indexName = "hospital_specialities")
public class HospitalSpecialities {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "speciality_name")
  private String specialityName;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSpecialityName() {
    return specialityName;
  }

  public void setSpecialityName(String specialityName) {
    this.specialityName = specialityName;
  }

  public HospitalSpecialities addSpecialityName(String specialityName) {
    this.specialityName = specialityName;
    return this;
  }

  @Override
  public String toString() {
    return "HospitalSpecialities{" +
        "id=" + id +
        ", specialityName='" + specialityName + '\'' +
        '}';
  }
}
