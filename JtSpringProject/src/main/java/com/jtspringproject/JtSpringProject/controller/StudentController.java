package com.jtspringproject.JtSpringProject.controller;



import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

import org.springframework.web.servlet.ModelAndView;
import java.util.List;
//
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import com.jtspringproject.JtSpringProject.models.Student;
import com.jtspringproject.JtSpringProject.services.courseService;
import com.jtspringproject.JtSpringProject.services.studentService;




import java.util.List;
import java.util.concurrent.*;


import java.util.List;
import java.util.concurrent.*;

@Controller
public class StudentController {
    @Autowired
    private studentService studentService;
    @Autowired
    private courseService courseService;

    // Create a priority queue to handle requests based on submission time (FCFS)
    private BlockingQueue<Runnable> requestQueue = new PriorityBlockingQueue<>(100, new Comparator<Runnable>() {
        @Override
        public int compare(Runnable o1, Runnable o2) {
            if (o1 instanceof SubmissionTimeRunnable && o2 instanceof SubmissionTimeRunnable) {
                long time1 = ((SubmissionTimeRunnable) o1).getSubmissionTime();
                long time2 = ((SubmissionTimeRunnable) o2).getSubmissionTime();
                return Long.compare(time1, time2);
            }
            return 0; // Handle other cases as needed
        }
    });

    // Create a ScheduledExecutorService with a fixed pool size and use the priority queue
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public String userForm(Model model) {
    	ModelAndView view=new ModelAndView();
    	
    	
    	
        System.out.println("Received request at /index");
        return "index";
    }

    @RequestMapping(value = "/saveStudent", method = RequestMethod.POST)
    public ModelAndView userlogin(@RequestParam("studentName") String studentName,
                                  @RequestParam("studentStatus") boolean studentStatus,
                                  @RequestParam("studentEmail") String studentEmail,
                                  @RequestParam("courseName") String courseName,
                                  @RequestParam("courseId") int courseId,
                                  Model model,
                                  HttpServletResponse res) {

        System.out.println("Received request at /saveStudent");

        // Create a submission-time-aware Runnable task for updating student asynchronously
        SubmissionTimeRunnable updateStudentTask = new SubmissionTimeRunnable(() -> {
            System.out.println("Updating student asynchronously");
            Student s = new Student();
            s.setCourseId(courseId);
            s.setCourseName(courseName);
            s.setStudentEmail(studentEmail);
            s.setStudentName(studentName);
            s.setStudentStatus(studentStatus);
            studentService.updateStudent(s);
        });

        // Schedule the task with a delay of 0 (execute immediately)
        scheduledExecutorService.schedule(updateStudentTask, 0, TimeUnit.MILLISECONDS);

        // Continue processing the ModelAndView
        ModelAndView mView = new ModelAndView("saveStudent");
        List<Student> students = this.studentService.getStudent();
        for (Student sr : students) {
            System.out.println(sr.toString() + "  ");
        }
        mView.addObject("saveStudent", students);
        System.out.println("saveStudent size: " + students.size());

        return mView;
    }

    // A wrapper class to store submission time with the Runnable task
    private static class SubmissionTimeRunnable implements Runnable, Comparable<SubmissionTimeRunnable> {
        private final Runnable task;
        private final long submissionTime;

        public SubmissionTimeRunnable(Runnable task) {
            this.task = task;
            this.submissionTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            System.out.println("Task execution started at: " + submissionTime);
            task.run();
            System.out.println("Task execution completed at: " + System.currentTimeMillis());
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


