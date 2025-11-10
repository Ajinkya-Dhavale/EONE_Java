package com.java.eONE.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "join_requests", indexes = {
    @Index(name = "idx_join_requests_student_class", columnList = "student_user_id,classroom_id")
})
public class JoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_user_id", nullable = false)
    private User student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @ManyToOne
    @JoinColumn(name = "approved_by_teacher_id")
    private User approvedByTeacher;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getApprovedByTeacher() { return approvedByTeacher; }
    public void setApprovedByTeacher(User approvedByTeacher) { this.approvedByTeacher = approvedByTeacher; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
}


