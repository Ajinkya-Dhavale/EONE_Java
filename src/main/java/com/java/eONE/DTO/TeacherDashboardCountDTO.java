package com.java.eONE.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TeacherDashboardCountDTO {

    @JsonProperty("subject_count")
    private long subjectCount;

    @JsonProperty("student_count")
    private long studentCount;

    @JsonProperty("assignment_count")
    private long assignmentCount;

    @JsonProperty("pending_approval_count")
    private long pendingApprovalCount;

    // Default Constructor
    public TeacherDashboardCountDTO() {
    }

    // Parameterized Constructor
    public TeacherDashboardCountDTO(long subjectCount, long studentCount, long assignmentCount, long pendingApprovalCount) {
        this.subjectCount = subjectCount;
        this.studentCount = studentCount;
        this.assignmentCount = assignmentCount;
        this.pendingApprovalCount = pendingApprovalCount;
    }

    // Getter for subjectCount
    public long getSubjectCount() {
        return subjectCount;
    }

    // Setter for subjectCount
    public void setSubjectCount(long subjectCount) {
        this.subjectCount = subjectCount;
    }

    // Getter for studentCount
    public long getStudentCount() {
        return studentCount;
    }

    // Setter for studentCount
    public void setStudentCount(long studentCount) {
        this.studentCount = studentCount;
    }

    // Getter for assignmentCount
    public long getAssignmentCount() {
        return assignmentCount;
    }

    // Setter for assignmentCount
    public void setAssignmentCount(long assignmentCount) {
        this.assignmentCount = assignmentCount;
    }

    // Getter for pendingApprovalCount
    public long getPendingApprovalCount() {
        return pendingApprovalCount;
    }

    // Setter for pendingApprovalCount
    public void setPendingApprovalCount(long pendingApprovalCount) {
        this.pendingApprovalCount = pendingApprovalCount;
    }
}