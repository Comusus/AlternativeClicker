
package mypkg;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

/**
 *
 * @author geniusming
 */
public class showQuestionResults extends HttpServlet{
    
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "cs5999";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        String questionID = request.getParameter("quesionID");
        String sessionID = request.getParameter("sessionID");
        
        //connect to db, set table correspondingto current session to writable
        Connection conn = null;
        PreparedStatement stmt;
        PrintWriter out = response.getWriter();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            Statement st = conn.createStatement();
            if (sessionID.contains(";")) //no session name should contain ";", prevent injection attack.
                return;
            String queryStr = "SELECT * FROM " + sessionID
                + "WHERE qID=?";
            
            stmt = conn.prepareStatement(queryStr);
            stmt.setString(1, questionID);
            // execute SQL query
            ResultSet rs = stmt.executeQuery(queryStr);

            // iterate through the java resultset
            while (rs.next()) {
                String studentID = rs.getString("studentID");
                String ans = rs.getString("ans");
                // print the results
                out.println("<p>"+studentID+":"+ans+"</p>");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Redirect POST request to GET request.
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }
}
