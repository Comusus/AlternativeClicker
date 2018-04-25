package mypkg;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Login extends HttpServlet {

    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cs5999";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Set the response message's MIME type
        response.setContentType("text/html; charset=UTF-8");
        String studentID = request.getParameter("studentID");
        String password = request.getParameter("studentPassword");
        String classID = request.getParameter("classID");
        String sessionID = request.getParameter("sessionID");
        
        //TODO: Check to make sure inputs are valid (very similar to Attendance.java)
        // MAKE SURE
        // TO DO THIS
        
        // Validate username and password
        PrintWriter out = response.getWriter();
        boolean isValidPassword = isValidPassword(classID, studentID, password);
        if (!isValidPassword){
            try{
                out.println("Invalid password. Please try again");
            } finally {
                out.close();  // Always close the output writer
            }
            return;
        }
        else{
            HttpSession session = request.getSession();
            session.setAttribute("studentID", studentID);
            session.setAttribute("sessionID", sessionID);
            session.setAttribute("classID", classID);
        }
        
        response.setContentType("text/html; charset=UTF-8");
        
        // TODO
        // Logging in logic
        // TODO
        
        // Forward request to studentapp.jsp
        request.setAttribute("sessionID", sessionID);
        request.setAttribute("studentID", studentID);
        request.setAttribute("classID", classID);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();
	String currTime = dtf.format(now);
        request.setAttribute("lastAction", "Logged in at " + currTime);
        RequestDispatcher view = request.getRequestDispatcher("studentapp.jsp");      
        view.forward(request, response);
    }

    /* Redirect POST request to GET request. */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }
    
    /* Validates password is correct */
    private boolean isValidPassword(String classID, String studentID, String password){
        // TODO
        return true;
    }
    
    /* Filter the string for special HTML characters to prevent
     * command injection attack */
    private static String htmlFilter(String message) {
        if (message == null) {
            return null;
        }
        int len = message.length();
        StringBuffer result = new StringBuffer(len + 20);
        char aChar;

        for (int i = 0; i < len; ++i) {
            aChar = message.charAt(i);
            switch (aChar) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                default:
                    result.append(aChar);
            }
        }
        return (result.toString());
    } 
}
