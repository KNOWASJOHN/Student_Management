// [file name]: MySQLService.java
package com.example.studentapp.service;

import com.example.studentapp.database.DatabaseConnection;
import com.example.studentapp.model.Student;
import com.example.studentapp.model.Admin;
import com.example.studentapp.model.UserRole;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * MySQL service class to replace FirebaseService
 * Handles all database operations for students and admins
 */
public class MySQLService {
    private static MySQLService instance;

    /**
     * Private constructor for Singleton pattern
     */
    private MySQLService() {
        initializeDatabase();
    }

    /**
     * Gets singleton instance of MySQLService
     */
    public static synchronized MySQLService getInstance() {
        if (instance == null) {
            instance = new MySQLService();
        }
        return instance;
    }

    /**
     * Initializes database tables if they don't exist
     */
    private void initializeDatabase() {
        String createStudentsTable = """
            CREATE TABLE IF NOT EXISTS students (
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
            )
        """;

        String createAdminsTable = """
            CREATE TABLE IF NOT EXISTS admins (
                admin_id VARCHAR(50) PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(100) NOT NULL,
                email VARCHAR(100),
                name VARCHAR(100) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create tables
            stmt.execute(createStudentsTable);
            stmt.execute(createAdminsTable);

            System.out.println("✅ Database tables initialized successfully");

            // Create indexes (ignore errors if they already exist)
            createIndexIfNotExists(conn, "idx_student_email", "CREATE INDEX idx_student_email ON students(email)");
            createIndexIfNotExists(conn, "idx_admin_username", "CREATE INDEX idx_admin_username ON admins(username)");

            // Create default admin
            createDefaultAdmin();

        } catch (SQLException e) {
            System.err.println("❌ Failed to initialize database: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Database initialization failed: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Helper method to create index if it doesn't exist
     */
    private void createIndexIfNotExists(Connection conn, String indexName, String createIndexSql) {
        try {
            // Check if index exists
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet indexes = meta.getIndexInfo(null, null, "students", false, false);

            boolean indexExists = false;
            while (indexes.next()) {
                if (indexName.equalsIgnoreCase(indexes.getString("INDEX_NAME"))) {
                    indexExists = true;
                    break;
                }
            }

            if (!indexExists) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createIndexSql);
                    System.out.println("✅ Index created: " + indexName);
                }
            }
        } catch (SQLException e) {
            System.out.println("ℹ️ Index might already exist: " + indexName + " - " + e.getMessage());
        }
    }

    /**
     * Authenticates admin by username and password
     */
    public CompletableFuture<Admin> authenticateAdmin(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, username);
                pstmt.setString(2, password);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setAdminId(rs.getString("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPassword(rs.getString("password"));
                    admin.setEmail(rs.getString("email"));
                    admin.setName(rs.getString("name"));

                    System.out.println("✅ Admin authentication successful for: " + username);
                    return admin;
                }

                System.out.println("❌ Admin authentication failed for: " + username);
                return null;

            } catch (SQLException e) {
                System.err.println("❌ Admin authentication error: " + e.getMessage());
                throw new RuntimeException("Admin authentication failed: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Authenticates student by email and password
     */
    public CompletableFuture<Student> authenticateStudent(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM students WHERE email = ? AND password = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, email);
                pstmt.setString(2, password);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    Student student = resultSetToStudent(rs);
                    System.out.println("✅ Student authentication successful for: " + email);
                    return student;
                }

                System.out.println("❌ Student authentication failed for: " + email);
                return null;

            } catch (SQLException e) {
                System.err.println("❌ Student authentication error: " + e.getMessage());
                throw new RuntimeException("Student authentication failed: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Retrieves all students from database
     */
    public CompletableFuture<List<Student>> getAllStudents() {
        return CompletableFuture.supplyAsync(() -> {
            List<Student> students = new ArrayList<>();
            String sql = "SELECT * FROM students ORDER BY name";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    students.add(resultSetToStudent(rs));
                }

                return students;

            } catch (SQLException e) {
                System.err.println("❌ Failed to fetch students: " + e.getMessage());
                throw new RuntimeException("Failed to fetch students: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Retrieves student by ID
     */
    public CompletableFuture<Student> getStudentById(String studentId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM students WHERE student_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, studentId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return resultSetToStudent(rs);
                }

                return null;

            } catch (SQLException e) {
                System.err.println("❌ Failed to fetch student: " + e.getMessage());
                throw new RuntimeException("Failed to fetch student: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Adds a new student to the database
     */
    public CompletableFuture<String> addStudent(Student student) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = """
                INSERT INTO students (student_id, name, email, password, branch, semester, hostel, phone, role)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            String studentId = generateStudentId();

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, studentId);
                pstmt.setString(2, student.getName());
                pstmt.setString(3, student.getEmail());
                pstmt.setString(4, student.getPassword());
                pstmt.setString(5, student.getBranch());
                pstmt.setString(6, student.getSemester());
                pstmt.setBoolean(7, student.isHostel());
                pstmt.setString(8, student.getPhone());
                pstmt.setString(9, student.getRole() != null ? student.getRole().toString() : "STUDENT");

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    student.setStudentId(studentId);
                    return studentId;
                } else {
                    throw new RuntimeException("Failed to add student: No rows affected");
                }

            } catch (SQLException e) {
                System.err.println("❌ Failed to add student: " + e.getMessage());

                // Handle duplicate email error
                if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
                    throw new RuntimeException("Email already exists: " + student.getEmail());
                }

                throw new RuntimeException("Failed to add student: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Updates an existing student in the database
     */
    public CompletableFuture<Void> updateStudent(String studentId, Student student) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = """
                UPDATE students 
                SET name = ?, email = ?, password = ?, branch = ?, semester = ?, hostel = ?, phone = ?, role = ?
                WHERE student_id = ?
            """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getEmail());
                pstmt.setString(3, student.getPassword());
                pstmt.setString(4, student.getBranch());
                pstmt.setString(5, student.getSemester());
                pstmt.setBoolean(6, student.isHostel());
                pstmt.setString(7, student.getPhone());
                pstmt.setString(8, student.getRole() != null ? student.getRole().toString() : "STUDENT");
                pstmt.setString(9, studentId);

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new RuntimeException("Student not found with ID: " + studentId);
                }

                return null;

            } catch (SQLException e) {
                System.err.println("❌ Failed to update student: " + e.getMessage());

                // Handle duplicate email error
                if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
                    throw new RuntimeException("Email already exists: " + student.getEmail());
                }

                throw new RuntimeException("Failed to update student: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Deletes a student from the database
     */
    public CompletableFuture<Void> deleteStudent(String studentId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "DELETE FROM students WHERE student_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, studentId);

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new RuntimeException("Student not found with ID: " + studentId);
                }

                return null;

            } catch (SQLException e) {
                System.err.println("❌ Failed to delete student: " + e.getMessage());
                throw new RuntimeException("Failed to delete student: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Checks if email already exists in database
     */
    public CompletableFuture<Boolean> emailExists(String email, String excludeStudentId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql;
            PreparedStatement pstmt;

            try (Connection conn = DatabaseConnection.getConnection()) {
                if (excludeStudentId != null && !excludeStudentId.isEmpty()) {
                    sql = "SELECT COUNT(*) as count FROM students WHERE email = ? AND student_id != ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, email);
                    pstmt.setString(2, excludeStudentId);
                } else {
                    sql = "SELECT COUNT(*) as count FROM students WHERE email = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, email);
                }

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }

                return false;

            } catch (SQLException e) {
                System.err.println("❌ Email check failed: " + e.getMessage());
                throw new RuntimeException("Email check failed: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Creates default admin account if not exists
     */
    public void createDefaultAdmin() {
        String checkSql = "SELECT COUNT(*) as count FROM admins WHERE username = 'admin'";
        String insertSql = """
            INSERT INTO admins (admin_id, username, password, email, name) 
            VALUES (?, 'admin', 'admin123', 'admin@school.com', 'System Administrator')
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt("count") == 0) {
                // Create default admin
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, generateAdminId());
                    pstmt.executeUpdate();
                    System.out.println("✅ Default admin account created");
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to create default admin: " + e.getMessage());
        }
    }

    /**
     * Converts ResultSet to Student object
     */
    private Student resultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getString("student_id"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setPassword(rs.getString("password"));
        student.setBranch(rs.getString("branch"));
        student.setSemester(rs.getString("semester"));
        student.setHostel(rs.getBoolean("hostel"));
        student.setPhone(rs.getString("phone"));

        String roleStr = rs.getString("role");
        if (roleStr != null) {
            student.setRole(UserRole.valueOf(roleStr));
        } else {
            student.setRole(UserRole.STUDENT);
        }

        return student;
    }

    /**
     * Generates unique student ID
     */
    private String generateStudentId() {
        return "STU_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    /**
     * Generates unique admin ID
     */
    private String generateAdminId() {
        return "ADM_" + System.currentTimeMillis();
    }
}