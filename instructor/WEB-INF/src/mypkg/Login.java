package mypkg;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;

public class Login extends HttpServlet {

    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "cs5999";
    
    //NOTE: the table below should already be in the DB
    //instructorPasswordTable contains the different salt for each user, and the encrypted password: hash = md5(salt+password)
    private static final String INSTR_SALT_HASH_TABLE_NAME = "instructorSaltHash"; //Table: (username, salt, hash)

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Set the response message's MIME type
        response.setContentType("text/html; charset=UTF-8");
        String classID = request.getParameter("classID");
        String classDate = request.getParameter("classDate");
        String username = request.getParameter("instructorUsername");
        String password = request.getParameter("instructorPassword");
        String sessionID = this.getSessionID(classID, classDate);
        
        //TODO: Check to make sure inputs are valid (very similar to Attendance.java)
        // MAKE SURE
        // TO DO THIS
        
        // Validate username and password
        PrintWriter out = response.getWriter();
        boolean isValidPassword = isValidPassword(classID, username, password);
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
            session.setAttribute("username", username);
            session.setAttribute("sessionID", sessionID);
        }
        
        //create table for the class if not exist
        createClassTableIfNotExist(classID);
        insertSessionRecordIfNotExist(classID, sessionID, classDate);
        
        //create table for the session if not exist
        createSessionTableIfNotExist(sessionID);
        createSessionActiveQuestionTableIfNotExist(sessionID);
        
        // Forward request to attendance.jsp
        List<String> questions = returnQuestions(sessionID);
        System.out.println(questions);
        request.setAttribute("qID_list", questions);
        request.setAttribute("sessionID", sessionID);
        request.setAttribute("statusMsg", "Successfully Logged In");
        RequestDispatcher view = request.getRequestDispatcher("attendance.jsp");      
        view.forward(request, response);
    }

    // Redirect POST request to GET request.
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
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
    
    private boolean isValidPassword(String classID, String username, String password){
        Connection conn = null;
        PreparedStatement stmt;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            String queryStr = "SELECT * FROM " + INSTR_SALT_HASH_TABLE_NAME + " WHERE username = ?";
            stmt = conn.prepareStatement(queryStr);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // iterate through the java resultset
            rs.next();
            String salt = rs.getString("salt");
            String hash = rs.getString("hash");
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((salt+password).getBytes());
            byte[] digest = md.digest();
            String calculatedHash = DatatypeConverter.printHexBinary(digest).toLowerCase();

//            System.out.println("stored salt:" + salt);
//            System.out.println("stored hash:" + hash);
//            System.out.println("entered password:" + password);
//            System.out.println("calculated hash:" + calculatedHash);
            
            return calculatedHash.equals(hash);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private List<String> returnQuestions(String sessionID) {
        Connection conn = null;
        PreparedStatement stmt;
        
        List<String> qID_list = new ArrayList<>();
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            String queryStr = "SELECT DISTINCT qID FROM " + sessionID;
            stmt = conn.prepareStatement(queryStr);
            ResultSet rs = stmt.executeQuery();
            
            // iterate through the java resultset
            while(rs.next()){
                qID_list.add(rs.getString("qID"));
            }
            return qID_list;
        
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        return qID_list;
        
    }
}
