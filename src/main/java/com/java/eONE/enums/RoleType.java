package com.java.eONE.enums;

/**
 * Role Type Enum
 * Represents user roles in the system
 * 
 * Role Types:
 * - ADMIN: System administrator with full access
 * - TEACHER: Teacher role with subject management capabilities
 * - STUDENT: Student role with learning access
 */
public enum RoleType {
    ADMIN("ADMIN", "Admin", "System administrator with full access"),
    TEACHER("Teacher", "Teacher", "Teacher role with subject management capabilities"),
    STUDENT("Student", "Student", "Student role with learning access");

    private final String code;
    private final String displayName;
    private final String description;

    RoleType(String code, String displayName, String description) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get RoleType by code
     */
    public static RoleType fromCode(String code) {
        for (RoleType roleType : RoleType.values()) {
            if (roleType.code.equalsIgnoreCase(code)) {
                return roleType;
            }
        }
        throw new IllegalArgumentException("No RoleType found for code: " + code);
    }

    /**
     * Get RoleType by display name
     */
    public static RoleType fromDisplayName(String displayName) {
        for (RoleType roleType : RoleType.values()) {
            if (roleType.displayName.equalsIgnoreCase(displayName)) {
                return roleType;
            }
        }
        throw new IllegalArgumentException("No RoleType found for display name: " + displayName);
    }
}

