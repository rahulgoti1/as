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
@Table(name = "hospital_service")
@Document(indexName = "hospital_service")
public class HospitalService {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "service_name")
  private String serviceName;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public HospitalService addServiceName(String serviceName) {
    this.serviceName = serviceName;
    return this;
  }

  @Override
  public String toString() {
    return "HospitalService{" +
        "id=" + id +
        ", serviceName='" + serviceName + '\'' +
        '}';
  }
}
