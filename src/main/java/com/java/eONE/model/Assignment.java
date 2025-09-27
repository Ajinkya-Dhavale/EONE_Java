package com.java.eONE.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assignments", indexes = {
    @Index(name = "index_assignments_on_subject_id", columnList = "subject_id"),
    @Index(name = "index_assignments_on_teacher_id", columnList = "teacher_id")
})
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // File name or path stored in DB (adjust type if needed)
    private String file;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<AssignmentSubmission> assignmentSubmissions;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public List<AssignmentSubmission> getAssignmentSubmissions() {
        return assignmentSubmissions;
    }

    public void setAssignmentSubmissions(List<AssignmentSubmission> assignmentSubmissions) {
        this.assignmentSubmissions = assignmentSubmissions;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
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

    // Helper method similar to Rails file_url
    public String getFileUrl() {
        if (file != null && !file.isEmpty()) {
            // Replace with your actual base URL or storage service URL
        	 return "http://localhost:8080/uploads/" + file;
        }
        return null;
    }
}
