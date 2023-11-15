<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>Student Form</title>
    <style>
        /* Adjust the styling as needed */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }

        h2 {
            color: #333;
        }

        .container {
            max-width: 800px;
            margin: 20px auto;
            background-color: #fff;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        label {
            display: block;
            margin: 10px 0 5px;
            color: #333;
        }

        input {
            width: 100%;
            padding: 10px;
            margin-bottom: 10px;
            box-sizing: border-box;
        }

        input[type="checkbox"] {
            width: auto;
            margin-top: 5px;
        }

        input[type="submit"] {
            background-color: #4caf50;
            color: white;
            border: none;
            padding: 15px 20px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
            margin: 10px 0;
            cursor: pointer;
            border-radius: 5px;
        }

        input[type="submit"]:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>Student Form</h2>

    <form action="saveStudent" method="post">
        <!-- You can use hidden input for the ID if you are editing an existing student -->
        <c:if test="${not empty student.id}">
            <input type="hidden" name="id" value="${student.id}" />
        </c:if>

        <label for="studentName">Student Name:</label>
        <input type="text" id="studentName" name="studentName" value="${student.studentName}" required />

        <label for="studentEmail">Student Email:</label>
        <input type="email" id="studentEmail" name="studentEmail" value="${student.studentEmail}" required />

        <label for="courseName">Course Name:</label>
        <input type="text" id="courseName" name="courseName" value="${student.courseName}" required />

        <label for="studentStatus">Student Status:</label>
        <input type="checkbox" id="studentStatus" name="studentStatus" ${student.studentStatus ? 'checked' : ''} />

        <label for="courseId">Course ID:</label>
        <input type="number" id="courseId" name="courseId" value="${student.courseId}" required />

        <input type="submit" value="Save"/>
    </form>
</div>

</body>
</html>
