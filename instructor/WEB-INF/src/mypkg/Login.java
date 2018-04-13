package mypkg;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

public class Login extends HttpServlet {

    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "cs5999";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Set the response message's MIME type
        response.setContentType("text/html; charset=UTF-8");
        String classID = request.getParameter("classID");
        String classDate = request.getParameter("classDate");
        
        //TODO: Check to make sure inputs are valid (very similar to Attendance.java
        String sessionID = this.getSessionID(classID, classDate);
        
        //create table for the class if not exist
        createClassTableIfNotExist(classID);
        insertSessionRecordIfNotExist(classID, sessionID, classDate);
        //create table for the session if not exit
        createSessionTableIfNotExist(sessionID);
        createSessionActiveQuestionTableIfNotExist(sessionID);
        
        String actionOpenCloseQuestion = "openClosePoll";
        String actionViewResults = "showQuestionResults";
        
        response.setContentType("text/html; charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        // Write the response message, in an HTML page
        try {
            out.println("<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>\n" +
            "  <title>Instructor Attendance Tracker</title>\n" +
            "  \n" +
            "  <!-- CSS\n" +
            "  ?????????????????????????????????????????????????? -->\n" +
            "  <link rel=\"stylesheet\" href=\"css/normalize.css\">\n" +
            "  <link rel=\"stylesheet\" href=\"css/skeleton.css\">\n" +
            "  <link rel=\"stylesheet\" href=\"css/main.css\">  \n" +
            "  \n" +
            "</head>\n" +
            " \n" +
            "<body>\n" +
            "\n" +
            "<div class=\"container\">\n" +
            "  <div class=\"row\">\n" +
            "    <div align=\"center\">\n" +
            "      <h2>Instructor Attendance Tracker</h2>\n" +
            "	  <h4>Session ID: " + sessionID + "</h4>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"row\">\n" +
            "  <div class=\"one-half column\">\n" +
            "    <div align=\"center\">\n" +
            "	  <h5> Open/Close Polling </h5>\n" +
            "	  <br />\n" +
            "      <form method=\"post\" action=\"" + actionOpenCloseQuestion + "\">\n" +
            "        <input type=\"text\" placeholder=\"Question ID\" name=\"questionID\" />\n" +
            "        <input type=\"hidden\" name=\"sessionID\" value=\"" + sessionID + "\" /> " +
            "        <br />\n" +
            "        <input style=\"color:#2AB441;\" type=\"submit\" name=\"submit\" value=\"OPEN POLLING\" />\n" +
            "        <input style=\"color:#E01010;\" type=\"submit\" name=\"submit\" value=\"CLOSE POLLING\" />\n" +
            "      </form> \n" +
            "	</div>\n" +
            "  </div>\n" +
            "  <div class=\"one-half column\">\n" +
            "    <div align=\"center\">\n" +
            "	  <h5> View Polling Results </h5>\n" +
            "	  <br />\n" +
            "      <form method=\"post\" action=\"" + actionViewResults + "\">\n" +
            "        <input type=\"text\" placeholder=\"Question ID\" name=\"questionID\" />\n" +
            "        <input type=\"hidden\" name=\"sessionID\" value=\"" + sessionID + "\" /> " +
            "        <br />\n" +
            "        <input type=\"submit\" name=\"submit\" value=\"View Results\" />\n" +
            "        <input type=\"submit\" name=\"submit\" value=\"Download Results\" />\n" +
            "      </form> \n" +
            "    </div>\n" +
            "  </div>\n" +
            "</div>\n" +
            "\n" +
            "</body>\n" +
            "</html>");
            

        } finally {
            out.close();  // Always close the output writer
        }
    }

    // Redirect POST request to GET request.
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
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
    
    /* Returns the sessionID for a given classID and date. This is a
     * Deterministic hash. If this session has never been accessed before, a
     * new session is created in the database. Otherwise, we use the existing
     * entry in the database */
    private String getSessionID(String classID, String date){
        //TODO: could design more complex way to name a session here
        return (classID + date).replace("-", "");
    }    
    
    private void createClassTableIfNotExist(String classTableName){
        //check if table exists
        Connection conn = null;
        PreparedStatement stmt;
        try {
            //switch to correct db, use prepased
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            if (classTableName.contains(";")) //no class name should contain ";", prevent injection attack.
                return;
            stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + classTableName + " (sessionID VARCHAR(50), time VARCHAR(20), UNIQUE(sessionID))");
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createSessionTableIfNotExist(String sessionTableName){
        //check if table exists
        Connection conn = null;
        PreparedStatement stmt;
        try {
            //switch to correct db, use prepased
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            if (sessionTableName.contains(";")) //no session name should contain ";", prevent injection attack.
                return;
            stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + sessionTableName + " (studentID VARCHAR(50), qID VARCHAR(20), ans VARCHAR(100), UNIQUE(studentID, qID))");
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
        private void createSessionActiveQuestionTableIfNotExist(String sessionID){
        //check if table exists
        Connection conn = null;
        PreparedStatement stmt;
        try {
            //switch to correct db, use prepased
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            if (sessionID.contains(";")) //no session name should contain ";", prevent injection attack.
                return;
            stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + sessionID + "activeQuestion" + " (qID VARCHAR(20), UNIQUE(qID))");
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void insertSessionRecordIfNotExist(String classID, String sessionID, String time){
        //must already have the table specified by classID
        Connection conn = null;
        PreparedStatement stmt;
        try {
            //switch to correct db, use prepase statement to avoid injection attack
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            if (classID.contains(";")) //no class name should contain ";", prevent injection attack.
                return;
            String insertTableSQL = "INSERT INTO " + classID
		+ "(sessionID, time) VALUES"
		+ "(?,?)"
                + "ON DUPLICATE KEY UPDATE time=?";
            stmt = conn.prepareStatement(insertTableSQL);
            stmt.setString(1, sessionID);
            stmt.setString(2, time);
            stmt.setString(3, time);
            // execute insert SQL stetement
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
