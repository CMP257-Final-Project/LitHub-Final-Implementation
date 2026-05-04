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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/LitHubBackend/FindClubServlet")
public class FindClubServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private ClubRepository clubRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        clubRepository = new ClubRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        ClubRepository repo = new ClubRepository();

        String sectionParam = request.getParameter("section");
        List<ClubDetails> clubs;
        if (sectionParam != null && !sectionParam.isEmpty()) {
            clubs = repo.getClubsBySection(sectionParam);
        } else {
            clubs = repo.getAllClubs();
        }

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(clubs);
        response.getWriter().write(json);
    }
    }