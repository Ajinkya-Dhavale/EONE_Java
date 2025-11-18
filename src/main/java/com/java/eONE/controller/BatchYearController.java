package com.java.eONE.controller;

import com.java.eONE.model.BatchYear;
import com.java.eONE.service.BatchYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * BatchYear Controller
 * 
 * REST API endpoints for batch year management.
 * Provides endpoints for fetching and managing batch years.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/batch-years", produces = "application/json")
public class BatchYearController {

    private final BatchYearService batchYearService;

    @Autowired
    public BatchYearController(BatchYearService batchYearService) {
        this.batchYearService = batchYearService;
    }

    /**
     * GET /api/v1/batch-years
     * Get all active batch years (for dropdown population)
     * 
     * Returns only active batch years ordered by display order.
     * This is the main endpoint used by the frontend to populate dropdowns.
     */
    @GetMapping
    public ResponseEntity<?> getActiveBatchYears() {
        try {
            List<BatchYear> batchYears = batchYearService.getActiveBatchYears();
            return ResponseEntity.ok(Map.of("batch_years", batchYears));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch batch years: " + e.getMessage()));
        }
    }

    /**
     * GET /api/v1/batch-years/all
     * Get all batch years including inactive ones
     * 
     * Used for admin management interface.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllBatchYears() {
        try {
            List<BatchYear> batchYears = batchYearService.getAllBatchYears();
            return ResponseEntity.ok(Map.of("batch_years", batchYears));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch batch years: " + e.getMessage()));
        }
    }

    /**
     * GET /api/v1/batch-years/{id}
     * Get batch year by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBatchYearById(@PathVariable Long id) {
        try {
            return batchYearService.getBatchYearById(id)
                    .<ResponseEntity<?>>map(batchYear -> ResponseEntity.ok(batchYear))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Batch year not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch batch year: " + e.getMessage()));
        }
    }

    /**
     * POST /api/v1/batch-years
     * Create a new batch year
     * 
     * Admin endpoint for creating new batch year options.
     */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createBatchYear(@RequestBody BatchYear batchYear) {
        try {
            BatchYear created = batchYearService.createBatchYear(batchYear);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Batch year created successfully", "batch_year", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create batch year: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/v1/batch-years/{id}
     * Update an existing batch year
     */
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateBatchYear(@PathVariable Long id, @RequestBody BatchYear batchYear) {
        try {
            BatchYear updated = batchYearService.updateBatchYear(id, batchYear);
            return ResponseEntity.ok(Map.of("message", "Batch year updated successfully", "batch_year", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update batch year: " + e.getMessage()));
        }
    }

    /**
     * PATCH /api/v1/batch-years/{id}/deactivate
     * Deactivate a batch year (soft delete)
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateBatchYear(@PathVariable Long id) {
        try {
            boolean success = batchYearService.deactivateBatchYear(id);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Batch year deactivated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Batch year not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to deactivate batch year: " + e.getMessage()));
        }
    }

    /**
     * PATCH /api/v1/batch-years/{id}/activate
     * Activate a batch year
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateBatchYear(@PathVariable Long id) {
        try {
            boolean success = batchYearService.activateBatchYear(id);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Batch year activated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Batch year not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to activate batch year: " + e.getMessage()));
        }
    }
}

