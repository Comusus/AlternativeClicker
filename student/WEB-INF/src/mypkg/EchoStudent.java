package mypkg;

import java.io.*;
import java.io.FileWriter;
import static java.lang.System.out;
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
    private static final String DB_NAME = "cs5999";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String buttonPressed = request.getParameter("submit");
        HttpSession httpSession = request.getSession();
        String studentID = (String) httpSession.getAttribute("studentID");
        String classID = (String) httpSession.getAttribute("classID");
        String sessionID = (String) httpSession.getAttribute("sessionID");
        
        // TODO
        if (buttonPressed.equals("A")){
            Boolean openquestion = this.hasOpenQuestion(classID, sessionID);
            if (!openquestion) {
                out.println("No Open Question");
            } else {
                String qID = this.getOpenQuestionID(classID, sessionID);
                if (!qID.isEmpty()) {
                    if (submitAnswer(classID, sessionID, qID, studentID, "A")) {
                        out.println("Submitted A");
                    } else {
                        out.println("Failed to Submit");
                    }   
                }
            }       
        }
        else if (buttonPressed.equals("B")){
            Boolean openquestion = this.hasOpenQuestion(classID, sessionID);
            if (!openquestion) {
                out.println("No Open Question");
            } else {
                String qID = this.getOpenQuestionID(classID, sessionID);
                if (!qID.isEmpty()) {
                    if (submitAnswer(classID, sessionID, qID, studentID, "B")) {
                        out.println("Submitted B");
                    } else {
                        out.println("Failed to Submit");
                    }   
                }
            }
        }
        else if (buttonPressed.equals("C")){
            Boolean openquestion = this.hasOpenQuestion(classID, sessionID);
            if (!openquestion) {
                out.println("No Open Question");
            } else {
                String qID = this.getOpenQuestionID(classID, sessionID);
                if (!qID.isEmpty()) {
                    if (submitAnswer(classID, sessionID, qID, studentID, "C")) {
                        out.println("Submitted C");
                    } else {
                        out.println("Failed to Submit");
                    }   
                }
            }
        }
        else if (buttonPressed.equals("D")){
            Boolean openquestion = this.hasOpenQuestion(classID, sessionID);
            if (!openquestion) {
                out.println("No Open Question");
            } else {
                String qID = this.getOpenQuestionID(classID, sessionID);
                if (!qID.isEmpty()) {
                    if (submitAnswer(classID, sessionID, qID, studentID, "D")) {
                        out.println("Submitted D");
                    } else {
                        out.println("Failed to Submit");
                    }   
                }
            }
        }
        else if (buttonPressed.equals("E")){
            Boolean openquestion = this.hasOpenQuestion(classID, sessionID);
            if (!openquestion) {
                out.println("No Open Question");
            } else {
                String qID = this.getOpenQuestionID(classID, sessionID);
                if (!qID.isEmpty()) {
                    if (submitAnswer(classID, sessionID, qID, studentID, "E")) {
                        out.println("Submitted E");
                    } else {
                        out.println("Failed to Submit");
                    }   
                }
            }
        }
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
        Connection conn = null;
        PreparedStatement stmt;
        
        try {
            //switch to correct db, use prepased
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            //+-----------+-------------+------+-----+---------+-------+
            //| Field     | Type        | Null | Key | Default | Extra |
            //+-----------+-------------+------+-----+---------+-------+
            //| sessionID | varchar(50) | YES  | UNI | NULL    |       |
            //| time      | varchar(20) | YES  |     | NULL    |       |
            //+-----------+-------------+------+-----+---------+-------+
            //2 rows in set (0.00 sec)
            //
            //mysql> select * from CS5999;
            //+----------------+------------+
            //| sessionID      | time       |
            //+----------------+------------+
            //| CS599920180224 | 2018-02-24 |
            //+----------------+------------+
            
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
            String query = "SELECT * from " + classID + " WHERE sessionID =" + sessionID;
            stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery(query);
            if (rs.isBeforeFirst()) {    
                //no data
                return true;
            } else {
                return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean hasOpenQuestion(String classID, String sessionID){
        //TODO
        Connection conn = null;
        PreparedStatement stmt;
        try {
            //switch to correct db, use prepased
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            // https://stackoverflow.com/questions/17950245/difference-between-createstatement-and-preparedstatement-in-jdbc?lq=1
            //| CS599920180224activeQuestion
            // check if there is an open question
            String query = "SELECT qID FROM " + classID + sessionID + "activeQuestion";
            out.println(query);
            stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery(query);
            if (rs.isBeforeFirst()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private String getOpenQuestionID(String classID, String sessionID){
        //TODO
        Connection conn = null;
        PreparedStatement stmt;
        try {
            //switch to correct db, use prepased
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            // https://stackoverflow.com/questions/17950245/difference-between-createstatement-and-preparedstatement-in-jdbc?lq=1
            //| CS599920180224activeQuestion
            // check if there is an open question
            String query = "SELECT qID FROM " + classID + sessionID + "activeQuestion";
            stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String question = rs.getString("qID");
                return question;
            }
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private boolean submitAnswer(String classID, String sessionID, String questionID, String studentID, String answer){
        //TODO
        Connection conn = null;
        PreparedStatement stmt;
        try {
            //switch to correct db, use prepased
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            // format: CS599920180224 studentID, qID, ans
            // (studentID, qID) is unique from looking at Ming's code
            String insertQuery = "INSERT INTO " + classID + sessionID 
                    + "(studentID, qID, ans) VALUES (?,?,?) "
                    + "ON DUPLICATE KEY UPDATE ans=?";

            stmt = conn.prepareStatement(insertQuery);
            stmt.setString(1, studentID);
            stmt.setString(2, questionID);
            stmt.setString(3, answer);
            stmt.setString(4, answer);
            // execute insert SQL stetement
            stmt.executeUpdate();
            return true;
        
        } catch (Exception e) {
            e.printStackTrace();
        }
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
