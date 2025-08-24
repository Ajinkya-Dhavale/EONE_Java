package com.java.eONE.DTO;
//
//import java.time.LocalDateTime;
//
//public class ViewSubmittedAssignmentDTO {
//    private Long id;
//    private Long assignmentId;
//    private Long userId;
//    private String studentName;
//    private String file;
//    private String fileUrl;
//    private LocalDateTime createdAt;
//    private Integer marks;
//    private String grade;
//    private String status;
//
//    public ViewSubmittedAssignmentDTO() {
//    }
//
//    public ViewSubmittedAssignmentDTO(Long id, Long assignmentId, Long userId, String studentName, String file, String fileUrl, LocalDateTime createdAt, Integer marks, String grade) {
//        this.id = id;
//        this.assignmentId = assignmentId;
//        this.userId = userId;
//        this.studentName = studentName;
//        this.file = file;
//        this.fileUrl = fileUrl;
//        this.createdAt = createdAt;
//        this.marks = marks;
//        this.grade = grade;
//        this.status = (marks != null) ? "Graded" : "Submitted";
//    }
//
//    // Getters
//    public Long getId() {
//        return id;
//    }
//
//    public Long getAssignmentId() {
//        return assignmentId;
//    }
//
//    public Long getUserId() {
//        return userId;
//    }
//
//    public String getStudentName() {
//        return studentName;
//    }
//
//    public String getFile() {
//        return file;
//    }
//
//    public String getFileUrl() {
//        return fileUrl;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public Integer getMarks() {
//        return marks;
//    }
//
//    public String getGrade() {
//        return grade;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    // Setters
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public void setAssignmentId(Long assignmentId) {
//        this.assignmentId = assignmentId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }
//
//    public void setStudentName(String studentName) {
//        this.studentName = studentName;
//    }
//
//    public void setFile(String file) {
//        this.file = file;
//    }
//
//    public void setFileUrl(String fileUrl) {
//        this.fileUrl = fileUrl;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public void setMarks(Integer marks) {
//        this.marks = marks;
//        this.status = (marks != null) ? "Graded" : "Submitted";
//    }
//
//    public void setGrade(String grade) {
//        this.grade = grade;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//}


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ViewSubmittedAssignmentDTO {
    private Long id;
    
    @JsonProperty("assignment_id")
    private Long assignmentId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("student_name")
    private String studentName;
    private String file;
    @JsonProperty("file_url")
    private String fileUrl;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    private Integer marks;
    private String grade;
    private String status;

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) { this.marks = marks; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
