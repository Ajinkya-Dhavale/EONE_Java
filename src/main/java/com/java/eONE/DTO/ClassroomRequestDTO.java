package com.java.eONE.DTO;

public class ClassroomRequestDTO {
    private String name;
    private String batch;
    private String year;
    private Long teacherId;

    // Default constructor
    public ClassroomRequestDTO() {
    }

    // Parameterized constructor
    public ClassroomRequestDTO(String name, String batch, String year, Long teacherId) {
        this.name = name;
        this.batch = batch;
        this.year = year;
        this.teacherId = teacherId;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getBatch() {
        return batch;
    }

    public String getYear() {
        return year;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
}