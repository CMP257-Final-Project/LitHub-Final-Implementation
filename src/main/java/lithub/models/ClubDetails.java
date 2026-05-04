//Club Details:
//Pls recheck the cover_url Setter and Getter name if they are the same and i have added one //more for the Section.

package lithub.models;


public class ClubDetails {
	private int id;
   private String name;
   private String tagline;
   private String description;
   private String cover_url;
   private int current_book_id;
   private int member_count;
   private String section;
  
   public ClubDetails() {
   	
   }
  
   public ClubDetails(int id, String name, String tagline, String description, String cover_url,
               int current_book_id, int member_count,String section) {
       this.id = id;
       this.name = name;
       this.tagline = tagline;
       this.description = description;
       this.cover_url = cover_url;
       this.current_book_id = current_book_id;
       this.member_count = member_count;
       this.section = section;
   }
  
   public int getId() { return id; }
   public void setId(int id) { this.id = id; }
   public String getName() { return name; }
   public void setName(String name) { this.name = name; }
   public String getTagline() { return tagline; }
   public void setTagline(String tagline) { this.tagline = tagline; }
   public String getDescription() { return description; }
   public void setDescription(String description) { this.description = description; }
   public String getCover_url() { return cover_url; }
   public void setCover_url(String cover_url) { this.cover_url = cover_url; }
   public int getCurrentBookId() { return current_book_id; }
   public void setCurrentBookId(int current_book_id) { this.current_book_id = current_book_id; }
   public int getMemberCount() { return member_count; }
   public void setMemberCount(int member_count) { this.member_count = member_count; }
  
   public String getSection() { return section; }
   public void setSection(String section) { this.section = section; }
 
 
  
}
