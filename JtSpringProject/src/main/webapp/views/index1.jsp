<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>Student Form</title>
</head>
<body>

<h2>Student Form</h2>

<form action="saveStudent" method="post">
    <!-- You can use hidden input for the ID if you are editing an existing student -->
    <c:if test="${not empty student.id}">
        <input type="hidden" name="id" value="${student.id}" />
    </c:if>

    <label for="studentName">Student Name:</label>
    <input type="text" id="studentName" name="studentName" value="${student.studentName}" required /><br/>

    <label for="studentEmail">Student Email:</label>
    <input type="email" id="studentEmail" name="studentEmail" value="${student.studentEmail}" required /><br/>

    <label for="courseName">Course Name:</label>
    <input type="text" id="courseName" name="courseName" value="${student.courseName}" required /><br/>

    <label for="studentStatus">Student Status:</label>
    <input type="checkbox" id="studentStatus" name="studentStatus" ${student.studentStatus ? 'checked' : ''} /><br/>

    <label for="courseId">Course ID:</label>
    <input type="number" id="courseId" name="courseId" value="${student.courseId}" required /><br/>

    <input type="submit" value="Save"/>
</form>

</body>
</html>
