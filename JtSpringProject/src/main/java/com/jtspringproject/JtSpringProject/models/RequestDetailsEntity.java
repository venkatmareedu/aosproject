package com.jtspringproject.JtSpringProject.models;

import java.time.LocalDateTime;

import javax.persistence.*;


@Entity
@Table(name = "request_details")
public class RequestDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "processor_number")
    private int processorNumber;

    @Column(name = "request_number")
    private int requestNumber;

    @Column(name = "thread_number")
    private long threadNumber;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "completion_time")
    private LocalDateTime completionTime;
    

    @Column(name = "executed_status")
    private boolean executedStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getProcessorNumber() {
        return processorNumber;
    }

    public void setProcessorNumber(int processorNumber) {
        this.processorNumber = processorNumber;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public long getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(long threadNumber) {
        this.threadNumber = threadNumber;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    public boolean isExecutedStatus() {
        return executedStatus;
    }

    public void setExecutedStatus(boolean executedStatus) {
        this.executedStatus = executedStatus;
    }
    
    @Column(name = "priority")
    private int priority;

    @Column(name = "request_at_same_time")
    private boolean requestAtSameTime;

    // ... (getters and setters)

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isRequestAtSameTime() {
        return requestAtSameTime;
    }

    public void setRequestAtSameTime(boolean requestAtSameTime) {
        this.requestAtSameTime = requestAtSameTime;
    }

	
    
}
