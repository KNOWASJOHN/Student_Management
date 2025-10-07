// [file name]: StudentController.java
package com.example.studentapp.controller;

import com.example.studentapp.model.Student;
import com.example.studentapp.model.Admin;
import com.example.studentapp.service.MySQLService; // Change this import

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller class handling business logic between UI and MySQL service
 */
public class StudentController {
    private MySQLService mySQLService; // Change this

    /**
     * Constructor - initializes MySQL service
     */
    public StudentController() {
        this.mySQLService = MySQLService.getInstance(); // Change this
    }

    /**
     * Authenticates a student with email and password
     */
    public CompletableFuture<Student> authenticateStudent(String email, String password) {
        return mySQLService.authenticateStudent(email, password); // Change this
    }

    /**
     * Authenticates an admin with username and password
     */
    public CompletableFuture<Admin> authenticateAdmin(String username, String password) {
        return mySQLService.authenticateAdmin(username, password); // Change this
    }

    /**
     * Retrieves all students from database
     */
    public CompletableFuture<List<Student>> getAllStudents() {
        return mySQLService.getAllStudents(); // Change this
    }

    /**
     * Retrieves a specific student by ID
     */
    public CompletableFuture<Student> getStudentById(String studentId) {
        return mySQLService.getStudentById(studentId); // Change this
    }

    /**
     * Adds a new student to the database
     */
    public CompletableFuture<String> addStudent(Student student) {
        return mySQLService.addStudent(student); // Change this
    }

    /**
     * Updates an existing student in the database
     */
    public CompletableFuture<Void> updateStudent(Student student) {
        return mySQLService.updateStudent(student.getStudentId(), student); // Change this
    }

    /**
     * Deletes a student from the database
     */
    public CompletableFuture<Void> deleteStudent(String studentId) {
        return mySQLService.deleteStudent(studentId); // Change this
    }

    /**
     * Checks if email already exists in database
     */
    public CompletableFuture<Boolean> emailExists(String email, String excludeStudentId) {
        return mySQLService.emailExists(email, excludeStudentId); // Change this
    }

    /**
     * Creates default admin account for first-time setup
     */
    public void createDefaultAdmin() {
        mySQLService.createDefaultAdmin(); // Change this
    }
}