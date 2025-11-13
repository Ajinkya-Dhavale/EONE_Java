package com.java.eONE.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class NotificationMessageDTO {

    private String message;

    @JsonProperty("created_at") // converts createdAt -> created_at in JSON
    private LocalDateTime createdAt;

    private String type; // "assignment" or "general"

    @JsonProperty("assignment_id")
    private Long assignmentId; // ID of related assignment (if any)

    public NotificationMessageDTO(String message, LocalDateTime createdAt, String type, Long assignmentId) {
        this.message = message;
        this.createdAt = createdAt;
        this.type = type;
        this.assignmentId = assignmentId;
    }

    // Default constructor needed for Jackson
    public NotificationMessageDTO() {}

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }
}
