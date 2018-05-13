package com.ams.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * Created by grahul on 09-05-2018.
 */
@Entity
@Table(name = "hospital_document")
@Document(indexName = "hospital_document")
public class HospitalDocument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String path;

  private String originalName;

  @Column(name = "content_type")
  private String contentType;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Hospital getHospital() {
    return hospital;
  }

  public void setHospital(Hospital hospital) {
    this.hospital = hospital;
  }

  public String getOriginalName() {
    return originalName;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  @ManyToOne
  @JoinColumn(name = "hospital_id", referencedColumnName = "id")
  @JsonBackReference
  private Hospital hospital;

  @Override
  public String toString() {
    return "HospitalDocument{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", path='" + path + '\'' +
        ", originalName='" + originalName + '\'' +
        ", contentType='" + contentType + '\'' +
        '}';
  }
}
