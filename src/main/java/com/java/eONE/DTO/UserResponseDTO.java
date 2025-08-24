package com.java.eONE.DTO;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponseDTO {
    private Long id;
    private String email;
    private String name;
    
    @JsonProperty("mobile_number")
    private String mobileNumber;
    private Integer status;
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    private String role;
    private String classroom;
    private Long classroomId;
    private String token; // nullable
    private String avatarUrl;

    // No-arg constructor
    public UserResponseDTO() {}

    // All-args constructor
    public UserResponseDTO(Long id, String email, String name, String mobileNumber, Integer status, LocalDate dateOfBirth,
                           String role, String classroom, Long classroomId, String token, String avatarUrl) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.status = status;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
        this.classroom = classroom;
        this.classroomId = classroomId;
        this.token = token;
        this.avatarUrl = avatarUrl;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
