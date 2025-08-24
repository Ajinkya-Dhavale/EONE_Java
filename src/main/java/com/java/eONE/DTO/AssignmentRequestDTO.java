package com.java.eONE.DTO;

import java.time.LocalDate;

public class AssignmentRequestDTO {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long subjectId;
    private String file;
    private Long teacherId;

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
}
