/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mypkg;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class OpenClosePoll extends HttpServlet{
    
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "cs5999";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        String actionType = request.getParameter("submit");
        String questionID = request.getParameter("questionID");
        HttpSession httpSession = request.getSession();
        String username = (String) httpSession.getAttribute("username");
        String sessionID = (String) httpSession.getAttribute("sessionID");
        
        PrintWriter out = response.getWriter();
        int success = 0; //1 if all notmal, -1 if operation failed
        
        String statusMsg;
        if (actionType.equals("OPEN POLLING")){
            success = this.openPoll(sessionID, questionID);
            if (success == -1)
                statusMsg = "Failed to open poll (try again later)";
            else
                statusMsg = "Successfully opened poll";
        }
        else if (actionType.equals("CLOSE POLLING")){
            success = this.closePoll(sessionID, questionID);
            if (success == -1)
                statusMsg = "Failed to close poll (make sure question ID exists)";
            else
                statusMsg = "Successfully closed poll";
        }
        else{
            statusMsg = "Something unexpected happened. Try again.";
        }
        
        //TODO: present next web page here, database configured correctly.
        // Forward request to attendance.jsp
        request.setAttribute("sessionID", sessionID);
        request.setAttribute("statusMsg", statusMsg);
        RequestDispatcher view = request.getRequestDispatcher("attendance.jsp");      
        view.forward(request, response);
    }
    
    // Redirect POST request to GET request.
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }

    private int openPoll(String sessionID, String questionID){
        //return 1 if success, -1 if failed
        Connection conn = null;
        PreparedStatement stmt;
        try {
            //switch to correct db, use prepase statement to avoid injection attack
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            if (sessionID.contains(";")) //no session name should contain ";", prevent injection attack.
                return -1;

            String insertTableSQL = "INSERT INTO " + sessionID+"activeQuestion"
		+ " (qID) VALUES"
		+ " (?)"
                + " ON DUPLICATE KEY UPDATE qID=?";
            stmt = conn.prepareStatement(insertTableSQL);
            stmt.setString(1, questionID);
            stmt.setString(2, questionID);
            System.out.println(insertTableSQL);
            stmt.executeUpdate();
            return 1;
            
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }  
    
    private int closePoll(String sessionID, String questionID){
        //return 1 if success, -1 if failed
        Connection conn = null;
        PreparedStatement stmt;
        try {
            //switch to correct db, use prepase statement to avoid injection attack
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            if (sessionID.contains(";")) //no session name should contain ";", prevent injection attack.
                return -1;
            
            //If question ID does not exist, return -1
            String existsRowSQL = "SELECT * FROM " + sessionID+"activeQuestion" + " WHERE qID=?";
            stmt = conn.prepareStatement(existsRowSQL);
            stmt.setString(1, questionID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()){
                return -1;
            }
            
            
            String deleteRowSQL = "DELETE FROM " + sessionID+"activeQuestion"
		+ " WHERE qID=?";
            stmt = conn.prepareStatement(deleteRowSQL);
            stmt.setString(1, questionID);
            System.out.println(deleteRowSQL);
            stmt.executeUpdate();
            return 1;
            
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }    
}
