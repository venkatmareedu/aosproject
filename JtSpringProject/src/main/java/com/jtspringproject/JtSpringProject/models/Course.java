package com.jtspringproject.JtSpringProject.models;


	

	import javax.persistence.Entity;
	import javax.persistence.GeneratedValue;
	import javax.persistence.GenerationType;
	import javax.persistence.Id;

	@Entity
	public class Course {
	    @Id
	    private int course_id;

	    private String courseName;
	    private String instructorName;
	    @Override
		public String toString() {
			return "Course [course_id=" + course_id + ", courseName=" + courseName + ", instructorName="
					+ instructorName + ", classRoomNumber=" + classRoomNumber + "]";
		}

		private String classRoomNumber;

	    // Constructors, getters, and setters

	    public Course() {
	    }

	    public Course(String courseName, String instructorName, String classRoomNumber) {
	        this.courseName = courseName;
	        this.instructorName = instructorName;
	        this.classRoomNumber = classRoomNumber;
	    }

	    // Getters and setters...

	    public int getCourse_id() {
	        return course_id;
	    }

	    public void setCourse_id(int course_id) {
	        this.course_id = course_id;
	    }

	    public String getCourseName() {
	        return courseName;
	    }

	    public void setCourseName(String courseName) {
	        this.courseName = courseName;
	    }

	    public String getInstructorName() {
	        return instructorName;
	    }

	    public void setInstructorName(String instructorName) {
	        this.instructorName = instructorName;
	    }

	    public String getClassRoomNumber() {
	        return classRoomNumber;
	    }

	    public void setClassRoomNumber(String classRoomNumber) {
	        this.classRoomNumber = classRoomNumber;
	    }

	    // Other methods...
	}



