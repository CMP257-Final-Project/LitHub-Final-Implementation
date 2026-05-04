//ClubReadMoreServlet

package lithub.servlets;
import lithub.lithubrepository.ClubRepository;
import lithub.models.ClubDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/LitHubBackend/ClubReadMoreServlet")
public class ClubReadMoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
        
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
       response.setCharacterEncoding("UTF-8");
       String idParam = request.getParameter("id");
       if (idParam == null || idParam.trim().isEmpty()) {
           response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
           response.getWriter().write("{\"error\": \"Club ID is required\"}");
           return;
       }
		
		
       try {
           int clubId = Integer.parseInt(idParam);
           ClubRepository repo = new ClubRepository();
           ClubDetails club = repo.getClubById(clubId);
           if (club == null) {
               response.setStatus(HttpServletResponse.SC_NOT_FOUND);
               response.getWriter().write("{\"error\": \"Club not found\"}");
               return;
           }
           ObjectMapper mapper = new ObjectMapper();
           String json = mapper.writeValueAsString(club);
           response.getWriter().write(json);
       } catch (NumberFormatException e) {
           response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
           response.getWriter().write("{\"error\": \"Invalid club ID format\"}");
       } catch (Exception e) {
           e.printStackTrace();
           response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
           response.getWriter().write("{\"error\": \"An internal error occurred\"}");
       }
	}
	
}

