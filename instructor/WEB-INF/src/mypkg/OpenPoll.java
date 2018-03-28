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

/**
 *
 * @author geniusming
 */
public class OpenPoll extends HttpServlet{
    
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "cs5999";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        String qestionID = request.getParameter("quesionID");
        String sessionID = request.getParameter("sessionID");
        
        //connect to db, set table correspondingto current session to writable
        Connection conn = null;
        PreparedStatement stmt;
        try {
            //switch to correct db, use prepase statement to avoid injection attack
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            if (sessionID.contains(";")) //no session name should contain ";", prevent injection attack.
                return;
            String queryStr = "RENAME TABLE " + sessionID + "_locked TO " + sessionID;
            stmt = conn.prepareStatement(queryStr);
            stmt.execute();

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
