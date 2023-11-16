package com.jtspringproject.JtSpringProject.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.RequestDetailsEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RequestDetailsDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    @Transactional
    public void saveRequestDetails(RequestDetailsEntity requestDetailsEntity) {
        this.sessionFactory.getCurrentSession().save(requestDetailsEntity);
    }

    @Transactional
    public List<RequestDetailsEntity> getPendingRequests() {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from RequestDetailsEntity where executedStatus = false").list();
    }

    @Transactional
    public List<RequestDetailsEntity> getPendingRequests(LocalDateTime requestReceivedTime) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from RequestDetailsEntity where executedStatus = false and startTime = :receivedTime")
                .setParameter("receivedTime", requestReceivedTime)
                .list();
    }
    
    @Transactional
    public void updateRequestDetails(RequestDetailsEntity requestDetailsEntity) {
        this.sessionFactory.getCurrentSession().update(requestDetailsEntity);
    }
}
