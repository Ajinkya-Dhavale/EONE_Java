package com.java.eONE.DTO;

import java.util.List;

public class SubjectDTO {
    private Long id;
    private String name;
    private String startTime;
    private String endTime;
    private Long teacherId;
    private Long classroomId;
    private List<String> daysList;

    // Constructors
    public SubjectDTO() {}

    public SubjectDTO(Long id, String name, String startTime, String endTime, Long teacherId, Long classroomId, List<String> daysList) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teacherId = teacherId;
        this.classroomId = classroomId;
        this.daysList = daysList;
    }

    // Getters & setters

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public List<String> getDaysList() {
        return daysList;
    }

    public void setDaysList(List<String> daysList) {
        this.daysList = daysList;
    }
}
