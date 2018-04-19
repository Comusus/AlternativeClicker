package mypkg;

import java.io.*;
import java.io.FileWriter;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EchoStudent extends HttpServlet {

    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cs5999";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String buttonPressed = request.getParameter("submit");
        HttpSession httpSession = request.getSession();
        String studentID = (String) httpSession.getAttribute("studentID");
        String sessionID = (String) httpSession.getAttribute("sessionID");
        
        // TODO
        // Actual polling logic!!!
        // TODO
        
        // Forward request to studentapp.jsp
        request.setAttribute("sessionID", sessionID);
        request.setAttribute("studentID", studentID);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();
	String currTime = dtf.format(now);
        request.setAttribute("lastAction", "Submitted " + buttonPressed + " at " + currTime);
        RequestDispatcher view = request.getRequestDispatcher("studentapp.jsp");      
        view.forward(request, response);
    }

    // Redirect POST request to GET request.
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }
    
    private boolean joinSession(String classID, String sessionID, String studentID){
        //TODO
        return false;
    }
    
    private boolean hasOpenQuestion(String classID, String sessionID){
        //TODO
        return false;
    }
    
    private String getOpenQuestionID(String classID, String sessionID){
        //TODO
        return null;
    }
    
    private boolean submitAnswer(String classID, String sessionID, String questionID, String studentID, String answer){
        //TODO
        return false;
    }

    // Filter the string for special HTML characters to prevent
    // command injection attack
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
