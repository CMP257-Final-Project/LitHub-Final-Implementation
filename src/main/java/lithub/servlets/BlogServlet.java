package lithub.servlets;

import lithub.utils.DatabaseUtil;
import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/blogs")
@MultipartConfig

public class BlogServlet extends HttpServlet {
   private static final ObjectMapper mapper = new ObjectMapper();
   // Handle POST - Create new blog
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws IOException, ServletException {
       response.setContentType("application/json");
       PrintWriter out = response.getWriter();
       Map<String, Object> res = new HashMap<>();
       Connection conn = null;
       PreparedStatement checkStmt = null;
       PreparedStatement insertStmt = null;
       ResultSet rs = null;
       try {
           conn = DatabaseUtil.getConnection();
           String userIdParam = request.getParameter("userId");
           if (userIdParam == null || userIdParam.isEmpty()) {
               res.put("success", false);
               res.put("message", "Not logged in");
               out.print(mapper.writeValueAsString(res));
               return;
           }
           int userId = Integer.parseInt(userIdParam);

           String username = "";
           String checkUserSql = "SELECT username FROM users WHERE id = ?";
           checkStmt = conn.prepareStatement(checkUserSql);
           checkStmt.setInt(1, userId);
           rs = checkStmt.executeQuery();
           if (rs.next()) {
               username = rs.getString("username");
           }
           rs.close();
           checkStmt.close();
           // Get parameters
           String title = request.getParameter("book_title");
           String author = request.getParameter("book_author");
           String content = request.getParameter("blog");
           String ratingRaw = request.getParameter("rating");
           int rating = (ratingRaw != null && !ratingRaw.isEmpty()) ? Integer.parseInt(ratingRaw) : 0;
          
           if (title == null || title.isEmpty() || author == null || author.isEmpty() ||
               content == null || content.isEmpty()) {
               res.put("success", false);
               res.put("message", "Book title, author, and blog content are required");
               out.print(mapper.writeValueAsString(res));
               return;
           }
          
           // Handle file upload (optional)
           String coverImagePath = null;
           Part filePart = request.getPart("photo");
           if (filePart != null && filePart.getSize() > 0) {
               String fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
               String uploadPath = getServletContext().getRealPath("/uploads");
               File uploadDir = new File(uploadPath);
               if (!uploadDir.exists()) uploadDir.mkdirs();
               filePart.write(uploadPath + File.separator + fileName);
               coverImagePath = "uploads/" + fileName;
           }
           // Insert the blog post
           String insertSql = "INSERT INTO blogs (user_id, book_title, book_author, book_cover_image, rating, content) VALUES (?, ?, ?, ?, ?, ?)";
           insertStmt = conn.prepareStatement(insertSql);
           insertStmt.setInt(1, userId);
           insertStmt.setString(2, title);
           insertStmt.setString(3, author);
           insertStmt.setString(4, coverImagePath);
           insertStmt.setInt(5, rating);
           insertStmt.setString(6, content);
           insertStmt.executeUpdate();
           res.put("success", true);
           res.put("message", "Blog posted successfully");
           System.out.println("Blog posted by user: " + username);
       } catch (Exception e) {
           System.out.println("Blog post error: " + e.getMessage());
           e.printStackTrace();
           response.setStatus(500);
           res.put("success", false);
           res.put("message", "Database error: " + e.getMessage());
       } finally {
           try { if (rs != null) rs.close(); } catch (Exception ignored) {}
           try { if (checkStmt != null) checkStmt.close(); } catch (Exception ignored) {}
           try { if (insertStmt != null) insertStmt.close(); } catch (Exception ignored) {}
           try { if (conn != null) conn.close(); } catch (Exception ignored) {}
       }
       out.print(mapper.writeValueAsString(res));
   }
   // Handle GET - Fetch blogs (for dashboard)
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws IOException {
       response.setContentType("application/json");
       PrintWriter out = response.getWriter();
       Map<String, Object> res = new HashMap<>();
       Connection conn = null;
       PreparedStatement stmt = null;
       ResultSet rs = null;
       try {
           conn = DatabaseUtil.getConnection();
           String userIdParam = request.getParameter("userId");
          
           // If userId is provided, fetch specific user's blogs
           // Otherwise, fetch all blogs
           String sql;
           if (userIdParam != null && !userIdParam.isEmpty()) {
               sql = "SELECT b.*, u.username FROM blogs b " +
                     "JOIN users u ON b.user_id = u.id " +
                     "WHERE b.user_id = ? ";
               stmt = conn.prepareStatement(sql);
               stmt.setInt(1, Integer.parseInt(userIdParam));
           } else {
               sql = "SELECT b.*, u.username FROM blogs b " +
                     "JOIN users u ON b.user_id = u.id ";
               stmt = conn.prepareStatement(sql);
           }
          
           rs = stmt.executeQuery();
          
           List<Map<String, Object>> blogs = new ArrayList<>();
           while (rs.next()) {
               Map<String, Object> blog = new HashMap<>();
               blog.put("id", rs.getInt("id"));
               blog.put("userId", rs.getInt("user_id"));
               blog.put("username", rs.getString("username"));
               blog.put("bookTitle", rs.getString("book_title"));
               blog.put("bookAuthor", rs.getString("book_author"));
               blog.put("bookCoverImage", rs.getString("book_cover_image"));
               blog.put("rating", rs.getInt("rating"));
               blog.put("content", rs.getString("content"));
               blogs.add(blog);
           }
          
           res.put("success", true);
           res.put("blogs", blogs);
          
       } catch (Exception e) {
           System.out.println("Fetch blogs error: " + e.getMessage());
           e.printStackTrace();
           res.put("success", false);
           res.put("message", "Server error: " + e.getMessage());
       } finally {
           try { if (rs != null) rs.close(); } catch (Exception ignored) {}
           try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
           try { if (conn != null) conn.close(); } catch (Exception ignored) {}
       }
      
       out.print(mapper.writeValueAsString(res));
   }
}

