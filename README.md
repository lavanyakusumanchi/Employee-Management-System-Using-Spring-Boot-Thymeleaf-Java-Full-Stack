<h1 align="center">Employee Attendance Management System 🧮🚀</h1>
<p align="center"><i>A Spring Boot REST API application for Construction Worker Attendance & Overtime Management</i></p>
<br>

## 🎯 Current Status

### ✅ What's Working:
- ✅ Application starts successfully on port 8080
- ✅ Interactive Web UI for API testing
- ✅ Complete REST API with 8 endpoints
- ✅ Database (H2 in-memory) running
- ✅ All CRUD operations functional
- ✅ Attendance tracking and overtime calculations

### ⚠️ Known Limitations:
- ⚠️ Data is **NOT persistent** (using H2 in-memory database)
- ⚠️ Data is **lost when app stops**
- ⚠️ Supabase PostgreSQL connection blocked by network/firewall

---

## 🚀 Quick Start (3 Steps)

### 1️⃣ Open Terminal in Project Folder
```cmd
cd C:\Users\lavan\Employee-Management-System-Using-Spring-Boot-Thymeleaf-Java-Full-Stack
```

### 2️⃣ Run the Application
```cmd
.\mvnw.cmd spring-boot:run
```

Wait for: `Started EmployeeManagementSystemApplication in X.XXX seconds`

### 3️⃣ Open Your Browser
```
http://localhost:8080
```


## 🔌 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/workers` | POST | Create a new worker |
| `/api/sites` | POST | Create a new construction site |
| `/api/attendance/clock-in` | POST | Clock in worker to site |
| `/api/attendance/clock-out` | POST | Clock out worker |
| `/api/attendance/active` | GET | Get all currently active workers |
| `/api/attendance/history` | GET | Get attendance history for worker |
| `/api/overtime/summary` | GET | Get overtime summary for worker/month |
| `/api/overtime/settle` | POST | Settle overtime payment |

---

## 🧪 Testing the APIs

### Option 1: Web UI (Easiest)
1. Start the app: `.\mvnw.cmd spring-boot:run`
2. Open browser: http://localhost:8080
3. Use the interactive forms to test all APIs

### Option 2: cURL Commands
```cmd
# Create Worker
curl -X POST http://localhost:8080/api/workers -H "Content-Type: application/json" -d "{\"name\":\"John Doe\",\"phone\":\"1234567890\",\"designation\":\"MASON\",\"dailyWageRate\":500}"

# Create Site
curl -X POST http://localhost:8080/api/sites -H "Content-Type: application/json" -d "{\"siteName\":\"Site A\",\"location\":\"Downtown\"}"

# Clock In
curl -X POST http://localhost:8080/api/attendance/clock-in -H "Content-Type: application/json" -d "{\"workerId\":1,\"siteId\":1}"

# Get Active Workers
curl http://localhost:8080/api/attendance/active
```

---

## 🏗️ Features

### ✨ Core Functionality:
- **Worker Management** - Create and manage construction workers
- **Site Management** - Track multiple construction sites
- **Attendance Tracking** - Clock in/out with automatic time calculation
- **Overtime Calculation** - Automatic overtime detection (>8 hours/day)
- **Overtime Settlement** - Mark overtime as paid
- **Pagination** - All list endpoints support pagination
- **Real-time Active Workers** - See who's currently on-site

### 🎨 Interactive Web UI:
- Clean, modern purple gradient design
- 8 interactive API testing cards
- Real-time response display (green = success, red = error)
- No installation needed (plain HTML/CSS/JavaScript)

---
## 🛠️ Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.x** - Application framework
- **Spring MVC** - REST API framework
- **Spring Data JPA** - Database access
- **Hibernate** - ORM framework
- **Maven** - Build tool (Maven Wrapper included)
- **PostgreSQL** - Production database (Supabase)
- **H2 Database** - Development database (current)
- **Spring Security** - Security framework (currently permissive)
- **Redis** - Caching (optional)
- **Lombok** - Code generation
- **HTML/CSS/JavaScript** - Web UI

---


## AI Tools Used
- Claude (Anthropic): System architecture design, entity/service/repository 
  code, Redis caching strategy, overtime calculation logic, all 5 ticket 
  fixes (LF-201 to LF-205), error handling patterns
- Kiro IDE AI: Package refactoring, dependency management, running and 
  testing the application

## Forked From
vikashshaarma007/Employee-Management-System-Using-Spring-Boot-Thymeleaf-Java-Full-Stack
Chose this because: Java 17, Spring Boot 3.x, JPA, PostgreSQL stack — 
matches assignment requirements exactly. Clean structure made it easy 
to extend with the attendance feature.

Then run:
git add .
git commit -m "docs: add AI tools used and forked repo details to README"
git push origin main

---


## 📊 Database

### Current: H2 In-Memory Database
- **JDBC URL**: `jdbc:h2:mem:attendancedb`
- **Console**: http://localhost:8080/h2-console
- **Username**: `sa`
- **Password**: (blank)
- **⚠️ WARNING**: Data is lost when app stops!

### Future: Supabase PostgreSQL
- **Status**: Currently blocked by network/firewall
- **Fix**: See [ANSWERS_TO_YOUR_QUESTIONS.md](ANSWERS_TO_YOUR_QUESTIONS.md) for troubleshooting
- **Benefit**: Permanent data storage in cloud

---

## 🔧 Prerequisites

### Required:
- **Java 17** or higher
  ```cmd
  java -version
  ```
- **Maven Wrapper** (included in project - no Maven installation needed!)

### Optional:
- **PostgreSQL** (if not using Supabase)
- **Redis** (for caching features)

---

## 📦 Installation

### Step 1: Clone or Download Project
```cmd
git clone <your-repo-url>
cd Employee-Management-System-Using-Spring-Boot-Thymeleaf-Java-Full-Stack
```

### Step 2: Run the Application
```cmd
.\mvnw.cmd spring-boot:run
```

That's it! No database setup needed - H2 is configured automatically.

---

## 🎮 Usage Examples

### Complete Test Flow (5 minutes):

1. **Create a Worker**
   - Name: lavanya
   - Phone: 1234567890
   - Designation: MASON
   - Daily Wage: 500

2. **Create a Site**
   - Site Name: Construction Site A
   - Location: Downtown

3. **Clock In**
   - Worker ID: 1
   - Site ID: 1

4. **View Active Workers**
   - See John Doe is currently on-site

5. **Clock Out**
   - Worker ID: 1
   - View total hours and overtime

6. **View Attendance History**
   - Worker ID: 1
   - Date range: Last 30 days

7. **Get Overtime Summary**
   - Worker ID: 1
   - Month: 2026-06

8. **Settle Overtime**
   - Worker ID: 1
   - Month: 2026-06

---

## 🐛 Troubleshooting

### Port 8080 already in use?
```cmd
netstat -ano | findstr :8080
taskkill /F /PID [PID_NUMBER]
```

### UI not loading?
1. Check if app is running in terminal
2. Hard refresh: Ctrl + Shift + R
3. Try: http://127.0.0.1:8080

### API returns errors?
- Read the error message (it explains the problem)
- Make sure you created workers/sites before clock in
- Check data format (phone must be numbers, etc.)

### Want permanent data storage?
- Fix Supabase connection (see ANSWERS_TO_YOUR_QUESTIONS.md)
- Or install PostgreSQL locally

---

## 📝 Project Structure

```
src/main/java/edu/qs/attendance/
├── config/          - Configuration classes (Security, CORS, Redis)
├── controller/      - REST API endpoints
├── dto/             - Data Transfer Objects
├── entity/          - Database entities (Worker, Site, AttendanceLog, etc.)
├── event/           - Application events
├── exception/       - Exception handling
├── listener/        - Event listeners
├── repository/      - Database repositories
└── service/         - Business logic

src/main/resources/
├── static/          - Web UI (index.html)
└── application.properties - Configuration
```

---

## 🤝 Contributing

Feel free to fork this project and submit pull requests!

---

## 📞 Support

For issues and questions:
1. Check [ANSWERS_TO_YOUR_QUESTIONS.md](ANSWERS_TO_YOUR_QUESTIONS.md)
2. Check [HOW_TO_TEST_APIs.md](HOW_TO_TEST_APIs.md)
3. Review terminal error messages

---

## 📄 License

This project is for educational purposes.

---

## 🎉 Acknowledgments

- Spring Boot team for the excellent framework
- Supabase for cloud PostgreSQL hosting
- All contributors and testers

---

<div align="center">
  <p><strong>Ready to start? Run: <code>.\mvnw.cmd spring-boot:run</code></strong></p>
  <p><strong>Then open: <a href="http://localhost:8080">http://localhost:8080</a></strong></p>
</div>

