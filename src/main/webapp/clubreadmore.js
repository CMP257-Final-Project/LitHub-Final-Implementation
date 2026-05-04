document.addEventListener("DOMContentLoaded", function() {
   var urlParams = new URLSearchParams(window.location.search);
   var clubId = urlParams.get("club");
   if (!clubId) {
       document.getElementById("clubTitle").innerText = "Error: Club ID missing";
       return;
   }
   fetch('LitHubBackend/ClubReadMoreServlet?id=' + clubId)
       .then(function(response) {
           if (!response.ok) {
               throw new Error("Failed to load club data");
           }
           return response.json();
       })
       .then(function(club) {
           if (!club) {
               document.getElementById("clubTitle").innerText = "Club not found";
               return;
           }
           populateClubPage(club);
           window.currentClub = club;
       })
       .catch(function(error) {
           console.error("Error fetching club:", error);
           document.getElementById("clubTitle").innerText = "Error loading club";
       });
   var joinBtn = document.getElementById("joinBtn");
   var closeBtn = document.getElementById("closeBtn");
   var modalOverlay = document.getElementById("modalOverlay");
   if (joinBtn) joinBtn.addEventListener("click", joinClub);
   if (closeBtn) closeBtn.addEventListener("click", closeModal);
   if (modalOverlay) {
       modalOverlay.addEventListener("click", function(event) {
           if (event.target === modalOverlay) closeModal();
       });
   }
});
function populateClubPage(club) {
   var imgUrl = club.cover_url || club.coverUrl || "Img/db1.png";
   document.getElementById("clubImage").src = imgUrl;
   document.getElementById("clubImage").alt = club.name;
   document.getElementById("clubTitle").innerText = club.name || "Club Name";
   document.getElementById("clubSub").innerText = club.tagline || "";
   var description = club.description || "No description available.";
   document.getElementById("clubSummary").innerText = description;
   var members = club.memberCount || 0;
   document.getElementById("clubMembers").innerHTML = members.toLocaleString() + " members";
   var currentBookId = club.currentBookId;
   //var currentBookText = currentBookId ? "Currently Reading: Book ID " + currentBookId : "No current book";
   //document.getElementById("clubCurrentBook").innerText = currentBookText;
   if (currentBookId && currentBookId !== 0 && currentBookId !== null) {
           fetch('LitHubBackend/ReadMoreServlet?id=' + currentBookId)
               .then(response => response.ok ? response.json() : Promise.reject())
               .then(book => {
                   document.getElementById("clubCurrentBook").innerText = "Currently Reading: " + book.title;
               })
               .catch(() => {
                   document.getElementById("clubCurrentBook").innerText = "Currently Reading: (unknown)";
               });
       } else {
           document.getElementById("clubCurrentBook").innerText = "No current book";
       }
}
var joined = false; 

//JOIN CLUB 
function joinClub() {
   	var club = window.currentClub;
	if (!club) return;
   
	var userId = LitHubAuth.getUserId();  
	    if (!userId) {
	        window.location.href = "Loginpage.html";
	        return;
	    }

	    var joinBtn = document.getElementById("joinBtn");
	    if (joinBtn.disabled) return;
	    joinBtn.disabled = true;

		fetch('LitHubBackend/JoinClubServlet?clubId=' + club.id + '&userId=' + userId, { method: 'POST' })
		        .then(function(response) {
		            if (response.status === 401) {
		                window.location.href = "Loginpage.html";
		                throw new Error("Please login");
		            }
		            if (!response.ok) {
		                throw new Error("Join failed");
		            }
		            return response.json();
		        })
		        .then(function(data) {
		            if (data.success) {
		                // to Update member count on page
		                var membersSpan = document.getElementById("clubMembers");
		                membersSpan.innerHTML = data.memberCount.toLocaleString() + " members";

		                // to Change button
		                joinBtn.innerText = "Joined!";
		                joinBtn.classList.add("joined");

		                // to Show popup
		                var modalTitle = document.getElementById("modalTitle");
		                var modalMessage = document.getElementById("modalMessage");
		                var modalMemberNum = document.getElementById("modalMemberNum");
		                modalTitle.innerText = "Welcome to " + club.name + "!";
		                modalMessage.innerText = "You've joined the club!";
		                modalMemberNum.innerText = "Now " + data.memberCount + " members";
		                document.getElementById("modalOverlay").classList.add("active");
		            } else {
		                alert("Could not join club. Perhaps you are already a member?");
		                joinBtn.disabled = false;
		            }
		        })
		        .catch(function(error) {
		            console.error("Join error:", error);
		            if (error.message !== "Please login") {
		                alert("Error joining club. Try again.");
		            }
		            joinBtn.disabled = false;
		        });
}
// Close popup
function closeModal() {
   document.getElementById("modalOverlay").classList.remove("active");
}
