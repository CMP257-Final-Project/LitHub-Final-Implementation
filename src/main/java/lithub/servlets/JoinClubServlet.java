package lithub.servlets;

import lithub.utils.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;




@WebServlet("/LitHubBackend/JoinClubServlet")
public class JoinClubServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String userIdParam = request.getParameter("userId");
        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"User ID missing\"}");
            return;
        }
        int userId;
        try {
            userId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid user ID\"}");
            return;
        }

        String clubIdParam = request.getParameter("clubId");
        if (clubIdParam == null || clubIdParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Club ID missing\"}");
            return;
        }
        int clubId;
        try {
            clubId = Integer.parseInt(clubIdParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid club ID\"}");
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);   

            // Insert into club_members (ignore if already a member)
            String insertSql = "INSERT IGNORE INTO club_members (club_id, user_id, role) VALUES (?, ?, 'member')";
            int rowsInserted;
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, clubId);
                stmt.setInt(2, userId);
                rowsInserted = stmt.executeUpdate();
            }

            if (rowsInserted > 0) {
                String updateSql = "UPDATE clubs SET member_count = member_count + 1 WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setInt(1, clubId);
                    stmt.executeUpdate();
                }
            }

            int newMemberCount = 0;
            String selectSql = "SELECT member_count FROM clubs WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                stmt.setInt(1, clubId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        newMemberCount = rs.getInt("member_count");
                    }
                }
            }

            conn.commit();   

            response.getWriter().write("{\"success\": true, \"memberCount\": " + newMemberCount + "}");

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) {}
            }
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Database error: " + e.getMessage() + "\"}");
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
        }
    }
}



