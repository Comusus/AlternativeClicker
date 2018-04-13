
package mypkg;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class ShowQuestionResults extends HttpServlet{
    
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "cs5999";
    
    private Dictionary pollResult = new Hashtable();
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        String questionID = request.getParameter("questionID");
        String sessionID = request.getParameter("sessionID");
        String actionType = request.getParameter("submit");
        
        PrintWriter out = response.getWriter();
        int success = 0; //1 if all notmal, -1 if operation failed
        
        if (actionType.equals("View Results")){
            success = this.viewResults(sessionID, questionID, response);
            if (success == -1)
                out.println("Failed to view result for "+ questionID);
        }
        else if (actionType.equals("Download Results")){
            success = this.downloadResults(sessionID, questionID);
            if (success == -1)
                out.println("Failed to download result for " + questionID);
        }
        
        //TODO: show result in a web page here
    }
    
    // Redirect POST request to GET request.
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }
    
    private int viewResults (String sessionID, String questionID, HttpServletResponse response)
            throws IOException, ServletException {
        //connect to db, set table correspondingto current session to writable
        Connection conn = null;
        PreparedStatement stmt;
        PrintWriter out = response.getWriter();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            if (sessionID.contains(";")) //no session name should contain ";", prevent injection attack.
                return -1;
            String queryStr = "SELECT * FROM " + sessionID + " WHERE qID = ?";
            System.out.println(queryStr);
            stmt = conn.prepareStatement(queryStr);
            stmt.setString(1, questionID);
            
            // execute SQL query
            ResultSet rs = stmt.executeQuery();

            // iterate through the java resultset
            while (rs.next()) {
                String studentID = rs.getString("studentID");
                String ans = rs.getString("ans");
                // print the results
                pollResult.put(studentID, ans);
                out.println("<p>"+studentID+":"+ans+"</p>");
            }
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    private int downloadResults(String sessionID, String questionID){
        //TODO: send result as file
        
        
        return -1;
    }
}
