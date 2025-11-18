package com.java.eONE.service;

import com.java.eONE.model.BatchYear;
import com.java.eONE.repository.BatchYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * BatchYear Service
 * 
 * Business logic for batch year operations.
 * Provides a clean interface for batch year management.
 */
@Service
public class BatchYearService {

    private final BatchYearRepository batchYearRepository;

    @Autowired
    public BatchYearService(BatchYearRepository batchYearRepository) {
        this.batchYearRepository = batchYearRepository;
    }

    /**
     * Get all active batch years ordered by display order
     * Used for populating dropdown lists in the frontend
     */
    public List<BatchYear> getActiveBatchYears() {
        return batchYearRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    /**
     * Get all batch years (including inactive) ordered by display order
     */
    public List<BatchYear> getAllBatchYears() {
        return batchYearRepository.findAllByOrderByDisplayOrderAsc();
    }

    /**
     * Get batch year by ID
     */
    public Optional<BatchYear> getBatchYearById(Long id) {
        return batchYearRepository.findById(id);
    }

    /**
     * Get batch year by name
     */
    public Optional<BatchYear> getBatchYearByName(String name) {
        return batchYearRepository.findByName(name);
    }

    /**
     * Create a new batch year
     */
    @Transactional
    public BatchYear createBatchYear(BatchYear batchYear) {
        // Validate name uniqueness
        if (batchYearRepository.existsByName(batchYear.getName())) {
            throw new IllegalArgumentException("Batch year with name '" + batchYear.getName() + "' already exists");
        }
        
        // Validate code uniqueness if provided
        if (batchYear.getCode() != null && !batchYear.getCode().trim().isEmpty()) {
            if (batchYearRepository.existsByCode(batchYear.getCode())) {
                throw new IllegalArgumentException("Batch year with code '" + batchYear.getCode() + "' already exists");
            }
        }
        
        return batchYearRepository.save(batchYear);
    }

    /**
     * Update an existing batch year
     */
    @Transactional
    public BatchYear updateBatchYear(Long id, BatchYear updates) {
        return batchYearRepository.findById(id).map(existing -> {
            // Update name if provided and different
            if (updates.getName() != null && !updates.getName().equals(existing.getName())) {
                if (batchYearRepository.existsByName(updates.getName())) {
                    throw new IllegalArgumentException("Batch year with name '" + updates.getName() + "' already exists");
                }
                existing.setName(updates.getName());
            }
            
            // Update code if provided
            if (updates.getCode() != null) {
                String newCode = updates.getCode().trim().isEmpty() ? null : updates.getCode().trim();
                if (newCode != null && !newCode.equals(existing.getCode())) {
                    if (batchYearRepository.existsByCode(newCode)) {
                        throw new IllegalArgumentException("Batch year with code '" + newCode + "' already exists");
                    }
                }
                existing.setCode(newCode);
            }
            
            // Update other fields
            if (updates.getDisplayOrder() != null) {
                existing.setDisplayOrder(updates.getDisplayOrder());
            }
            if (updates.getDescription() != null) {
                existing.setDescription(updates.getDescription());
            }
            if (updates.getIsActive() != null) {
                existing.setIsActive(updates.getIsActive());
            }
            
            return batchYearRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Batch year with ID " + id + " not found"));
    }

    /**
     * Soft delete - deactivate a batch year
     */
    @Transactional
    public boolean deactivateBatchYear(Long id) {
        return batchYearRepository.findById(id).map(batchYear -> {
            batchYear.setIsActive(false);
            batchYearRepository.save(batchYear);
            return true;
        }).orElse(false);
    }

    /**
     * Activate a batch year
     */
    @Transactional
    public boolean activateBatchYear(Long id) {
        return batchYearRepository.findById(id).map(batchYear -> {
            batchYear.setIsActive(true);
            batchYearRepository.save(batchYear);
            return true;
        }).orElse(false);
    }

    /**
     * Hard delete - permanently delete a batch year
     * Use with caution - only if no classrooms reference it
     */
    @Transactional
    public boolean deleteBatchYear(Long id) {
        if (batchYearRepository.existsById(id)) {
            batchYearRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

