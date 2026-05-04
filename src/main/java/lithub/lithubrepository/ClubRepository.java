package lithub.lithubrepository;
import lithub.models.BookDetails;
import lithub.models.ClubDetails;
import lithub.utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClubRepository {
	
	public List<ClubDetails> getAllClubs() {
       List<ClubDetails> clubs = new ArrayList<>();
       String query = "SELECT * FROM clubs";
      
       try (Connection conn = DatabaseUtil.getConnection();
               PreparedStatement stmt = conn.prepareStatement(query);
               ResultSet rs = stmt.executeQuery()) {
              while (rs.next()) {
           	   ClubDetails club = new ClubDetails(
                      rs.getInt("id"),
                      rs.getString("name"),
                      rs.getString("tagline"),
                      rs.getString("description"),
                      rs.getString("cover_url"),
                      rs.getInt("current_book_id"),
                      rs.getInt("member_count"),
                      rs.getString("section")
                     
                  );
                 
                  clubs.add(club);
              }
          } catch (SQLException e) {
              e.printStackTrace();
          }
         
          return clubs;
      }
	
	public ClubDetails getClubById(int id) {
		lithub.models.ClubDetails club = null;
	    String sql = "SELECT * FROM clubs WHERE id = ?";
	   
	    try (Connection conn = DatabaseUtil.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	       
	        stmt.setInt(1, id);
	        ResultSet rs = stmt.executeQuery();
	       
	        if (rs.next()) {
	            club = new ClubDetails();
	            club.setId(rs.getInt("id"));
	            club.setName(rs.getString("name"));
	            club.setTagline(rs.getString("tagline"));
	            club.setDescription(rs.getString("description"));
	            club.setCover_url(rs.getString("cover_url"));  
	            club.setCurrentBookId(rs.getInt("current_book_id"));
	            club.setMemberCount(rs.getInt("member_count"));
	            club.setSection(rs.getString("section"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return club;
	}
	
	public int incrementMemberCount(int clubId) {
	    String sql = "UPDATE clubs SET member_count = member_count + 1 WHERE id = ?";
	    try (Connection conn = DatabaseUtil.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, clubId);
	        int affected = stmt.executeUpdate();
	        if (affected == 0) return -1; 
	        String selectSql = "SELECT member_count FROM clubs WHERE id = ?";
	        try (PreparedStatement stmt2 = conn.prepareStatement(selectSql)) {
	            stmt2.setInt(1, clubId);
	            ResultSet rs = stmt2.executeQuery();
	            if (rs.next()) return rs.getInt("member_count");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1;
	}
	
	public List<ClubDetails> getClubsBySection(String section) {
 	   List<ClubDetails> clubs = new ArrayList<>();
 	   
 	   String sql = "SELECT id,name,cover_url FROM clubs WHERE section = ?";
 	   try (Connection conn = DatabaseUtil.getConnection();
 			   PreparedStatement stmt = conn.prepareStatement(sql)){
 		   stmt.setString(1, section);
 		   ResultSet rs = stmt.executeQuery();
 		   
 		   while(rs.next()) {
 			   ClubDetails club = new ClubDetails();
 			   club.setId(rs.getInt("id"));
 	           club.setName(rs.getString("name"));
 	           club.setCover_url(rs.getString("cover_url"));
 	           
 	           clubs.add(club);
 			   }
 	   } catch (SQLException e){
 		   e.printStackTrace();
 	   }
 	   return clubs;

	}
}