package mypkg;

import java.io.*;
import java.io.FileWriter;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

public class EchoStudent extends HttpServlet {

    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    private static final String DBURL = "jdbc:mysql://localhost:3306/cs5999";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Set the response message's MIME type
        response.setContentType("text/html; charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        // Write the response message, in an HTML page
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html><head>");
            out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
            out.println("<title>Echo Student</title></head>");
            out.println("<body><h2>You have entered:</h2>");

            // Retrieve the value of the query parameter "username" (from text field)
            String username = request.getParameter("username");
            String username2 = request.getParameter("username2");
            String classID = request.getParameter("classID");
            // Get null if the parameter is missing from query string.
            // Get empty string or string of white spaces if user did not fill in
            if (username == null
                    || (username = htmlFilter(username.trim())).length() == 0) {
                out.println("<p>First Name: MISSING</p>");
            } else {
                out.println("<p>First Name: " + username + "</p>");
            }

            if (username2 == null
                    || (username2 = htmlFilter(username2.trim())).length() == 0) {
                out.println("<p>Last Name: MISSING</p>");
            } else {
                out.println("<p>Last Name: " + username2 + "</p>");
            }

            System.out.println("Test...");
            // Hyperlink "BACK" to input page
            out.println("<a href='form.html'>BACK</a>");
            out.println("</body></html>");

            // Write to database:
            Connection conn = null;
            PreparedStatement stmt;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
                stmt = conn.prepareStatement("USE cs5999");
                stmt.execute();

                String sql = "insert into " + classID + " (studentName) values ('" + username + " " + username2 + "')";
                stmt = conn.prepareStatement(sql);
                stmt.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Write to the text file
//         FileWriter fw;
//         BufferedWriter bw;
//         System.out.println("Writing file...");
//         try {
//            fw = new FileWriter("attendance.txt", true);
//            bw = new BufferedWriter(fw);
//            bw.write(username + ' ' + username2);
//            bw.write('\n');
//            bw.flush();
//            bw.close();
//         } catch (IOException e) {
//            e.printStackTrace();
//         } 
//
//         System.out.println("Finished file...Saved to: "+new File("attendance.txt").getAbsolutePath());
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
}
