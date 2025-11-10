package com.java.eONE.enums;

/**
 * User Status Enum
 * Represents the approval/block status of users in the system
 * 
 * Status Values:
 * - PENDING (0): User registered but not approved
 * - APPROVED (1): User approved and can log in
 * - REJECTED (2): User registration rejected
 * - BLOCKED (3): User blocked by admin, cannot log in
 */
public enum UserStatus {
    PENDING(0, "Pending", "User registered but not approved"),
    APPROVED(1, "Approved", "User approved and can log in"),
    REJECTED(2, "Rejected", "User registration rejected"),
    BLOCKED(3, "Blocked", "User blocked by admin, cannot log in");

    private final int value;
    private final String label;
    private final String description;

    UserStatus(int value, String label, String description) {
        this.value = value;
        this.label = label;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get UserStatus by integer value
     */
    public static UserStatus fromValue(int value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("No UserStatus found for value: " + value);
    }

    /**
     * Check if status allows login
     */
    public boolean canLogin() {
        return this == APPROVED;
    }

    /**
     * Check if status is active (not blocked or rejected)
     */
    public boolean isActive() {
        return this == PENDING || this == APPROVED;
    }
}

