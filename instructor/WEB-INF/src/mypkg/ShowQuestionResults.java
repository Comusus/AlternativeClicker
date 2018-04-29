package mypkg;

import java.io.*;
import java.awt.BasicStroke;
import java.awt.Color;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;


public class ShowQuestionResults extends HttpServlet{
    
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "cs5999";
    
    //private Dictionary pollResult = new Hashtable();
    private Map<String, Integer> pollStats = new HashMap<String, Integer>();
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        String questionID = request.getParameter("questionID");
        String actionType = request.getParameter("submit");
        HttpSession httpSession = request.getSession();
        String username = (String) httpSession.getAttribute("username");
        String sessionID = (String) httpSession.getAttribute("sessionID");
        
        int success = 0; //1 if all notmal, -1 if operation failed
        
        if (actionType.equals("View Results")){
            success = this.viewResults(sessionID, questionID, request, response);
            if (success == -1){
                PrintWriter out = response.getWriter();
                out.println("Failed to view result for "+ questionID);
            }
        }
        else if (actionType.equals("Download Results")){
            PrintWriter out = response.getWriter();
            success = this.downloadResults(sessionID, questionID);
            if (success == -1){
                out.println("Failed to download result.");
            }
            else
                out.println("CSV File is created successfully.");
        }
        
        //TODO: show result in a web page here
    }
    
    // Redirect POST request to GET request.
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }
    
    private int viewResults (String sessionID, String questionID, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        //connect to db, set table correspondingto current session to writable
        Connection conn = null;
        PreparedStatement stmt;
        //PrintWriter out = response.getWriter();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            if (sessionID.contains(";")) //no session name should contain ";", prevent injection attack.
                return -1;
            String queryStr = "SELECT * FROM " + sessionID + " WHERE qID = ?";
            System.out.println(queryStr);
            System.out.println(questionID);
            stmt = conn.prepareStatement(queryStr);
            stmt.setString(1, questionID);
            
            // execute SQL query
            ResultSet rs = stmt.executeQuery();

            // iterate through the java resultset
            while (rs.next()) {
                //String studentID = rs.getString("studentID");
                String ans = rs.getString("ans");
                // print the results
                //pollResult.put(studentID, ans);
                //out.println("<p>"+studentID+":"+ans+"</p>");
                
                if (pollStats.get(ans)==null){
                    pollStats.put(ans, 0);
                }
                pollStats.put(ans, pollStats.get(ans)+1);
            }

            OutputStream streamOut = response.getOutputStream();
            DefaultPieDataset myServletPieChart = new DefaultPieDataset();
            
            for ( String key : pollStats.keySet() ) {
                myServletPieChart.setValue(key, pollStats.get(key));
            }
       
            JFreeChart mychart = ChartFactory.createPieChart(questionID + " Poll Result",myServletPieChart,true,true,false);
            PiePlot ColorConfigurator = (PiePlot) mychart.getPlot();
            ColorConfigurator.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1}"));
            ColorConfigurator.setLabelBackgroundPaint(new Color(220, 220, 220));  
            response.setContentType("image/png"); /* Set the HTTP Response Type */
            /* Send a big chart back to the browser */
            ChartUtilities.writeChartAsPNG(streamOut, mychart, 640, 480);/* Write the data to the output stream */
            
            pollStats.clear();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    private int downloadResults(String sessionID, String questionID){
        //TODO: send result as file
        Connection conn = null;
        PreparedStatement stmt;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.prepareStatement("USE " + DB_NAME);
            stmt.execute();
            
            if (sessionID.contains(";")) //no session name should contain ";", prevent injection attack.
                return -1;
            String queryStr = "SELECT * FROM " + sessionID + " WHERE qID = ?";
            stmt = conn.prepareStatement(queryStr);
            stmt.setString(1, questionID);
            
            // execute SQL query
            ResultSet rs = stmt.executeQuery();
            
            FileWriter fw = new FileWriter(sessionID+".csv");
            fw.append("studentID");
            fw.append(',');
            fw.append("qID");
            fw.append(',');
            fw.append("ans");
            fw.append('\n');
            
            // iterate through the java resultset
            while (rs.next()) {
                String studentID = rs.getString("studentID");
                String qID = rs.getString("qID");
                String ans = rs.getString("ans");
                // print the results
                fw.append(studentID);
                fw.append(',');
                fw.append(qID);
                fw.append(',');
                fw.append(ans);
                fw.append('\n');
            }
            fw.flush();
            fw.close();
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
