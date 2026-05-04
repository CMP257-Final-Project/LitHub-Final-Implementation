package lithub.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithub.utils.DatabaseUtil;
import lithub.models.UserBook;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;


@WebServlet("/api/dashboard")
public class DashboardServlet extends HttpServlet {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		//int userId = 1; //hardcoded for testing
		
		String userIdParam = req.getParameter("userId");
		if (userIdParam == null) {
		    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    resp.getWriter().write("{\"error\":\"Not logged in\"}");
		    return;
		}
		int userId = Integer.parseInt(userIdParam);

//		String sql = """
//		        SELECT
//		            ub.id           AS user_book_id,
//		            ub.user_id,
//		            ub.book_id,
//		            ub.status,
//		            COALESCE(ub.rating, 0) AS rating,
//		            DATE_FORMAT(ub.added_at,  '%Y-%m-%d') AS added_at,
//		            DATE_FORMAT(ub.read_date, '%Y-%m-%d') AS read_date,
//		            b.title,
//		            b.author,
//		            b.cover_url,
//		            b.page_count
//		        FROM user_books ub
//		        JOIN books b ON b.id = ub.book_id
//		        WHERE ub.user_id = ?
//		        ORDER BY ub.added_at DESC
//		        """;
		
		
		
		String sql = """
			    SELECT
			        ub.id           AS user_book_id,
			        ub.user_id,
			        ub.book_id,
			        ub.status,
			        COALESCE(ub.rating, 0) AS rating,
			        DATE_FORMAT(ub.added_at,  '%Y-%m-%d') AS added_at,
			        DATE_FORMAT(ub.read_date, '%Y-%m-%d') AS read_date,
			        b.title,
			        b.author,
			        b.cover_url,
			        b.page_count
			    FROM user_books ub
			    INNER JOIN books b ON CAST(ub.book_id AS CHAR) = CAST(b.id AS CHAR)
			    WHERE ub.user_id = ?
			    ORDER BY ub.added_at DESC
			    """;

		List<UserBook> wishlist  = new ArrayList<>();
		List<UserBook> readBooks = new ArrayList<>();
		String username = "";         // declared ONCE here
		int totalPages  = 0;

		try (Connection conn = DatabaseUtil.getConnection()) {

		    // ── Fetch username first ─────────────────────────────────────────
		    String usernameSql = "SELECT username FROM users WHERE id = ?";
		    try (PreparedStatement ps2 = conn.prepareStatement(usernameSql)) {
		        ps2.setInt(1, userId);
		        ResultSet rs2 = ps2.executeQuery();
		        if (rs2.next()) {
		            username = rs2.getString("username");
		        }
		    }

		    // ── Fetch books ──────────────────────────────────────────────────
		    try (PreparedStatement ps = conn.prepareStatement(sql)) {
		        ps.setInt(1, userId);
		        ResultSet rs = ps.executeQuery();

		        while (rs.next()) {
		            UserBook ub = new UserBook();
		            ub.setUserBookId(rs.getInt("user_book_id"));
		            ub.setUserId(rs.getInt("user_id"));
		            ub.setBookId(rs.getInt("book_id"));
		            ub.setStatus(rs.getString("status"));
		            ub.setRating(rs.getInt("rating"));
		            ub.setAddedAt(rs.getString("added_at"));
		            ub.setReadDate(rs.getString("read_date"));
		            ub.setTitle(rs.getString("title"));
		            ub.setAuthor(rs.getString("author"));
		            ub.setCoverUrl(rs.getString("cover_url"));
		            ub.setPageCount(rs.getInt("page_count"));

		            if ("wishlist".equals(ub.getStatus())) {
		                wishlist.add(ub);
		            } else if ("read".equals(ub.getStatus())) {
		                readBooks.add(ub);
		                totalPages += ub.getPageCount();
		            }
		        }
		    }

		} catch (SQLException e) {
		    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    resp.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
		    return;
		}

		// ── Build response ───────────────────────────────────────────────────
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("username",   username);
		response.put("wishlist",   wishlist);
		response.put("readBooks",  readBooks);
		response.put("totalPages", totalPages);
		
		
		// In DashboardServlet.java, after building the response/////////////////////////////////////////////////////////////////
		System.out.println("=== DEBUG ===");
		System.out.println("Wishlist size: " + wishlist.size());
		for (UserBook ub : wishlist) {
		    System.out.println("Book: " + ub.getTitle() + 
		                       " | Cover: " + ub.getCoverUrl() + 
		                       " | BookID: " + ub.getBookId());
		}
		System.out.println("ReadBooks size: " + readBooks.size());
		for (UserBook ub : readBooks) {
		    System.out.println("Book: " + ub.getTitle() + 
		                       " | Cover: " + ub.getCoverUrl() + 
		                       " | BookID: " + ub.getBookId());
		}

		mapper.writeValue(resp.getWriter(), response);
	}
}
