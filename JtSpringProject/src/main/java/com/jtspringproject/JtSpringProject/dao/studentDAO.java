package com.jtspringproject.JtSpringProject.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.Course;
import com.jtspringproject.JtSpringProject.models.Student;
@Repository
public class studentDAO {
	@Autowired
    private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf) {
        this.sessionFactory = sf;
    }
	
//	@Transactional
//	public List<Student> getStudent() {
//		return this.sessionFactory.getCurrentSession().createQuery("from STUDENT").list();
//	}
	
	@Transactional
	public List<Student> getStudent() {
	    return this.sessionFactory.getCurrentSession().createQuery("from Student").list();
	}

	
	@Transactional
	public Student updateStudent(Student st) {
		 System.out.println(st.getId());
		    Student stu = this.sessionFactory.getCurrentSession().get(Student.class, st.getId());

		    if (stu != null) {
		        // Update the properties
		        stu.setCourseId(st.getCourseId());
		        stu.setCourseName(st.getCourseName());
		        stu.setStudentEmail(st.getStudentEmail());
		        stu.setStudentName(st.getStudentName());

		        // No need to call merge, just update the entity
		        this.sessionFactory.getCurrentSession().update(stu);
		    } else {
		       
		        Serializable id = this.sessionFactory.getCurrentSession().save(st);
		        System.out.println("Generated ID for the new student: " + id);
		    }

		    return stu;
	}

	
	
	
	
}
