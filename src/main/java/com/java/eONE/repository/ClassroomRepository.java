package com.java.eONE.repository;

import com.java.eONE.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    // Find classroom by name, batch, and year combination (legacy - for backward compatibility)
    Optional<Classroom> findByNameAndBatchAndYear(String name, String batch, String year);
    
    // Find classroom by name, batch_year_id, and year combination (new method)
    @Query("SELECT c FROM Classroom c WHERE c.name = :name AND " +
           "(:batchYearId IS NULL AND c.batchYear IS NULL OR c.batchYear.id = :batchYearId) AND " +
           "(:year IS NULL AND c.year IS NULL OR c.year = :year)")
    Optional<Classroom> findByNameAndBatchYearIdAndYear(
        @Param("name") String name, 
        @Param("batchYearId") Long batchYearId, 
        @Param("year") String year
    );
}
