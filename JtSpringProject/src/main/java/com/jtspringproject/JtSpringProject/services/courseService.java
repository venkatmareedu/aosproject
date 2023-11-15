package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.courseDAO;
import com.jtspringproject.JtSpringProject.dao.studentDAO;
import com.jtspringproject.JtSpringProject.models.Course;

@Service
public class courseService {

	@Autowired
	private courseDAO coursedao;
	
	public List<Course> getStudent(){
		return this.coursedao.getCourse();
	}
	
}
