<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
  <title>Student Polling Application</title>
  
  <meta name="viewport" content="width=device-width, intial-scale=1">
  
  <!-- CSS
  ?????????????????????????????????????????????????? -->
  <link rel="stylesheet" href="css/normalize.css">
  <link rel="stylesheet" href="css/skeleton.css">
  <link rel="stylesheet" href="css/main.css">  
  
</head>
 
<body>

<%
	if(session.getAttribute("studentID")==null){
		response.sendRedirect("index.html");
	}
%>

<div class="container">
  <div class="row">
    <div align="center">
      <h2>Session ID: ${requestScope["sessionID"]} </h2>
      <h4>Student ID: ${requestScope["studentID"]} </h4>
      <h5>${requestScope["lastAction"]} </h5>
    </div>
  </div>
</div>

<div class="container">
  <div class="row">
    <div align="center">
      <form method="post" action="answer">        
	<input style="color:#0000A0;" class="${requestScope["pressedA"]}" type="submit" name="submit" value="A" />
        <br />
        <input style="color:#0000A0;" class="${requestScope["pressedB"]}" type="submit" name="submit" value="B" />
        <br />
        <input style="color:#0000A0;" class="${requestScope["pressedC"]}" type="submit" name="submit" value="C" />
        <br />
        <input style="color:#0000A0;" class="${requestScope["pressedD"]}" type="submit" name="submit" value="D" />
        <br />
        <input style="color:#0000A0;" class="${requestScope["pressedE"]}" type="submit" name="submit" value="E" />
      </form>
    </div>

  </div>
</div>

</form>
</body>
</html>
