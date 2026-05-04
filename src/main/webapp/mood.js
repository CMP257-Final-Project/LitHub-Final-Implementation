var moodMap = {
    'feel-good': 'feel_good',
    'cant-put-down': 'cant_put_down',
    'laugh-out-loud': 'laugh_out_loud',
    'dark-twisted': 'dark_twisted',
    'make-me-cry': 'make_me_cry'
};

var urlParams = new URLSearchParams(window.location.search);
var moodKey = urlParams.get('mood');
var dbSection = moodMap[moodKey] || 'feel_good';

var moodConfig = {
    'feel-good': {
        emoji: '😊',
        tagline: 'Sunshine in every page',
        subtext: 'Books that wrap you in a warm hug',
        booksTitle: 'Feel-Good Reads',
        booksSub: 'Handpicked to lift your spirits',
        clubsTitle: 'Clubs for Happy Readers',
        clubsSub: 'Find your reading community'
    },
    'cant-put-down': {
        emoji: '🔥',
        tagline: 'Just one more chapter...',
        subtext: 'Stories you won\'t be able to stop reading',
        booksTitle: 'Can\'t Put Down Reads',
        booksSub: 'Warning: sleep not included',
        clubsTitle: 'Clubs for Addicted Readers',
        clubsSub: 'Find your people'
    },
    'laugh-out-loud': {
        emoji: '😂',
        tagline: 'Books that feel like a comedy show',
        subtext: 'Light, funny reads to brighten your day',
        booksTitle: 'Laugh-Out-Loud Reads',
        booksSub: 'Because life\'s too short to be serious',
        clubsTitle: 'Comedy Book Clubs',
        clubsSub: 'Share laughs, memes & favorite moments'
    },
    'dark-twisted': {
        emoji: '🖤',
        tagline: 'Enter the shadows',
        subtext: 'Twisted tales and dark minds',
        booksTitle: 'Dark & Twisted Picks',
        booksSub: 'Not for the faint-hearted',
        clubsTitle: 'Clubs for the Brave',
        clubsSub: 'Find your people'
    },
    'make-me-cry': {
        emoji: '😢',
        tagline: 'Bring the tissues',
        subtext: 'Emotional stories that stay with you forever',
        booksTitle: 'Heartbreaking Reads',
        booksSub: 'Stories that hit you right in the feels',
        clubsTitle: 'Emotional Readers Clubs',
        clubsSub: 'Find your reading community'
    }
};

var config = moodConfig[moodKey] || moodConfig['feel-good'];

document.body.classList.add('theme-' + moodKey);

document.getElementById('moodEmoji').innerText = config.emoji;
document.getElementById('moodTagline').innerText = config.tagline;
document.getElementById('moodSub').innerText = config.subtext;
document.getElementById('booksSectionTitle').innerText = config.booksTitle;
document.getElementById('booksSectionSub').innerText = config.booksSub;
document.getElementById('BookClubsTitle').innerText = config.clubsTitle;
document.getElementById('BookClubsSub').innerText = config.clubsSub;

function loadBooks() {
    return fetch('LitHubBackend/FindBookServlet?section=' + dbSection)  // ✅ leading slash
        .then(function(response) {
            if (!response.ok) throw new Error('Failed to fetch books');
            return response.json();
        })
        .then(function(books) {
            console.log('Mood books:', books);
            populateBooks(books);
            return books;
        });
}

function populateBooks(books) {
    var track = document.getElementById('mvTrack');
    if (!track) return;
    track.innerHTML = '';
    books.forEach(function(book) {
        track.appendChild(createBookCard(book));
    });
    setupBookSlider();   
}

function loadClubs() {
    return fetch('LitHubBackend/FindClubServlet?section=' + dbSection)  
        .then(function(response) {
            if (!response.ok) throw new Error('Failed to fetch clubs');
            return response.json();
        })
        .then(function(clubs) {
            console.log('Mood clubs:', clubs);
            populateClubs(clubs);
            return clubs;
        });
}

function populateClubs(clubs) {
    var track = document.getElementById('clubTrack');  
    if (!track) return;
    track.innerHTML = '';
    clubs.forEach(function(club) {
        track.appendChild(createClubCard(club));
    });
}

function createBookCard(book) {
    var card = document.createElement('div');
    card.className = 'book-card';
    var cover = book.cover_url || 'Img/tn1.jpg';
    var html = '';
    html += '<img src="' + cover + '" alt="' + book.title + '">';
    html += '<div class="tn-button">';
    html += '<button class="btn-readmore" onclick="window.location.href=\'BookReadMore.html?book=' + book.id + '\'">Read More</button>';
    html += '<button class="btn-save" data-book-id="' + book.id + '"><i class="far fa-bookmark"></i> Save</button>';
    html += '</div>';
    card.innerHTML = html;
    return card;
}

function createClubCard(club) {
    var card = document.createElement('div');
    card.className = 'dbook-card';
    var image = club.cover_url || 'Img/db1.png';
    var html = '';
    html += '<img src="' + image + '" alt="' + club.name + '">';
    html += '<div class="tn-button">';
    html += '<button class="btn-readmore" onclick="window.location.href=\'clubreadmore.html?club=' + club.id + '\'">Read More</button>';
    html += '</div>';
    card.innerHTML = html;
    return card;
}

function setupBookSlider() {
    var track = document.getElementById('mvTrack');
    var prev = document.getElementById('Prev');
    var next = document.getElementById('Next');
    var container = document.querySelector('.mv-overflow');
    if (!track || !prev || !next || !container || track.children.length === 0) return;
    var index = 0;
    function update() {
        var cardWidth = track.children[0].offsetWidth + 12;
        var visible = Math.max(1, Math.floor(container.offsetWidth / cardWidth));
        var maxIndex = Math.max(0, track.children.length - visible);
        if (index > maxIndex) index = maxIndex;
        track.style.transform = 'translateX(-' + (index * cardWidth) + 'px)';
    }
    next.onclick = function(e) { e.preventDefault(); index++; update(); };
    prev.onclick = function(e) { e.preventDefault(); index--; if (index < 0) index = 0; update(); };
    window.addEventListener('resize', update);
    update();
}


document.addEventListener('DOMContentLoaded', function() {
    Promise.all([loadBooks(), loadClubs()])
        .catch(function(err) {
            console.error('Error loading mood page:', err);
        });
});