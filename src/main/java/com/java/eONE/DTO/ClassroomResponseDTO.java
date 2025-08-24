package com.java.eONE.DTO;


public class ClassroomResponseDTO {
    private Long id;
	private String name;
    private String teacher_name; // matches Flutter expectation
    private String batch;
    private String year;
    
    public ClassroomResponseDTO(Long id, String name, String teacherName, String batch, String year) {
        this.id = id;
        this.name = name;
        this.teacher_name = teacherName;
        this.batch = batch;
        this.year = year;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getTeacher_name() {
		return teacher_name;
	}

	public void setTeacher_name(String teacher_name) {
		this.teacher_name = teacher_name;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
}
