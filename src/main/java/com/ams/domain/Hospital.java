package com.ams.domain;

import com.ams.service.dto.HospitalDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * Created by grahul on 09-05-2018.
 */

@Entity
@Table(name = "hospital")
@Document(indexName = "hospital")
public class Hospital {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @NotNull
  @Column(name = "postal_code")
  private String postalCode;

  @NotBlank
  @NotNull
  private String city;

  @NotBlank
  @NotNull
  private String state;

  @NotBlank
  @NotNull
  private String country;

  @NotBlank
  @NotNull
  private String address;

  @NotBlank
  @NotNull
  private String street;

  @NotBlank
  @NotNull
  @Column(name = "contact_no")
  private String contactNo;

  private String information;

  private String website;

  private String localName;

  @Column(name = "from_at")
  @Temporal(TemporalType.TIME)
  private Date fromAt;

  @Column(name = "to_at")
  @Temporal(TemporalType.TIME)
  private Date endAt;

  @Column(name = "user_id")
  private Long userId;

  @JsonIgnore
  @ManyToMany
  @JoinTable(
      name = "hospital_service_mapping",
      joinColumns = {@JoinColumn(name = "hospital_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "service_id", referencedColumnName = "id")})
  @JsonIgnoreProperties("hospital")
  @JsonManagedReference
  private Set<HospitalService> hospitalServices = new HashSet<>();

  @OneToMany(mappedBy = "hospital", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnoreProperties("hospital")
  @JsonManagedReference
  private List<HospitalDocument> hospitalDocuments = new ArrayList<>();


  @JsonIgnore
  @ManyToMany
  @JoinTable(
      name = "hospital_specialities_mapping",
      joinColumns = {@JoinColumn(name = "hospital_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "speciality_id", referencedColumnName = "id")})
  private Set<HospitalSpecialities> hospitalSpecialities = new HashSet<>();

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

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getContactNo() {
    return contactNo;
  }

  public void setContactNo(String contactNo) {
    this.contactNo = contactNo;
  }

  public String getInformation() {
    return information;
  }

  public void setInformation(String information) {
    this.information = information;
  }

  public Date getFromAt() {
    return fromAt;
  }

  public void setFromAt(Date fromAt) {
    this.fromAt = fromAt;
  }

  public Date getEndAt() {
    return endAt;
  }

  public void setEndAt(Date endAt) {
    this.endAt = endAt;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void addHospitalServices(HospitalService hospitalService) {
    this.hospitalServices.add(hospitalService);
//    hospitalService.setHospital(this);
  }

  public List<HospitalDocument> getHospitalDocuments() {
    return hospitalDocuments;
  }

  public void setHospitalDocuments(List<HospitalDocument> hospitalDocuments) {
    this.hospitalDocuments = hospitalDocuments;
  }

  public void addHospitalDocuments(HospitalDocument hospitalDocument) {
    this.hospitalDocuments.add(hospitalDocument);
    hospitalDocument.setHospital(this);
  }

  public Set<HospitalService> getHospitalServices() {
    return hospitalServices;
  }

  public void setHospitalServices(Set<HospitalService> hospitalServices) {
    this.hospitalServices = hospitalServices;
  }

  public Set<HospitalSpecialities> getHospitalSpecialities() {
    return hospitalSpecialities;
  }

  public void setHospitalSpecialities(Set<HospitalSpecialities> hospitalSpecialities) {
    this.hospitalSpecialities = hospitalSpecialities;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getLocalName() {
    return localName;
  }

  public void setLocalName(String localName) {
    this.localName = localName;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }


  public void addHospitalSpecialities(HospitalSpecialities hospitalSpecialities) {
    this.hospitalSpecialities.add(hospitalSpecialities);
//    hospitalSpecialities.setHospital(this);
  }

  public Hospital(HospitalDTO hospitalDTO) {
    this.name = hospitalDTO.getName();
    this.postalCode = hospitalDTO.getPostalCode();
    this.city = hospitalDTO.getCity();
    this.state = hospitalDTO.getState();
    this.country = hospitalDTO.getCountry();
    this.address = hospitalDTO.getAddress();
    this.contactNo = hospitalDTO.getContactNo();
    this.information = hospitalDTO.getInformation();
    this.website = hospitalDTO.getWebsite();
    this.localName = hospitalDTO.getLocalName();
    this.street = hospitalDTO.getStreet();
  }

  public Hospital() {
  }

  @Override
  public String toString() {
    return "Hospital{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", postalCode='" + postalCode + '\'' +
        ", city='" + city + '\'' +
        ", state='" + state + '\'' +
        ", country='" + country + '\'' +
        ", address='" + address + '\'' +
        ", contactNo='" + contactNo + '\'' +
        ", information='" + information + '\'' +
        ", website='" + website + '\'' +
        ", locaName='" + localName + '\'' +
        ", fromAt=" + fromAt +
        ", endAt=" + endAt +
        ", userId=" + userId +
        '}';
  }
}
