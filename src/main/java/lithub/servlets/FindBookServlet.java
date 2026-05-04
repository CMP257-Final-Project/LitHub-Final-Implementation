package lithub.servlets;

import lithub.lithubrepository.BookRepository;
import lithub.models.BookDetails;
import com.fasterxml.jackson.databind.ObjectMapper; // Uses the Jackson library you installed!

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/LitHubBackend/FindBookServlet")
public class FindBookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Setting the response type to JSON so the frontend knows what it is receiving
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        BookRepository repo = new BookRepository();
        String sectionParam = request.getParameter("section");

        List<BookDetails> books;
        if(sectionParam != null && !sectionParam.isEmpty()) {
        	books = repo.getBooksBySection(sectionParam);
        }
        else {
        	books = repo.getAllBooks();
        }
       
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(books);

        response.getWriter().write(json);
    
		
	}

}