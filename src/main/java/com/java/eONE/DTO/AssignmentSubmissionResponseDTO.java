package com.java.eONE.DTO;


import java.time.LocalDateTime;

public class AssignmentSubmissionResponseDTO {

    private Long id;
    private Long assignmentId;
    private Long userId;
    private String file;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer marks;
    private String grade;
    private String fileUrl; // computed field

    // Default constructor
    public AssignmentSubmissionResponseDTO() {
    }

    // Parameterized constructor
    public AssignmentSubmissionResponseDTO(Long id, Long assignmentId, Long userId, String file, LocalDateTime createdAt, LocalDateTime updatedAt, Integer marks, String grade, String fileUrl) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.userId = userId;
        this.file = file;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.marks = marks;
        this.grade = grade;
        this.fileUrl = fileUrl;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}