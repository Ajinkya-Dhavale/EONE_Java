package com.java.eONE.repository;

import com.java.eONE.model.JoinRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {
    List<JoinRequest> findByClassroom_IdAndStatus(Long classroomId, String status);
    List<JoinRequest> findByStudent_Id(Long studentId);
    Optional<JoinRequest> findByIdAndClassroom_Id(Long id, Long classroomId);
}


