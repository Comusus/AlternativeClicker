package mypkg;
 
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

 
public class Attendance extends HttpServlet {
    
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    private static final String DBURL = "jdbc:mysql://localhost:3306/cs5999";
    
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
      // Set the response message's MIME type
      

        String classID = request.getParameter("classID");
        
        if(classID != null)                            // if classID submitted
        {    
            // Create a database if not exist.
            Connection conn = null;
            PreparedStatement stmt;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
                stmt = conn.prepareStatement("USE cs5999");
                stmt.execute();
                
                String sql = "create table if not exists " + classID + " (studentName VARCHAR(50))";
                stmt = conn.prepareStatement(sql);
                stmt.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }	

      response.setContentType("text/html; charset=UTF-8");
      // Allocate a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
 
      // Write the response message, in an HTML page
      try {
         out.println("<!DOCTYPE html>");
         out.println("<html><head>");
         out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
         out.println("<title>Show Attendance</title></head>");
 
         // Retrieve the value of the query parameter "username" (from text field)
       
//         BufferedReader br;
//         FileReader fr;
//         fr = new FileReader("attendance.txt");
//         br = new BufferedReader(fr);
//         try {
//             StringBuilder sb = new StringBuilder();
//             String line = br.readLine();
//
//             while (line != null) {
//                 sb.append(line);
//                 sb.append(System.lineSeparator());
//                 line = br.readLine();
//             }
//             String everything = sb.toString();
//             out.println(everything);
//         } finally {
//             br.close();
//         }


         out.println("<a href='form.html'>BACK</a>");
         out.println("</body></html>");



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
      if (message == null) return null;
      int len = message.length();
      StringBuffer result = new StringBuffer(len + 20);
      char aChar;
 
      for (int i = 0; i < len; ++i) {
         aChar = message.charAt(i);
         switch (aChar) {
             case '<': result.append("&lt;"); break;
             case '>': result.append("&gt;"); break;
             case '&': result.append("&amp;"); break;
             case '"': result.append("&quot;"); break;
             default: result.append(aChar);
         }
      }
      return (result.toString());
   }
}