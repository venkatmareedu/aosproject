package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.studentDAO;
import com.jtspringproject.JtSpringProject.models.Course;
import com.jtspringproject.JtSpringProject.models.Student;
@Service
public class studentService {
	@Autowired
	private studentDAO studentdao;
	
	public Student updateStudent(Student st) {
		return this.studentdao.updateStudent(st);
		
	}
	public List<Student> getStudent(){
		return this.studentdao.getStudent();
	}

	
	
	

}
