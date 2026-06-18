# LitHub - Book Club & Reading Tracker

<div align="center">
    
![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6-yellow)

</div>

---

## Overview

**LitHub** is a full-stack web application for book lovers to track their reading journey. Users can:

-  Maintain a **wishlist** of books to read
-  Mark books as **read** with dates
-  **Rate** books from 1-5 stars
-  Track **reading statistics** (total books, pages read)
-  Write and share **book blogs**
-  Join and participate in **book clubs**
-  **Export** reading lists as TXT or CSV

---

##  Features

### Core Features
- **User Authentication** - Secure login and session management
- **Wishlist Management** - Add, view, and remove books
- **Reading Tracker** - Move books from wishlist to read list
- **Rating System** - Rate books 1-5 stars
- **Dashboard** - Overview of all reading activity
- **Statistics** - Track total books and pages read

### Advanced Features
- **Book Clubs** - Join clubs and view details
- **Blogs** - Write and share book reviews
- **Export** - Download lists as TXT or CSV
- **Responsive Design** - Works on all devices
- **Toast Notifications** - Real-time user feedback

---

##  Tech Stack

### Frontend
- HTML5, CSS3, JavaScript (ES6)
- Font Awesome 6

### Backend
- Java 21
- Jakarta Servlets 6.0
- Jackson Databind (JSON parsing)
- JDBC

### Database
- MySQL 8.0

### Tools
- Apache Tomcat 10.1
- Git
- VS Code

---

##  Getting Started

### Prerequisites

- Java 21 or higher
- Apache Tomcat 10.1 or higher
- MySQL 8.0 or higher

### Installation

```bash
# 1. Clone the repository

# 2. Set up the database locally
# Create a MySQL database named 'lithub'

# 3. Update database credentials
# Edit src/main/java/lithub/utils/DatabaseUtil.java
# Change USERNAME and PASSWORD to match your MySQL setup

# 4. Deploy to Tomcat
# Copy the WAR file to Tomcat's webapps folder

# 5. Start Tomcat
/path/to/tomcat/bin/catalina.sh start
```

The application starts at: **http://localhost:8080/lithub/**

### Development Mode

```bash
npm install
npm run dev
```

The development server starts at **http://localhost:5173**

### Production Build

```bash
npm run build
npm run preview
```

---

##  API Endpoints

### Book Operations
```
POST /api/book-action    # markRead, removeWishlist, removeRead, rate
```

### Dashboard
```
GET /api/dashboard       # Get user dashboard data
```

### Blogs
```
GET  /api/blogs          # Get user's blogs
POST /api/blogs          # Create a new blog
```

### Book Clubs
```
GET /api/user-clubs      # Get user's joined clubs
```

### Save Book
```
POST /LitHubBackend/SaveBookServlet?userId=1&bookId=5
```

---

##  Project Structure

```
lithub/
├── src/
│   ├── main/
│   │   ├── java/lithub/
│   │   │   ├── servlets/
│   │   │   ├── utils/
│   │   │   └── models/
│   │   └── webapp/
│   │       ├── Img/
│   │       ├── META-INF/
│   │       └── WEB-INF/
└── └──
```
