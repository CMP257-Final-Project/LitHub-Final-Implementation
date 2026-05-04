//BooksReadMore.js:

document.addEventListener("DOMContentLoaded", function() {
   var params = new URLSearchParams(window.location.search);
   var bookId = params.get("book");
   
   // WE GET ONE BOOK FROM THEIR ID
   if (!bookId) {
       document.getElementById("bookTitle").innerText = "Error: Book ID is missing";
       return;
   }
   fetch('LitHubBackend/ReadMoreServlet?id=' + bookId)
       .then(function(response) {
           if (!response.ok) {
               throw new Error("Failed to receive book data");
           }
           return response.json();
       })
       .then(function(book) {
           if (!book) {
               document.getElementById("bookTitle").innerText = "Book not found";
               return;
           }
           populateBookDetails(book);
       })
       .catch(function(error) {
           console.error("Error loading book:", error);
           document.getElementById("bookTitle").innerText = "Error loading book details";
       });
});
function populateBookDetails(book) {
   // update book image and text
   document.getElementById("bookImage").src = book.cover_url;
   document.getElementById("bookImage").alt = book.title;
   document.getElementById("bookTitle").innerText = book.title;
   document.getElementById("bookAuthor").innerText = book.author || "Unknown Author";
   document.getElementById("bookSummary").innerText = book.description || "No description available.";
   // update the metadata elements
   document.getElementById("pages").innerHTML =
       '<strong>' + (book.page_count || 0) + '</strong><br>Pages';
   document.getElementById("genre").innerHTML =
       '<strong>' + (book.genre || "N/A") + '</strong><br>Genre';
   document.getElementById("publishedDate").innerHTML =
       '<strong>' + (book.year || "N/A") + '</strong><br>Published';
   renderStars(book.avg_rating);
}
function renderStars(rating) {
   var stars = document.getElementById("star");
   if (!stars) return;
   var fullStars = Math.floor(rating || 0);
   var html = '';
   for (var i = 1; i <= 5; i++) {
       if (i <= fullStars) {
           html += '<i class="fas fa-star"></i>';
       } else {
           html += '<i class="far fa-star"></i>';
       }
   }
   stars.innerHTML = html;
}
// User clicks About button
function backToAbout() {
   window.location.href = 'HomePage.html#About';
}
