<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Student Records</title>
</head>
<body>

<h2>Student Records</h2>

<table border="1">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Course</th>
            <th>Status</th>
            <th>Course ID</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="student" items="${saveStudent}">
            <tr>
                <td>${student.id}</td>
                <td>${student.studentName}</td>
                <td>${student.studentEmail}</td>
                <td>${student.courseName}</td>
                <td>${student.studentStatus}</td>
                <td>${student.courseId}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>

</body>
</html>
