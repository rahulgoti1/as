package com.ams.service.dto;

import com.ams.config.Constants;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A DTO representing a user, with his authorities.
 */
public class HospitalDTO {

  private final Logger log = LoggerFactory.getLogger(HospitalDTO.class);

  private Long id;

  private String name;

  @NotNull
  @NotBlank
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
  private String contactNo;

  private String information;

  private String website;

  private String localName;

  @NotNull
  @Pattern(regexp = Constants.TIME_REGEX)
  private String fromAt;

  @NotNull
  @Pattern(regexp = Constants.TIME_REGEX)
  private String endAt;

  public HospitalDTO() {
  }

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

  public String getFromAt() {
    return fromAt;
  }

  public void setFromAt(String fromAt) {
    this.fromAt = fromAt;
  }

  public String getEndAt() {
    return endAt;
  }

  public void setEndAt(String endAt) {
    this.endAt = endAt;
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
}
