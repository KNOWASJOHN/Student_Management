package com.example.studentapp.model;

/**
 * Model class representing a Student entity
 */
public class Student {
    private String studentId;
    private String name;
    private String email;
    private String password;
    private String branch;
    private String semester;
    private boolean hostel;
    private String phone;
    private UserRole role;

    // Constructors, getters, and setters
    public Student() {}

    /**
     * Parameterized constructor for creating student instances
     */
    public Student(String studentId, String name, String email, String password,
                   String branch, String semester, boolean hostel, String phone, UserRole role) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.branch = branch;
        this.semester = semester;
        this.hostel = hostel;
        this.phone = phone;
        this.role = role;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public boolean isHostel() { return hostel; }
    public void setHostel(boolean hostel) { this.hostel = hostel; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // NEW: Role getter and setter
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    /**
     * String representation of Student object
     */
    @Override
    public String toString() {
        return String.format("Student{id=%s, name=%s, email=%s, branch=%s, semester=%s, hostel=%s, phone=%s, role=%s}",
                studentId, name, email, branch, semester, hostel, phone, role);
    }
}