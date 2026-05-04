package lithub.servlets;


import lithub.lithubrepository.BookRepository;
import lithub.models.BookDetails;
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


@WebServlet("/LitHubBackend/ReadMoreServlet")
public class ReadMoreServlet extends HttpServlet {
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
       response.setContentType("application/json");
       response.setCharacterEncoding("UTF-8");
       String idParam = request.getParameter("id");
       
       // Validate that the ID parameter exists
       if (idParam == null || idParam.trim().isEmpty()) {
           response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
           response.getWriter().write("{\"error\": \"Book ID is required\"}");
           return;
       }
       try {
           int bookId = Integer.parseInt(idParam);
           BookRepository repo = new BookRepository();
           BookDetails book = repo.getBookById(bookId);
           
           // Validate that the book exists in the repository
           if (book == null) {
               response.setStatus(HttpServletResponse.SC_NOT_FOUND);
               response.getWriter().write("{\"error\": \"Book not found\"}");
               return;
           }
           
           // Map to JSON and write output
           ObjectMapper mapper = new ObjectMapper();
           String json = mapper.writeValueAsString(book);
           response.getWriter().write(json);
       } catch (NumberFormatException e) {
           response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
           response.getWriter().write("{\"error\": \"Invalid book ID format\"}");
       } catch (Exception e) {
           e.printStackTrace();
           response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
           response.getWriter().write("{\"error\": \"An internal error occurred\"}");
       }
   }
}

