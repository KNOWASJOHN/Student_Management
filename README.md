# Student Management System

A comprehensive Java-based Student Management System with role-based access control, MySQL database integration, and QR code generation for hostel students.

## 🚀 Features

### Authentication & Authorization
- **Dual Login System**: Separate login for Admin and Student users
- **Role-Based Access Control**:
  - **Admin**: Full CRUD operations on all student records
  - **Student**: Read-only access to personal profile only

### Student Management
- Add new students with comprehensive information
- Edit existing student records
- Delete student accounts
- View all students (Admin only)
- Email validation and duplicate prevention

### Special Features
- **QR Code Generation**: Automatic QR code generation for hostel students containing their information
- **Hostel Management**: Track hostel accommodation status
- **Real-time Validation**: Form validation with immediate feedback

## 🛠️ Technology Stack

- **Backend**: Java 11+
- **Database**: MySQL 8.0+
- **UI**: Java Swing
- **QR Generation**: ZXing Library
- **Connection Pooling**: HikariCP
- **Build Tool**: Maven

## 📋 Prerequisites

Before running this application, ensure you have:

- Java JDK 11 or higher
- MySQL Server 8.0 or higher
- Maven 3.6 or higher

## 🗄️ Database Setup

### 1. Create Database
```sql
CREATE DATABASE student_management;
USE student_management;
```

### 2. Create Tables (Automatically created on first run, or manually):
```sql
CREATE TABLE students (
    student_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    branch VARCHAR(50) NOT NULL,
    semester VARCHAR(20) NOT NULL,
    hostel BOOLEAN DEFAULT FALSE,
    phone VARCHAR(15),
    role VARCHAR(20) DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE admins (
    admin_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_student_email ON students(email);
CREATE INDEX idx_admin_username ON admins(username);
```

### 3. Default Admin Account
The system automatically creates a default admin account:
- **Username**: `admin`
- **Password**: `admin123`

## ⚙️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/KNOWASJOHN/Student_Management.git
cd Student_Management
```

### 2. Database Configuration
Create `src/main/resources/database.properties`:
```properties
# Database Configuration
db.url=jdbc:mysql://localhost:3306/student_management
db.username=your_mysql_username
db.password=your_mysql_password
db.pool.size=10
```

### 3. Build the Project
```bash
mvn clean compile
```

### 4. Run the Application
```bash
mvn exec:java -Dexec.mainClass="com.example.studentapp.Main"
```

### Alternative: Create Executable JAR
```bash
mvn clean package
java -jar target/student-management-system-1.0.0.jar
```

## 👥 User Roles & Permissions

### Administrator
- ✅ View all student records
- ✅ Add new students
- ✅ Edit existing student information
- ✅ Delete student accounts
- ✅ Generate QR codes for any hostel student
- ✅ Access comprehensive dashboard

### Student
- ✅ View personal profile only
- ✅ Generate personal QR code (if hostel student)
- ❌ Cannot modify any information
- ❌ Cannot view other student records

## 📱 QR Code Feature

### For Hostel Students
- Generates QR codes containing:
  - Student ID and Name
  - Contact Information
  - Branch and Semester
  - Hostel Status
- QR codes can be saved as PNG files
- Useful for identification and access control

### Access
- **Students**: Can generate their own QR code
- **Admins**: Can generate QR codes for any hostel student

## 🗂️ Project Structure

```
src/main/java/com/example/studentapp/
├── Main.java                 # Application entry point
├── config/
│   └── DatabaseConfig.java   # Database configuration loader
├── database/
│   └── DatabaseConnection.java # MySQL connection pool
├── model/                    # Data models
│   ├── Student.java
│   ├── Admin.java
│   └── UserRole.java
├── service/                  # Business logic
│   ├── MySQLService.java     # Database operations
│   └── QRCodeService.java    # QR code generation
├── controller/               # Controllers
│   └── StudentController.java
└── view/                     # UI components
    ├── LoginFrame.java
    ├── DashboardFrame.java
    ├── StudentFormPanel.java
    └── SignupFrame.java
```

## 🔧 Configuration

### Database Properties
The application uses a connection pool with the following configurable properties:
- Connection pool size
- Timeout settings
- MySQL performance optimizations

### QR Code Settings
- Size: 300x300 pixels
- Format: PNG
- Content: Student information in readable text format

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify MySQL server is running
   - Check database credentials in `database.properties`
   - Ensure database and tables exist

2. **QR Code Generation Fails**
   - Ensure ZXing dependencies are included
   - Check file write permissions for saving QR codes

3. **Login Issues**
   - Default admin credentials: admin/admin123
   - Verify student email and password match

### Logs
- Application logs are output to console
- Database operations are logged with emoji indicators
- Error messages include detailed context

## 🔒 Security Features

- Password hashing (implement hashing in future versions)
- SQL injection prevention using prepared statements
- Input validation on all forms
- Role-based access control
- No plain text credentials in code

## 🚀 Future Enhancements

- [ ] Password encryption/hashing
- [ ] Email verification for student registration
- [ ] Password reset functionality
- [ ] Advanced reporting and analytics
- [ ] Bulk student import/export
- [ ] Attendance tracking system
- [ ] Fee management module
- [ ] Mobile app companion

## 👨‍💻 Developer

- **John** - [KNOWASJOHN](https://github.com/KNOWASJOHN)
- **Athuljith** - [AthulJithVasudev](https://github.com/AthulJithVasudev)
