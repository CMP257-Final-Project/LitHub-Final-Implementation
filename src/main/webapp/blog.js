document.addEventListener("DOMContentLoaded", function () {
   // ── ELEMENTS ─────────────────────────────────────────
   const topBtn = document.getElementById("topBtn");
   const form = document.getElementById("blogForm");
   const rate = document.getElementById("rating");
   const blogBox = document.getElementById("blog");
   const titleBox = document.getElementById("book_title");
   const authorBox = document.getElementById("book_author");
   const stars = document.getElementById("stars");
   const count = document.getElementById("count");
   const photoInput = document.getElementById("photo");
   const preview = document.getElementById("imagePreview");
   const previewWrapper = document.getElementById("imagePreviewWrapper");
   // ── SAFETY CHECK ─────────────────────────────────────
   if (!form) return;
   // ── SCROLL TO TOP BUTTON ─────────────────────────────
   if (topBtn) {
       topBtn.onclick = function () {
           window.scrollTo({
               top: 0,
               behavior: "smooth"
           });
       };
   }
   // ── HOVER EFFECTS FOR BUTTONS ────────────────────────
   document.querySelectorAll(".submit").forEach(btn => {
       btn.addEventListener("mouseover", () => {
           btn.style.background = "#3E2C23";
           btn.style.borderColor = "#3E2C23";
       });
       btn.addEventListener("mouseout", () => {
           btn.style.background = "";
           btn.style.borderColor = "";
       });
   });
   // ── INPUT FOCUS EFFECTS ──────────────────────────────
   [blogBox, titleBox, authorBox].forEach(input => {
       if (!input) return;
       input.addEventListener("focus", () => {
           input.style.boxShadow = "0 0 10px #3E2C23";
       });
       input.addEventListener("blur", () => {
           input.style.boxShadow = "none";
       });
   });
   // ── CHARACTER COUNTER ────────────────────────────────
   if (blogBox && count) {
       blogBox.addEventListener("input", () => {
           count.textContent = blogBox.value.length;
       });
   }
   // ── STAR RATING DISPLAY ──────────────────────────────
   if (rate && stars) {
       rate.addEventListener("input", () => {
           let rating = parseInt(rate.value);
           stars.textContent =
               "★".repeat(rating) + "☆".repeat(5 - rating);
           if (rating === 5) {
               stars.classList.add("five-star-glow");
           } else {
               stars.classList.remove("five-star-glow");
           }
       });
   }
   // ── IMAGE PREVIEW (OPTIONAL SAFE) ────────────────────
   if (photoInput && preview && previewWrapper) {
       photoInput.addEventListener("change", function () {
           const file = this.files[0];
           if (file) {
               const reader = new FileReader();
               reader.onload = e => {
                   preview.src = e.target.result;
                   previewWrapper.style.display = "block";
               };
               reader.readAsDataURL(file);
           } else {
               previewWrapper.style.display = "none";
           }
       });
   }
   // ── FORM SUBMIT (CLEAN, NO CONFLICT) ─────────────────
   form.addEventListener("submit", function () {
       // Let your inline script handle fetch()
       // Optional: reset UI AFTER submission success happens
       setTimeout(() => {
           form.reset();
           if (stars) {
               stars.textContent = "";
               stars.classList.remove("five-star-glow");
           }
           if (count) {
               count.textContent = "0";
           }
           if (previewWrapper) {
               previewWrapper.style.display = "none";
           }
       }, 500);
   });
});
