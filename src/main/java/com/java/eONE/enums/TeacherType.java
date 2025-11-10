package com.java.eONE.enums;

/**
 * Teacher Type Enum
 * Represents different types of teachers in the system
 * 
 * Teacher Types:
 * - CLASS_TEACHER: Class teacher who can approve/reject student join requests
 * - SUBJECT_TEACHER: Subject teacher who can only view students
 */
public enum TeacherType {
    CLASS_TEACHER("CLASS_TEACHER", "Class Teacher", "Class teacher who can approve/reject student join requests"),
    SUBJECT_TEACHER("SUBJECT_TEACHER", "Subject Teacher", "Subject teacher who can only view students");

    private final String code;
    private final String displayName;
    private final String description;

    TeacherType(String code, String displayName, String description) {
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
     * Get TeacherType by code
     */
    public static TeacherType fromCode(String code) {
        for (TeacherType teacherType : TeacherType.values()) {
            if (teacherType.code.equalsIgnoreCase(code)) {
                return teacherType;
            }
        }
        throw new IllegalArgumentException("No TeacherType found for code: " + code);
    }

    /**
     * Check if teacher type is class teacher
     */
    public boolean isClassTeacher() {
        return this == CLASS_TEACHER;
    }
}

