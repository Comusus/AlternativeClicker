package mypkg;

import java.io.*;
import static java.lang.System.out;
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
    private static final String DB_NAME = "cs5999";
    
    //Table: (username, salt, hash)

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
        
        // IF SESSION DOES NOT EXIST:
        Boolean session_exists = this.joinSession(classID, sessionID);
        if (session_exists) {
        // TODO
            String previous_answer = checkPreviousLogin(classID, sessionID, studentID);
            System.out.println(previous_answer);
            if (!previous_answer.equals("")) {
                request.setAttribute("pressed" + previous_answer, "pressedButton");
            }
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
        } else {
            response.sendRedirect("index.html");
            out.println("<p> No Session Found </p>");    
        }
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
    
    private String checkPreviousLogin(String classID, String sessionID, String studentID) {
        Connection conn = null;
        PreparedStatement stmt;
        
        try {
            //switch to correct db, use prepased
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();

            //find questionID
            String questionID = "";
            String qIDquery = "SELECT qID FROM " + classID + sessionID + "activeQuestion";
            stmt = conn.prepareStatement(qIDquery);
            ResultSet rs = stmt.executeQuery(qIDquery);
            while (rs.next()) {
                questionID = rs.getString("qID");
            }
            if (questionID.equals("")) {
                System.out.println("meep");
            } else {
                System.out.println("meep2");
            }
            if (!questionID.equals("")) {
                 String query = "SELECT ans from " + classID + sessionID 
                    + " WHERE studentID = " + "'" + studentID + "'" 
                    + " AND qID = " + "'" + questionID + "'";
                System.out.println(query);
                stmt = conn.prepareStatement(query);
                ResultSet rs2 = stmt.executeQuery(query);
                while (rs2.next()) {
                    String answer = rs2.getString("ans");
                    System.out.println("answer");
                    return answer;
                }                
            }         
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private boolean joinSession(String classID, String sessionID){
        //TODO
        Connection conn = null;
        PreparedStatement stmt;
        
        try {
            //switch to correct db, use prepased
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            // Two ways to check if Session exists
            // 1. Check if Table CS599920180224 exists
            // 2. Check if CS599920180224 exists in the CS5999
//            
//            String table_name = classID + sessionID;
//            DatabaseMetaData meta = conn.getMetaData();
//            ResultSet rs = meta.getTables(null, null, table_name, null);
//            if (rs.next()) {
//                // Table exists
//                return true;
//            }
//              else {
//                // Table does not exist
//                return false;
//            }
//            
            String query = "SELECT * from " + classID + " WHERE sessionID = " 
                    + "'" +classID+sessionID + "'";
            out.println(query);
            stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery(query);
            return rs.isBeforeFirst(); //false if no data
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
