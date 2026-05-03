// SWIPER (Hero Slider)
if (document.querySelector('.bookSwiper')) {
    new Swiper('.bookSwiper', {
        effect: 'coverflow',
        grabCursor: true,
        centeredSlides: true,
        slidesPerView: 3,
        loop: true,
        autoplay: { delay: 2500, disableOnInteraction: false },
        coverflowEffect: {
            rotate: 30,
            stretch: 0,
            depth: 100,
            modifier: 1,
            slideShadows: false
        }
    });
}

var allBooks = [];

// LOAD EVERYTHING FIRST → THEN INIT SLIDERS ONCE
document.addEventListener("DOMContentLoaded", function() {
    Promise.all([loadBooks(), loadClubs()])
        .then(function(results) {
            var books = results[0];
            var clubs = results[1];
            populateSliders(books);
            populateClubs(clubs);
            setTimeout(function() {
                initSliders();
            }, 800);
            setupFilterSidebar();
        })
        .catch(function(err) {
            console.error("Error loading page:", err);
        });
});

// -----------------------------
// FETCH BOOKS
// -----------------------------
function loadBooks() {
    return fetch('LitHubBackend/FindBookServlet')
        .then(function(response) {
            if (!response.ok) throw new Error('Failed to fetch books');
            return response.json();
        })
        .then(function(data) {
            console.log("Books:", data);
            allBooks = data;
            return data;
        });
}

// -----------------------------
// FETCH CLUBS
// -----------------------------
function loadClubs() {
    return fetch('LitHubBackend/FindClubServlet')
        .then(function(response) {
            if (!response.ok) throw new Error('Failed to fetch clubs');
            return response.json();
        })
        .then(function(data) {
            console.log("Clubs:", data);
            return data;
        });
}

// -----------------------------
// POPULATE SLIDERS
// -----------------------------
function populateSliders(books) {
    var tnTrack = document.getElementById('tnTrack');
    var mvTrack = document.getElementById('mvTrack');

    if (tnTrack) tnTrack.innerHTML = '';
    if (mvTrack) mvTrack.innerHTML = '';

    books.forEach(function(book) {
        if (tnTrack && book.section === "trending") {
            tnTrack.appendChild(createBookCard(book));
        }
        if (mvTrack && book.section === "most_viewed") {
            mvTrack.appendChild(createBookCard(book));
        }
    });
}

// -----------------------------
// POPULATE CLUBS
// -----------------------------
function populateClubs(clubs) {
    var track = document.getElementById('mvTrackDiscover');
    if (!track) return;

    track.innerHTML = '';

    if (clubs.length === 0) {
        track.innerHTML = '<p style="text-align:center;width:100%">No clubs found</p>';
        return;
    }

    clubs.forEach(function(club) {
        track.appendChild(createClubCard(club));
    });
}

// -----------------------------
// CREATE CARDS (ES5 compatible)
// -----------------------------
function createBookCard(book) {
    var card = document.createElement('div');
    card.className = 'book-card';

    var cover = book.cover_url || 'Img/tn1.jpg';

    var html = '';
    html += '<img src="' + cover + '" alt="' + book.title + '">';
    html += '<div class="tn-button">';
    html += '<button class="btn-readmore" onclick="window.location.href=\'BookReadMore.html?book=' + book.id + '\'">';
    html += 'Read More';
    html += '</button>';
    html += '<button class="btn-save">';
    html += '<i class="far fa-bookmark"></i> Save';
    html += '</button>';
    html += '</div>';

    card.innerHTML = html;
    return card;
}

function createClubCard(club) {
    var card = document.createElement('div');
    card.className = 'dbook-card';

    var image = club.cover_url|| 'Img/db1.png'|| club.coverUrl ;

    var html = '';
    html += '<img src="' + image + '" alt="' + club.name + '">';
    html += '<div class="tn-button">';
    html += '<button class="btn-readmore" onclick="window.location.href=\'clubreadmore.html?club=' + club.id + '\'">';
    html += 'Read More';
    html += '</button>';
    html += '<button class="btn-save">';
    html += '<i class="far fa-bookmark"></i> Save';
    html += '</button>';
    html += '</div>';

    card.innerHTML = html;
    return card;
}

// -----------------------------
// FILTERS (FIXED YEAR BUG)
// -----------------------------
function getSelectedFilters() {
    var selected = { genre: [], rating: [], year: [] };
    var checkedBoxes = document.querySelectorAll('.filter-checkbox input:checked');
    
    for (var i = 0; i < checkedBoxes.length; i++) {
        var cb = checkedBoxes[i];
        selected[cb.dataset.filter].push(cb.value);
    }
    return selected;
}

function filterBooks(filters) {
    return allBooks.filter(function(book) {
        var match = true;

        if (filters.genre.length) {
            var genres = (book.genre || "").split(",");
            var genreMatch = false;
            for (var i = 0; i < filters.genre.length; i++) {
                if (genres.indexOf(filters.genre[i]) !== -1) {
                    genreMatch = true;
                    break;
                }
            }
            match = genreMatch;
        }

        if (match && filters.rating.length) {
            var ratingMatch = false;
            for (var i = 0; i < filters.rating.length; i++) {
                if (book.avg_rating >= parseFloat(filters.rating[i])) {
                    ratingMatch = true;
                    break;
                }
            }
            match = ratingMatch;
        }

        if (match && filters.year.length) {
            var yearMatch = false;
            for (var i = 0; i < filters.year.length; i++) {
                var y = filters.year[i];
                if (y === 'older' && book.year < 2022) {
                    yearMatch = true;
                    break;
                } else if (book.year == y) {
                    yearMatch = true;
                    break;
                }
            }
            match = yearMatch;
        }

        return match;
    });
}

function applyFiltersAndDisplay() {
    var filtered = filterBooks(getSelectedFilters());
    populateSliders(filtered);
    setTimeout(function() {
        initSliders();
    }, 100);
}

// -----------------------------
// FILTER SIDEBAR
// -----------------------------
function setupFilterSidebar() {
    var sidebar = document.getElementById('filterSidebar');
    if (!sidebar) return;

    var overlay = document.getElementById('filterOverlay');
    var filterToggleBtn = document.getElementById('filterToggleBtn');
    var filterCloseBtn = document.getElementById('filterCloseBtn');
    var filterApplyBtn = document.getElementById('filterApplyBtn');

    if (filterToggleBtn) {
        filterToggleBtn.addEventListener('click', function() {
            sidebar.classList.add('open');
            if (overlay) overlay.classList.add('active');
        });
    }

    if (filterCloseBtn) {
        filterCloseBtn.addEventListener('click', closeSidebar);
    }
    if (overlay) {
        overlay.addEventListener('click', closeSidebar);
    }

    function closeSidebar() {
        sidebar.classList.remove('open');
        if (overlay) overlay.classList.remove('active');
    }

    if (filterApplyBtn) {
        filterApplyBtn.addEventListener('click', applyFiltersAndDisplay);
    }
}

// -----------------------------
// SLIDERS (FIXED)
// -----------------------------
function initSliders() {
    setupSlider('tnTrack', 'Prev1', 'Next1', '.tn-overflow');
    setupSlider('mvTrack', 'Prev', 'Next', '.mv-overflow');
    setupSlider('mvTrackDiscover', 'PrevDiscover', 'NextDiscover', '#db-overflow');
}

function setupSlider(trackId, prevId, nextId, containerSelector) {
    var track = document.getElementById(trackId);
    var prev = document.getElementById(prevId);
    var next = document.getElementById(nextId);
    var container = document.querySelector(containerSelector);

    if (!track || !prev || !next || !container || track.children.length === 0) return;

    var index = 0;

    function update() {
        var cardWidth = track.children[0].offsetWidth + 12;
        var visible = Math.max(1, Math.floor(container.offsetWidth / cardWidth));
        var maxIndex = Math.max(0, track.children.length - visible);
        if (index > maxIndex) index = maxIndex;
        track.style.transform = 'translateX(-' + (index * cardWidth) + 'px)';
    }

    // Remove old listeners by cloning
    var newPrev = prev.cloneNode(true);
    var newNext = next.cloneNode(true);
    prev.parentNode.replaceChild(newPrev, prev);
    next.parentNode.replaceChild(newNext, next);

    var finalPrev = document.getElementById(prevId);
    var finalNext = document.getElementById(nextId);

    finalNext.onclick = function(e) {
        e.preventDefault();
        index++;
        update();
    };
    finalPrev.onclick = function(e) {
        e.preventDefault();
        index--;
        if (index < 0) index = 0;
        update();
    };

    window.addEventListener('resize', update);
    update();
}

// -----------------------------
// TOAST + SAVE BUTTON
// -----------------------------
document.addEventListener('click', function(event) {
    var btn = event.target.closest('.btn-save');
    if (!btn) return;

    var icon = btn.querySelector('i');

    if (btn.classList.contains('saved')) {
        btn.classList.remove('saved');
        icon.classList.replace('fas', 'far');
        showToast("Removed from wishlist!");
    } else {
        btn.classList.add('saved');
        icon.classList.replace('far', 'fas');
        showToast("Saved to wishlist!");
    }
});

function showToast(message) {
    var toast = document.createElement('div');
    toast.className = 'toast-notification';
    toast.innerText = message;
    document.body.appendChild(toast);
    setTimeout(function() {
        toast.remove();
    }, 3000);
}

