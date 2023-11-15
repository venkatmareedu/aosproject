package com.jtspringproject.JtSpringProject.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.Course;


@Repository
public class courseDAO {
	
	@Autowired
    private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf) {
        this.sessionFactory = sf;
    }
	
	@Transactional
	public List<Course> getCourse() {
	    return this.sessionFactory.getCurrentSession().createQuery("from Course").list();
	}

}
