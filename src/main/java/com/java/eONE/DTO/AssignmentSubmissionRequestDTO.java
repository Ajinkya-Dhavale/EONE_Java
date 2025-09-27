package com.java.eONE.DTO;

public class AssignmentSubmissionRequestDTO {
    private Long assignmentId;
    private Long userId;
    private String file;

    // Getters and setters

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }
}
