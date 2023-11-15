package com.jtspringproject.JtSpringProject.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Student {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String studentName;
	
	private String studentEmail;
	
	private String courseName;
	
	private boolean studentStatus;
@Override
	public String toString() {
		return "Student [id=" + id + ", studentName=" + studentName + ", studentEmail=" + studentEmail + ", courseName="
				+ courseName + ", studentStatus=" + studentStatus + ", courseId=" + courseId + "]";
	}

	//	@ManyToOne
//	@JoinColumn(name="course_id")
	private int courseId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentEmail() {
		return studentEmail;
	}

	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public boolean isStudentStatus() {
		return studentStatus;
	}

	public void setStudentStatus(boolean studentStatus) {
		this.studentStatus = studentStatus;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	
	

}
