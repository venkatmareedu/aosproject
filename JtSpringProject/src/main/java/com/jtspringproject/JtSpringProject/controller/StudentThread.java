package com.jtspringproject.JtSpringProject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.Iterator;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.models.Student;
import com.jtspringproject.JtSpringProject.services.courseService;
import com.jtspringproject.JtSpringProject.services.studentService;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import com.jtspringproject.JtSpringProject.models.RequestDetailsEntity;
import com.jtspringproject.JtSpringProject.dao.RequestDetailsDAO;
import com.jtspringproject.JtSpringProject.dao.studentDAO;

@Controller
public class StudentThread {

    List<RequestDetailsEntity> request = new ArrayList<>();

    @Autowired
    private studentService studentService;

    @Autowired
    private courseService courseService;

    @Autowired
    private studentDAO studentDAO;

    @Autowired
    private RequestDetailsDAO requestDetailsDAO;  // Added RequestDetailsDAO

    private static final AtomicInteger timestampCounter = new AtomicInteger(0);
    private final LinkedList<RequestDetailsEntity> requestQueue = new LinkedList<>();

    // New map to store priorities for requests with the same timestamp
    private final Map<LocalDateTime, Integer> timestampPriorityMap = new TreeMap<>();

    private PriorityQueue<RequestDetailsEntity> priorityQueue = new PriorityQueue<>();

    private final List<RequestDetailsEntity> requestsInOneSecond = new ArrayList<>();

    @RequestMapping(value = "/saveStudent", method = RequestMethod.POST)
    public ModelAndView userlogin(@RequestParam("studentName") String studentName,
                                  @RequestParam("studentStatus") boolean studentStatus,
                                  @RequestParam("studentEmail") String studentEmail,
                                  @RequestParam("courseName") String courseName,
                                  @RequestParam("courseId") int courseId,
                                  Model model,
                                  HttpServletResponse res) throws InterruptedException {

        System.out.println("Received request at /saveStudent");
        
        Student studentEntity = new Student();
        studentEntity.setCourseId(courseId);
        studentEntity.setCourseName(courseName);
        studentEntity.setStudentEmail(studentEmail);
        studentEntity.setStudentName(studentName);
        studentEntity.setStudentStatus(studentStatus);

        CompletableFuture<Void> saveTask = CompletableFuture.runAsync(() -> {
            int processorNumber = getProcessorNumber();
            
            long threadNumber = Thread.currentThread().getId();
            int requestNumber = getRequestNumber(0);
            
            LocalDateTime startTime = LocalDateTime.now();
            
            System.out.println("Save Request handling started on Processor: " + processorNumber +
                    " at " + startTime);

            // Introduce a delay before processing the request
            RequestDetailsEntity r = new RequestDetailsEntity();

            sleepBeforeProcessing();

            studentDAO.updateStudent(studentEntity);

            LocalDateTime completionTime = LocalDateTime.now();
            System.out.println("Save Request handling completed on Processor: " + processorNumber +
                    " at " + completionTime);

            // New logic to determine priority based on timestamp
            LocalDateTime currentTimeStamp = LocalDateTime.now();
            int priority = timestampPriorityMap.computeIfAbsent(currentTimeStamp, k -> timestampPriorityMap.size());

            // Save request details to the database
            RequestDetailsEntity requestDetailsEntity = new RequestDetailsEntity();
            requestDetailsEntity.setProcessorNumber(processorNumber);
            requestDetailsEntity.setRequestNumber(requestNumber);
            requestDetailsEntity.setThreadNumber(threadNumber);
            requestDetailsEntity.setStartTime(startTime);//.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            requestDetailsEntity.setCompletionTime(completionTime);//.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            requestDetailsEntity.setExecutedStatus(false);
            requestDetailsEntity.setPriority(priority);
//            requestsInOneSecond.add(requestDetailsEntity);
//            assignPriorityInOneSecond();
//            for(RequestDetailsEntity r1:requestsInOneSecond) {
//            	if(requestDetailsEntity.getRequestNumber()== r1.getRequestNumber()) {
//            		requestDetailsEntity.setPriority(r.getPriority());
//            		requestDetailsEntity.setRequestNumber(requestDetailsEntity.getPriority());        
//            		requestDetailsEntity.setRequestAtSameTime(true);
//            	}
//            }
//            requestsInOneSecond.clear();

            requestDetailsDAO.saveRequestDetails(requestDetailsEntity);  // Use RequestDetailsDAO to save request details
        });

        CompletableFuture<Void> retrieveTask = CompletableFuture.runAsync(() -> {
            try {
                // Sleep to simulate processing time
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int processorNumber = getProcessorNumber();
            int requestNumber = getRequestNumber(0);
            long threadNumber = Thread.currentThread().getId();

            LocalDateTime startTime = LocalDateTime.now();
            System.out.println("Retrieve Request handling started on Processor: " + processorNumber +
                    " at " + startTime);

            // Introduce a delay before processing the request
            sleepBeforeProcessing();

            List<Student> students = this.studentDAO.getStudent();

            LocalDateTime completionTime = LocalDateTime.now();
            System.out.println("Retrieve Request handling completed on Processor: " + processorNumber +
                    " at " + completionTime);

            // New logic to determine priority based on timestamp
            LocalDateTime currentTimeStamp = LocalDateTime.now();

            int priority = timestampPriorityMap.computeIfAbsent(currentTimeStamp, k -> timestampPriorityMap.size());

            // Save request details to the database
            RequestDetailsEntity requestDetailsEntity = new RequestDetailsEntity();
            requestDetailsEntity.setProcessorNumber(processorNumber);
            requestDetailsEntity.setRequestNumber(requestNumber);
            requestDetailsEntity.setThreadNumber(threadNumber);
            requestDetailsEntity.setStartTime(startTime);//.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            requestDetailsEntity.setCompletionTime(completionTime);//.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            requestDetailsEntity.setPriority(priority);
//            requestsInOneSecond.add(requestDetailsEntity);
//            assignPriorityInOneSecond();
//            for(RequestDetailsEntity s:requestsInOneSecond) {
//            	if(requestDetailsEntity.getStartTime()==s.getStartTime()) {
//            		requestDetailsEntity.setPriority(s.getPriority());
//            		requestDetailsEntity.setRequestNumber(requestDetailsEntity.getPriority());
//            		requestDetailsEntity.setRequestAtSameTime(true);
//            	}
//            }
            
            requestDetailsDAO.saveRequestDetails(requestDetailsEntity);  // Use RequestDetailsDAO to save request details
        });

        // Wait for both tasks to complete
        CompletableFuture.allOf(saveTask, retrieveTask).join();
        List<RequestDetailsEntity> pendingRequests = requestDetailsDAO.getPendingRequests();
        for(RequestDetailsEntity wq:pendingRequests) {
        List<RequestDetailsEntity> pendingRequest = requestDetailsDAO.getPendingRequests(wq.getStartTime());
        if(pendingRequest!=null) {
        	for(RequestDetailsEntity a:pendingRequest) {
        	requestsInOneSecond.add(a);
        	}
        }
        
        }
        assignPriorityInOneSecond();
        // Continue processing the ModelAndView
        ModelAndView mView = new ModelAndView("saveStudent");

        List<Student> students = this.studentDAO.getStudent();

        mView.addObject("saveStudent", students);

        // Retrieve and process pending requests
        processPendingRequests();

        return mView;
    }

    private void processPendingRequests() {
        // Retrieve and process pending requests
        List<RequestDetailsEntity> pendingRequests = requestDetailsDAO.getPendingRequests();

        for (RequestDetailsEntity request : pendingRequests) {
            // Process each request based on the scheduling algorithm
            processRequest(request);

            // Update the request status to executed
            request.setExecutedStatus(true);
            requestDetailsDAO.updateRequestDetails(request);
        }
    }

    private void processRequest(RequestDetailsEntity request) {
        // Implement your scheduling logic here based on the First Come First Serve algorithm
        // ...

        // Enqueue the request to the queue with priority
        requestQueue.add(request);

        // Process requests in the order they arrive (FCFS)
        while (!requestQueue.isEmpty()) {
            RequestDetailsEntity nextRequest = requestQueue.poll();
            
            if (nextRequest != null) {
                // Simulate processing by sleeping for a duration (replace with your actual processing logic)
                try {
                    Thread.sleep(1000); // Sleep for 1 second as an example processing time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Print the processed request details
                System.out.println("Processing request: " + nextRequest.getRequestNumber() +
                        ", Processor: " + nextRequest.getProcessorNumber() +
                        ", Thread: " + nextRequest.getThreadNumber() +
                        ", StartTime: " + nextRequest.getStartTime() +
                        ", CompletionTime: " + nextRequest.getCompletionTime());

                // Update the request status to executed
                nextRequest.setExecutedStatus(true);
                requestDetailsDAO.updateRequestDetails(nextRequest);
            }
        }
       
    }

    private int getProcessorNumber() {
        // You can customize this method based on your specific needs to assign processor numbers.
        // For simplicity, it uses the current thread ID.
        return (int) Thread.currentThread().getId() % Runtime.getRuntime().availableProcessors();
    }

    private int getRequestNumber(int n) {
        // Use timestamp counter to ensure unique request numbers for requests with the same timestamp
    	List<RequestDetailsEntity> r=requestDetailsDAO.getPendingRequests();
    	if(r.size()>1) {
    		for(RequestDetailsEntity d:r) {
    			requestsInOneSecond.add(d);
    		}
    		assignPriorityInOneSecond();
    		
    		return timestampCounter.getAndIncrement()+n;
    	}
        return timestampCounter.getAndIncrement();
    	
    }

    
    private void assignPriorityInOneSecond() {
        // Sort the requests in one second based on thread number
    	
    	requestsInOneSecond.sort(Comparator.comparing(RequestDetailsEntity::getThreadNumber));

        // Assign priority based on the sorted order
        int priority = 0;
        Iterator<RequestDetailsEntity> iterator = requestsInOneSecond.iterator();
        while (iterator.hasNext()) {
            RequestDetailsEntity request = iterator.next();
            request.setPriority(priority++);
            request.setRequestNumber(request.getPriority());
            request.setRequestAtSameTime(true);
            requestDetailsDAO.updateRequestDetails(request);
            iterator.remove(); 
        }
    	
    	
//        requestsInOneSecond.sort(Comparator.comparing(RequestDetailsEntity::getThreadNumber));
//
//        // Assign priority based on the sorted order
//        int priority = 0;
//        for (RequestDetailsEntity request : requestsInOneSecond) {
//            request.setPriority(priority++);
//            request.setRequestNumber(request.getPriority());
//            request.setRequestAtSameTime(true);
//            requestDetailsDAO.updateRequestDetails(request);
//            
//        }
//
//        // Clear the list for the next second
//        requestsInOneSecond.clear();
    }
    
    private void sleepBeforeProcessing() {
        try {
            // Introduce a delay of 1 second before processing each request
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}




















//package com.jtspringproject.JtSpringProject.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.ModelAndView;
//
//import com.jtspringproject.JtSpringProject.models.Student;
//import com.jtspringproject.JtSpringProject.services.courseService;
//import com.jtspringproject.JtSpringProject.services.studentService;
//
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.PriorityQueue;
//import java.util.TreeMap;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import com.jtspringproject.JtSpringProject.models.RequestDetailsEntity;
//import com.jtspringproject.JtSpringProject.dao.RequestDetailsDAO;
//import com.jtspringproject.JtSpringProject.dao.studentDAO;
//
//@Controller
//public class StudentThread {
//
//	List<RequestDetailsEntity> request=new ArrayList<>();
//
//	
//    @Autowired
//    private studentService studentService;
//
//    @Autowired
//    private courseService courseService;
//
//    @Autowired
//    private studentDAO studentDAO;
//
//    @Autowired
//    private RequestDetailsDAO requestDetailsDAO;  // Added RequestDetailsDAO
//
//    private static final AtomicInteger timestampCounter = new AtomicInteger(0);
//    private final LinkedList<RequestDetailsEntity> requestQueue = new LinkedList<>();
//
//    // New map to store priorities for requests with the same timestamp
//    private final Map<LocalDateTime, Integer> timestampPriorityMap = new TreeMap<>();
//    
//    private PriorityQueue<RequestDetailsEntity> priorityQueue=new PriorityQueue<>();
//
//    @RequestMapping(value = "/saveStudent", method = RequestMethod.POST)
//    public ModelAndView userlogin(@RequestParam("studentName") String studentName,
//                                  @RequestParam("studentStatus") boolean studentStatus,
//                                  @RequestParam("studentEmail") String studentEmail,
//                                  @RequestParam("courseName") String courseName,
//                                  @RequestParam("courseId") int courseId,
//                                  Model model,
//                                  HttpServletResponse res) throws InterruptedException {
//
//        System.out.println("Received request at /saveStudent");
//
//        Student studentEntity = new Student();
//        studentEntity.setCourseId(courseId);
//        studentEntity.setCourseName(courseName);
//        studentEntity.setStudentEmail(studentEmail);
//        studentEntity.setStudentName(studentName);
//        studentEntity.setStudentStatus(studentStatus);
//
//        CompletableFuture<Void> saveTask = CompletableFuture.runAsync(() -> {
//            int processorNumber = getProcessorNumber();
//            int requestNumber = getRequestNumber();
//            long threadNumber = Thread.currentThread().getId();
//
//            LocalDateTime startTime = LocalDateTime.now();
//            System.out.println("Save Request handling started on Processor: " + processorNumber +
//                    " at " + startTime);
//
//            // Introduce a delay before processing the request
//            RequestDetailsEntity r=new RequestDetailsEntity();
//           
//            sleepBeforeProcessing();
//              
//            
//            studentDAO.updateStudent(studentEntity);
//
//            LocalDateTime completionTime = LocalDateTime.now();
//            System.out.println("Save Request handling completed on Processor: " + processorNumber +
//                    " at " + completionTime);
//
//            // New logic to determine priority based on timestamp
//            LocalDateTime currentTimeStamp = LocalDateTime.now();
//            int priority = timestampPriorityMap.computeIfAbsent(currentTimeStamp, k -> timestampPriorityMap.size());
//
//            // Save request details to the database
//            RequestDetailsEntity requestDetailsEntity = new RequestDetailsEntity();
//            requestDetailsEntity.setProcessorNumber(processorNumber);
//            requestDetailsEntity.setRequestNumber(requestNumber);
//            requestDetailsEntity.setThreadNumber(threadNumber);
//            requestDetailsEntity.setStartTime(startTime);//.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
//            requestDetailsEntity.setCompletionTime(completionTime);//.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
//            requestDetailsEntity.setExecutedStatus(false);
//            requestDetailsEntity.setPriority(priority);
//
//            requestDetailsDAO.saveRequestDetails(requestDetailsEntity);  // Use RequestDetailsDAO to save request details
//        });
//
//        CompletableFuture<Void> retrieveTask = CompletableFuture.runAsync(() -> {
//            try {
//                // Sleep to simulate processing time
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            int processorNumber = getProcessorNumber();
//            int requestNumber = getRequestNumber();
//            long threadNumber = Thread.currentThread().getId();
//
//            LocalDateTime startTime = LocalDateTime.now();
//            System.out.println("Retrieve Request handling started on Processor: " + processorNumber +
//                    " at " + startTime);
//
//            // Introduce a delay before processing the request
//            sleepBeforeProcessing();
//
//            List<Student> students = this.studentDAO.getStudent();
//
//            LocalDateTime completionTime = LocalDateTime.now();
//            System.out.println("Retrieve Request handling completed on Processor: " + processorNumber +
//                    " at " + completionTime);
//
//            // New logic to determine priority based on timestamp
//            LocalDateTime currentTimeStamp = LocalDateTime.now();
//            
//            
//            int priority = timestampPriorityMap.computeIfAbsent(currentTimeStamp, k -> timestampPriorityMap.size());
//
//            // Save request details to the database
//            RequestDetailsEntity requestDetailsEntity = new RequestDetailsEntity();
//            requestDetailsEntity.setProcessorNumber(processorNumber);
//            requestDetailsEntity.setRequestNumber(requestNumber);
//            requestDetailsEntity.setThreadNumber(threadNumber);
//            requestDetailsEntity.setStartTime(startTime);//.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
//            requestDetailsEntity.setCompletionTime(completionTime);//.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
//            requestDetailsEntity.setPriority(priority);
//
//            requestDetailsDAO.saveRequestDetails(requestDetailsEntity);  // Use RequestDetailsDAO to save request details
//        });
//
//        // Wait for both tasks to complete
//        CompletableFuture.allOf(saveTask, retrieveTask).join();
//
//        // Continue processing the ModelAndView
//        ModelAndView mView = new ModelAndView("saveStudent");
//
//        List<Student> students = this.studentDAO.getStudent();
//
//        mView.addObject("saveStudent", students);
//
//        // Retrieve and process pending requests
//        processPendingRequests();
//
//        return mView;
//    }
//
//    private void processPendingRequests() {
//        // Retrieve and process pending requests
//        List<RequestDetailsEntity> pendingRequests = requestDetailsDAO.getPendingRequests();
//
//        for (RequestDetailsEntity request : pendingRequests) {
//            // Process each request based on the scheduling algorithm
//            processRequest(request);
//
//            // Update the request status to executed
//            request.setExecutedStatus(true);
//            requestDetailsDAO.updateRequestDetails(request);
//        }
//    }
//
//    private void processRequest(RequestDetailsEntity request) {
//        // Implement your scheduling logic here based on the First Come First Serve algorithm
//        // ...
//
//        // Enqueue the request to the queue with priority
//        requestQueue.add(request);
//
//        // Process requests in the order they arrive (FCFS)
//        while (!requestQueue.isEmpty()) {
//            RequestDetailsEntity nextRequest = requestQueue.poll();
//            if (nextRequest != null) {
//                // Simulate processing by sleeping for a duration (replace with your actual processing logic)
//                try {
//                    Thread.sleep(1000); // Sleep for 1 second as an example processing time
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                // Print the processed request details
//                System.out.println("Processing request: " + nextRequest.getRequestNumber() +
//                        ", Processor: " + nextRequest.getProcessorNumber() +
//                        ", Thread: " + nextRequest.getThreadNumber() +
//                        ", StartTime: " + nextRequest.getStartTime() +
//                        ", CompletionTime: " + nextRequest.getCompletionTime());
//
//                // Update the request status to executed
//                nextRequest.setExecutedStatus(true);
//                requestDetailsDAO.updateRequestDetails(nextRequest);
//            }
//        }
//        // ...
//    }
//
//    private int getProcessorNumber() {
//        // You can customize this method based on your specific needs to assign processor numbers.
//        // For simplicity, it uses the current thread ID.
//        return (int) Thread.currentThread().getId() % Runtime.getRuntime().availableProcessors();
//    }
//
//    private int getRequestNumber() {
//        // Use timestamp counter to ensure unique request numbers for requests with the same timestamp
//        return timestampCounter.getAndIncrement();
//    }
//
//    private void sleepBeforeProcessing() {
//        try {
//            // Introduce a delay of 1 second before processing each request
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
