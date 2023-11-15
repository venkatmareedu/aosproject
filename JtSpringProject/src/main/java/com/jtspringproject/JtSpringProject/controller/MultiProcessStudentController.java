package com.jtspringproject.JtSpringProject.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.models.Student;
import com.jtspringproject.JtSpringProject.services.studentService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.*;

@Controller
public class MultiProcessStudentController {


    @Autowired
    private studentService studentService;
	 private  int taskCounter = 0;

	    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	    @Async("asyncExecutor")
	    @RequestMapping(value = "/saveStudent", method = RequestMethod.POST)
	    public CompletableFuture<ModelAndView> userlogin(@RequestParam("studentName") String studentName,
	                                                      @RequestParam("studentStatus") boolean studentStatus,
	                                                      @RequestParam("studentEmail") String studentEmail,
	                                                      @RequestParam("courseName") String courseName,
	                                                      @RequestParam("courseId") int courseId,
	                                                      Model model,
	                                                      HttpServletResponse res) {

	        int taskId = ++taskCounter;

	        CompletableFuture<Void> updateStudentTask = CompletableFuture.runAsync(() -> {
	            System.out.println("Task " + taskId + " - Updating student asynchronously");
	            Student s = new Student();
	            s.setCourseId(courseId);
	            s.setCourseName(courseName);
	            s.setStudentEmail(studentEmail);
	            s.setStudentName(studentName);
	            s.setStudentStatus(studentStatus);
	            studentService.updateStudent(s);
	        }, executorService);

	        CompletableFuture<List<Student>> getStudentsTask = CompletableFuture.supplyAsync(() -> {
	            try {
	                // Simulate processing time
	                Thread.sleep(50);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            return studentService.getStudent();
	        }, executorService);

	        CompletableFuture<ModelAndView> result = updateStudentTask.thenCombine(getStudentsTask, (unused, students) -> {
	            ModelAndView mView = new ModelAndView("saveStudent");
	            for (Student sr : students) {
	                System.out.println("Task " + taskId + " - " + sr.toString());
	            }
	            mView.addObject("saveStudent", students);
	            System.out.println("Task " + taskId + " - saveStudent size: " + students.size());
	            return mView;
	        });

	        return result;
	    }
	
    private static class SubmissionTimeRunnable implements Runnable, Comparable<SubmissionTimeRunnable> {
        private final Runnable task;
        private final long submissionTime;

        public SubmissionTimeRunnable(Runnable task) {
            this.task = task;
            this.submissionTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            System.out.println("Task " + submissionTime + " execution started");
            task.run();
            System.out.println("Task " + submissionTime + " execution completed");
        }

        public long getSubmissionTime() {
            return submissionTime;
        }

        @Override
        public int compareTo(SubmissionTimeRunnable other) {
            return Long.compare(this.submissionTime, other.submissionTime);
        }
    }
}
