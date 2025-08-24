package com.java.eONE.DTO;


import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponsesDTO {
    private Long id;
    private String email;
    private String name;
    @JsonProperty("mobile_number")
    private String mobileNumber;
    private Integer status; // or enum, depending on your entity
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    private Long roleId;
    private Long classroomId;

    // Constructor(s), getters, setters

    public UserResponsesDTO() {}

    public UserResponsesDTO(Long id, String email, String name, String mobileNumber, Integer status, LocalDate dateOfBirth, Long roleId, Long classroomId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.status = status;
        this.dateOfBirth = dateOfBirth;
        this.roleId = roleId;
        this.classroomId = classroomId;
    }

    // Getters and setters here ...
}
