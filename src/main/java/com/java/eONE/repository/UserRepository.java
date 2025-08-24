package com.java.eONE.repository;

import com.java.eONE.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	 Optional<User> findByEmail(String email);
	 
	 @Query("SELECT u FROM User u WHERE u.role.id = ?1 AND u.classroom.id = ?2 AND u.status = ?3 AND u.role.name <> 'admin'")
	    List<User> findByRoleClassroomStatusExcludingAdmin(Long roleId, Long classroomId, Integer status);

	    // Find by role id and status, exclude admins
	    @Query("SELECT u FROM User u WHERE u.role.id = ?1 AND u.status = ?2 AND u.role.name <> 'admin'")
	    List<User> findByRoleAndStatusExcludingAdmin(Long roleId, Integer status);

	    // Count by status and roles in list
	    long countByStatusAndRoleIdIn(Integer status, List<Long> roleIds);

	    // Count students by classroom and role
	    long countByClassroomIdAndRoleId(Long classroomId, Long roleId);

	    // Count students by classroom, role and status
	    long countByClassroomIdAndRoleIdAndStatus(Long classroomId, Long roleId, Integer status);
	    
	    @Query("SELECT COUNT(DISTINCT u.id) FROM User u " +
	            "JOIN u.classroom c " +
	            "JOIN Subject s ON s.classroom.id = c.id " +
	            "WHERE s.teacher.id = :teacherId " +
	            "AND u.role.name = 'Student' " +
	            "AND u.status = :status")
	     long countStudentsByTeacherIdAndStatus(@Param("teacherId") Long teacherId,
	                                            @Param("status") int status);
}
