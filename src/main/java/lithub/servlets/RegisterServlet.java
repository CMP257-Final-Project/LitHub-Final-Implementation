package lithub.servlets;

import lithub.utils.DatabaseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> res = new HashMap<>();

     // ── Read parameters ───────────────────────────────────────────────
        String username = request.getParameter("name");   // "name" field = username
        String email    = request.getParameter("email");  // "email" field = email
        String password = request.getParameter("password");
        String dob      = request.getParameter("dob");


        // ── Basic null check ──────────────────────────────────────────────
        if (username == null || email == null || password == null || dob == null ||
        	    username.isEmpty() || email.isEmpty() || password.isEmpty() || dob.isEmpty()) {
        	    res.put("success", false);
        	    res.put("message", "All fields are required");
        	    out.print(mapper.writeValueAsString(res));
        	    return;
        	}
       
        try (Connection conn = DatabaseUtil.getConnection()) {

            // ── Check for duplicate email or username ─────────────────────
        	String checkSql = "SELECT id FROM users WHERE email = ? OR username = ?";
        	try (PreparedStatement check = conn.prepareStatement(checkSql)) {
        	    check.setString(1, email);
        	    check.setString(2, username);
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    res.put("success", false);
                    res.put("message", "Email or username already in use");
                    out.print(mapper.writeValueAsString(res));
                    return;
                }
            }

            // ── Insert new user ───────────────────────────────────────────
        	String insertSql = "INSERT INTO users (username, email, password, dob) VALUES (?, ?, ?, ?)";
        	try (PreparedStatement insert = conn.prepareStatement(insertSql,
        	        Statement.RETURN_GENERATED_KEYS)) {
        	    insert.setString(1, username);  // from "name" field
        	    insert.setString(2, email);     // from "email" field
        	    insert.setString(3, password);
        	    insert.setString(4, dob);
        	    insert.executeUpdate();

        	    ResultSet keys = insert.getGeneratedKeys();
        	    int newUserId = keys.next() ? keys.getInt(1) : -1;

        	    String token = java.util.UUID.randomUUID().toString();

        	    res.put("success",  true);
        	    res.put("token",    token);
        	    res.put("userId",   newUserId);
        	    res.put("username", username);
        	}

        } catch (SQLException e) {
            e.printStackTrace();
            res.put("success", false);
            res.put("message", "Database error: " + e.getMessage());
        }

        out.print(mapper.writeValueAsString(res));
    }
}