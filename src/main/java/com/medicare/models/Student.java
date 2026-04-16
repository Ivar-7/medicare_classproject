package com.medicare.models;

import java.time.LocalDate;
import java.io.Serializable;

public class Student implements Serializable {

  private int regNumber;
  private String firstName;
  private String lastName;
  private LocalDate dob;
  private String faculty;
  private String email;
  private String phone;
  private String address;
  private String emergencyContact;
  private LocalDate createdAt;
  private LocalDate updatedAt;

  public Student() {
  }

  public Student(int regNumber, String firstName, String lastName, LocalDate dob,
      String faculty, String email, String phone, String address,
      String emergencyContact, LocalDate createdAt, LocalDate updatedAt) {
    this.regNumber = regNumber;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dob = dob;
    this.faculty = faculty;
    this.email = email;
    this.phone = phone;
    this.address = address;
    this.emergencyContact = emergencyContact;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // ── Getters ──────────────────────────────────────────────────────────────

  public int getRegNumber() {
    return regNumber;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public LocalDate getDob() {
    return dob;
  }

  public String getFaculty() {
    return faculty;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public String getAddress() {
    return address;
  }

  public String getEmergencyContact() {
    return emergencyContact;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public LocalDate getUpdatedAt() {
    return updatedAt;
  }

  // ── Setters ──────────────────────────────────────────────────────────────

  public void setRegNumber(int regNumber) {
    this.regNumber = regNumber;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setDob(LocalDate dob) {
    this.dob = dob;
  }

  public void setFaculty(String faculty) {
    this.faculty = faculty;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setEmergencyContact(String contact) {
    this.emergencyContact = contact;
  }

  public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(LocalDate updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return "Student{regNumber=" + regNumber + ", firstName='" + firstName + "', lastName='" + lastName + "'}";
  }
}
