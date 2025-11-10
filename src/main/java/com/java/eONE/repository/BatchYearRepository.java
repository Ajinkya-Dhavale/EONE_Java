package com.java.eONE.repository;

import com.java.eONE.model.BatchYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * BatchYear Repository
 * 
 * Provides data access methods for BatchYear entity.
 * Follows Spring Data JPA conventions for query methods.
 */
@Repository
public interface BatchYearRepository extends JpaRepository<BatchYear, Long> {
    
    /**
     * Find batch year by name (case-sensitive)
     */
    Optional<BatchYear> findByName(String name);
    
    /**
     * Find batch year by code (case-sensitive)
     */
    Optional<BatchYear> findByCode(String code);
    
    /**
     * Find all active batch years ordered by display order
     * Used for populating dropdown lists
     */
    List<BatchYear> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    /**
     * Find all batch years ordered by display order (including inactive)
     */
    List<BatchYear> findAllByOrderByDisplayOrderAsc();
    
    /**
     * Check if batch year exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Check if batch year exists by code
     */
    boolean existsByCode(String code);
}

