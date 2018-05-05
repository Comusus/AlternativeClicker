<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
  <title>Instructor Attendance Tracker</title>
  
  <!-- CSS
  ?????????????????????????????????????????????????? -->
  <link rel="stylesheet" href="css/normalize.css">
  <link rel="stylesheet" href="css/skeleton.css">
  <link rel="stylesheet" href="css/main.css">  
  
</head>

<body>

<%
	if(session.getAttribute("username")==null){
		response.sendRedirect("index.html");
	}
%>

<div class="container">
  <div class="row">
    <div align="center">
      <h2>Instructor Attendance Tracker</h2>
	  <h4>Session ID: ${requestScope["sessionID"]}</h4>
          <h6>Status: ${requestScope["statusMsg"]}</h6>
    </div>
  </div>
</div>

<div class="row">
  <div class="one-half column">
    <div align="center">
	  <h5> Open/Close Polling </h5>
	  <br />
        <c:choose>
            <c:when test="${open_qID == 'None'}">
                <c:set var="name" scope="application" value=""/>
            </c:when>
            <c:otherwise>
                <c:set var="name" scope="application" value="${open_qID}"/>
            </c:otherwise>
        </c:choose>
      <form method="post" action="openClosePoll">
        <input type="text" placeholder="Question ID" name="questionID" value="${name}" required/>        <br />
        <input style="color:#2AB441;" type="submit" name="submit" value="OPEN POLLING" />
        <input style="color:#E01010;" type="submit" name="submit" value="CLOSE POLLING" />
        <p> Open Question: ${open_qID} </p>
        
       
      </form> 
	</div>
  </div>
  <div class="one-half column">
    <div align="center">
	  <h5> View Polling Results </h5>
	  <br />
      <form method="post" action="showQuestionResults" target="_blank">
<!--        <input type="text" placeholder="Question ID" name="questionID" />        <br />-->
        
        <select name="questionID" class="dropdown">
        <c:forEach var="item" items="${qID_list}">
            <option value=${item}>${item}</option>
        </c:forEach>
            <option value="all">All Questions</option>
        </select>
        <br>
        
        <input type="submit" name="submit" value="View Results" />
        <input type="submit" name="submit" value="Download Results" />
      </form> 
    </div>
  </div>
</div>

</body>
</html>
