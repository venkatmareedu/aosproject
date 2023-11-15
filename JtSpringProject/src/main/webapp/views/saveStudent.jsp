<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Student Records</title>
    
    <!-- Include jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
        }

        h2 {
            color: #333;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            overflow-x: auto; /* Add horizontal scrollbar if needed */
        }

        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }

        th {
            background-color: #4CAF50;
            color: white;
            cursor: pointer; /* Add cursor style for clickable elements */
        }

        tr:nth-child(even) {
            background-color: #f2f2f2;
        }

        .highlight {
            background-color: #FFD700; /* Change this color as needed */
        }

        #reloadButton {
            margin-top: 20px;
            padding: 10px;
            cursor: pointer;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 5px;
        }

        #recordCount {
            margin-top: 10px;
            color: #555;
        }
    </style>

    <script>
        var sortDirection = "asc";

        // Assuming you have an element with id="dataContainer" to display the data
        function reloadData() {
            $.get("/yourPage?sortDirection=" + sortDirection, function (data) {
                $("#dataContainer").html(data);
                highlightRows();
                updateRecordCount();
            });
        }

        function highlightRows() {
            // Remove existing highlights
            $("tbody tr").removeClass("highlight");
            
            // Add highlight to alternate rows
            $("tbody tr:even").addClass("highlight");
        }

        function updateRecordCount() {
            var recordCount = "${saveStudent.size()}"; // Assuming you have a model attribute named 'saveStudent'
            $("#recordCount").text("Total Records: " + recordCount);
        }

        function toggleSortDirection() {
            sortDirection = (sortDirection === "asc") ? "desc" : "asc";
            reloadData();
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

<div id="recordCount"></div>

<table border="1">
    <thead>
        <tr>
            <th onclick="toggleSortDirection()">ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Course</th>
            <th>Status</th>
            <th>Course ID</th>
        </tr>
    </thead>
    <tbody id="dataContainer">
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

<button id="reloadButton">Reload Data</button>

</body>
</html>
