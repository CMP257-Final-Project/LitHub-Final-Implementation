package lithub.servlets;

import lithub.utils.DatabaseUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {


    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Map<String, Object> res = new HashMap<>();

        String emailOrUsername = request.getParameter("email");
        String password = request.getParameter("password");

        if (emailOrUsername == null || password == null ||
                emailOrUsername.isEmpty() || password.isEmpty()) {

            res.put("success", false);
            res.put("message", "Email/username and password are required");
            out.print(mapper.writeValueAsString(res));
            return;
        }

        System.out.println("Login attempt: " + emailOrUsername);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            String sql = "SELECT id, username, password FROM users WHERE email = ? OR username = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, emailOrUsername);
            stmt.setString(2, emailOrUsername);

            rs = stmt.executeQuery();

            if (!rs.next()) {
                res.put("success", false);
                res.put("message", "User not found");
                out.print(mapper.writeValueAsString(res));
                return;
            }

            String dbPassword = rs.getString("password");

            if (!dbPassword.equals(password)) {
                res.put("success", false);
                res.put("message", "Incorrect password");
                out.print(mapper.writeValueAsString(res));
                return;
            }

            // SUCCESS
            String token = UUID.randomUUID().toString();

            res.put("success", true);
            res.put("token", token);
            res.put("userId", rs.getInt("id"));
            res.put("username", rs.getString("username"));

            System.out.println("Login SUCCESS for: " + emailOrUsername);

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();

            res.put("success", false);
            res.put("message", "Server error");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }

        out.print(mapper.writeValueAsString(res));
    }
}

