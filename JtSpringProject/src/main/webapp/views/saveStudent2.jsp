<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Student Records</title>
    
    <!-- Include jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>

    <!-- Your existing scripts or stylesheets -->

    <script>
        // Assuming you have an element with id="dataContainer" to display the data
        function reloadData() {
            $.get("/yourPage", function (data) {
                $("#dataContainer").html(data);
            });
        }

        $(document).ready(function () {
            // Reload data on page load
            reloadData();

            // Bind the reloadData function to a button click event
            $("#reloadButton").click(function () {
                reloadData();
            });
        });
    </script>
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
