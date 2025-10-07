package com.example.studentapp.view;

import com.example.studentapp.controller.StudentController;
import com.example.studentapp.model.Admin;
import com.example.studentapp.model.Student;
import com.example.studentapp.model.UserRole;
import com.example.studentapp.service.QRCodeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Dashboard Frame with role-based access control
 * Admin: Full CRUD access to all students
 * Student: Read-only access to own profile only
 */
public class DashboardFrame extends JFrame {
    private StudentController controller;
    private QRCodeService qrCodeService;
    private Admin admin;
    private Student student;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, logoutButton, qrCodeButton;
    private JLabel userInfoLabel;
    private JPanel mainPanel;

    /**
     * Constructor for role-based dashboard
     * @param admin Admin user (null if student)
     * @param student Student user (null if admin)
     */
    public DashboardFrame(Admin admin, Student student) {
        this.controller = new StudentController();
        this.qrCodeService = new QRCodeService(); // Initialize QR code service
        this.admin = admin;
        this.student = student;
        initializeUI();

        if (isAdmin()) {
            loadAllStudents();
        } else {
            loadStudentProfile();
        }
    }

    /**
     * Check if current user is admin
     */
    private boolean isAdmin() {
        return admin != null;
    }

    /**
     * Check if current user is student
     */
    private boolean isStudent() {
        return student != null;
    }

    /**
     * Initializes UI based on user role
     */
    private void initializeUI() {
        setTitle("Student Management System - " + (isAdmin() ? "Admin Dashboard" : "Student Portal"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Main panel with border layout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header panel with user info and logout button
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        if (isAdmin()) {
            // Admin view: Full dashboard with CRUD operations
            setupAdminView(mainPanel);
        } else {
            // Student view: Personal profile only
            setupStudentView(mainPanel);
        }

        add(mainPanel);
    }

    /**
     * Creates header panel with user information and logout button
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(240, 240, 240));

        // User info
        String userInfo;
        if (isAdmin()) {
            userInfo = String.format("Admin: %s (%s) | Role: Administrator",
                    admin.getName(), admin.getEmail());
        } else {
            userInfo = String.format("Student: %s (%s) | %s | Semester: %s | Role: Student",
                    student.getName(), student.getEmail(), student.getBranch(), student.getSemester());
        }

        userInfoLabel = new JLabel(userInfo);
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(userInfoLabel, BorderLayout.WEST);

        // Logout button - ALWAYS VISIBLE for both admin and student
        logoutButton = createLogoutButton();
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(new Color(240, 240, 240));
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Creates a styled logout button
     */
    private JButton createLogoutButton() {
        JButton logoutBtn = new JButton("Logout"); // Add icon for better visibility
        logoutBtn.setBackground(new Color(220, 53, 69)); // Red color
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25)); // Larger padding
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14)); // Larger font
        logoutBtn.setToolTipText("Click to logout from the system");

        // Add hover effect
        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(new Color(200, 35, 51)); // Darker red on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(new Color(220, 53, 69)); // Original red
            }
        });

        // Add logout functionality
        logoutBtn.addActionListener(e -> logout());

        return logoutBtn;
    }

    /**
     * Sets up admin view with full CRUD capabilities
     */
    private void setupAdminView(JPanel mainPanel) {
        // Title label
        JLabel titleLabel = new JLabel("Student Management - Admin Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 100, 200));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Button panel for admin
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        addButton = createButton("Add Student", new Color(40, 167, 69));
        editButton = createButton("Edit Student", new Color(255, 193, 7));
        deleteButton = createButton("Delete Student", new Color(220, 53, 69));
        refreshButton = createButton("Refresh", new Color(108, 117, 125));
        qrCodeButton = createButton("Generate QR Code", new Color(0, 123, 255));

        // ADD EXTRA LOGOUT BUTTON FOR ADMIN IN BUTTON PANEL
        JButton adminLogoutBtn = createButton("Logout", new Color(220, 53, 69));
        adminLogoutBtn.addActionListener(e -> logout());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(qrCodeButton);
        buttonPanel.add(adminLogoutBtn); // ADD EXTRA LOGOUT BUTTON

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Table setup for admin
        setupStudentTable();
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Status label for admin
        JLabel statusLabel = new JLabel("Admin View: You can view, add, edit, and delete all student records");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setBackground(Color.LIGHT_GRAY);
        statusLabel.setOpaque(true);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        // Add action listeners for admin
        setupAdminEventListeners();
    }

    /**
     * Sets up student view with read-only profile
     */
    private void setupStudentView(JPanel mainPanel) {
        // Title label
        JLabel titleLabel = new JLabel("Student Portal - My Profile", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 100, 200));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create profile panel
        JPanel profilePanel = createStudentProfilePanel();
        JScrollPane scrollPane = new JScrollPane(profilePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add QR code button for hostel students AND logout button
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Status label for student
        JLabel statusLabel = new JLabel("Student View: You can only view your own profile. Contact admin for changes.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setBackground(Color.LIGHT_GRAY);
        statusLabel.setOpaque(true);
        bottomPanel.add(statusLabel, BorderLayout.NORTH);

        // Button panel for student (QR Code + Additional Logout)
        JPanel studentButtonPanel = new JPanel(new FlowLayout());

        if (student.isHostel()) {
            JButton studentQRButton = createButton("Generate My QR Code", new Color(0, 123, 255));
            studentQRButton.addActionListener(e -> {
                qrCodeService.showQRCodeDialog(student, DashboardFrame.this);
            });
            studentButtonPanel.add(studentQRButton);
        }

        // ADD EXTRA LOGOUT BUTTON FOR STUDENT
        JButton extraLogoutBtn = createButton("Logout", new Color(220, 53, 69));
        extraLogoutBtn.addActionListener(e -> logout());
        studentButtonPanel.add(extraLogoutBtn);

        bottomPanel.add(studentButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates student profile panel (read-only)
     */
    private JPanel createStudentProfilePanel() {
        String title = "Personal Information";
        if (student.isHostel()) {
            title += " 🏠 Hostel Student";
        }

        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 100, 200), 2),
                title));
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setPreferredSize(new Dimension(500, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Create read-only fields
        JTextField nameField = createReadOnlyField(student.getName());
        JTextField emailField = createReadOnlyField(student.getEmail());
        JTextField branchField = createReadOnlyField(student.getBranch());
        JTextField semesterField = createReadOnlyField(student.getSemester());
        JTextField hostelField = createReadOnlyField(student.isHostel() ? "Yes" : "No");
        JTextField phoneField = createReadOnlyField(student.getPhone());
        JTextField roleField = createReadOnlyField("Student");

        // Add fields to panel
        addFormRow(profilePanel, gbc, "Full Name:", nameField, 0);
        addFormRow(profilePanel, gbc, "Email:", emailField, 1);
        addFormRow(profilePanel, gbc, "Branch:", branchField, 2);
        addFormRow(profilePanel, gbc, "Semester:", semesterField, 3);
        addFormRow(profilePanel, gbc, "Hostel Accommodation:", hostelField, 4);
        addFormRow(profilePanel, gbc, "Phone Number:", phoneField, 5);
        addFormRow(profilePanel, gbc, "Role:", roleField, 6);

        return profilePanel;
    }

    /**
     * Creates a read-only text field
     */
    private JTextField createReadOnlyField(String text) {
        JTextField field = new JTextField(text, 25);
        field.setEditable(false);
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        return field;
    }

    /**
     * Adds a form row to profile panel
     */
    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.NONE;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    /**
     * Sets up student table for admin view
     */
    private void setupStudentTable() {
        String[] columnNames = {
                "Student ID", "Name", "Email", "Branch", "Semester", "Hostel", "Phone", "Role"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setReorderingAllowed(false);
        studentTable.setRowHeight(30);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Enable double-click to edit (admin only)
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && isAdmin()) {
                    editSelectedStudent();
                }
            }
        });
    }

    /**
     * Creates a styled button
     */
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFont(new Font("Arial", Font.BOLD, 12));

        // Add hover effect for all buttons
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    /**
     * Sets up event listeners for admin operations
     */
    private void setupAdminEventListeners() {
        addButton.addActionListener(e -> showStudentForm(null));
        editButton.addActionListener(e -> editSelectedStudent());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        refreshButton.addActionListener(e -> loadAllStudents());
        qrCodeButton.addActionListener(e -> generateQRCodeForSelectedStudent());
    }

    /**
     * Loads all students for admin view
     */
    private void loadAllStudents() {
        refreshButton.setEnabled(false);
        refreshButton.setText("Loading...");

        new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                return controller.getAllStudents().get();
            }

            @Override
            protected void done() {
                refreshButton.setEnabled(true);
                refreshButton.setText("Refresh");

                try {
                    List<Student> students = get();
                    updateTable(students);
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(DashboardFrame.this,
                            "Failed to load students: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Loads single student profile for student view
     */
    private void loadStudentProfile() {
        // Student profile is already loaded in constructor
        System.out.println("Loaded student profile for: " + student.getName());
    }

    /**
     * Updates the table with student data (admin only)
     */
    private void updateTable(List<Student> students) {
        tableModel.setRowCount(0); // Clear existing data

        for (Student student : students) {
            Object[] rowData = {
                    student.getStudentId(),
                    student.getName(),
                    student.getEmail(),
                    student.getBranch(),
                    student.getSemester(),
                    student.isHostel() ? "Yes" : "No",
                    student.getPhone(),
                    student.getRole() != null ? student.getRole().toString() : "STUDENT"
            };
            tableModel.addRow(rowData);
        }

        // Show success message
        JOptionPane.showMessageDialog(this,
                "Loaded " + students.size() + " students",
                "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows student form for adding or editing (admin only)
     */
    private void showStudentForm(Student student) {
        if (!isAdmin()) {
            JOptionPane.showMessageDialog(this,
                    "Only administrators can manage student records.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        StudentFormPanel formPanel = new StudentFormPanel(student, this.controller, this);
        formPanel.setVisible(true);
    }

    /**
     * Edits the currently selected student (admin only)
     */
    private void editSelectedStudent() {
        if (!isAdmin()) {
            JOptionPane.showMessageDialog(this,
                    "Only administrators can edit student records.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to edit",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);

        new SwingWorker<Student, Void>() {
            @Override
            protected Student doInBackground() throws Exception {
                return controller.getStudentById(studentId).get();
            }

            @Override
            protected void done() {
                try {
                    Student student = get();
                    if (student != null) {
                        showStudentForm(student);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(DashboardFrame.this,
                            "Failed to load student: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Deletes the currently selected student (admin only)
     */
    private void deleteSelectedStudent() {
        if (!isAdmin()) {
            JOptionPane.showMessageDialog(this,
                    "Only administrators can delete student records.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to delete",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student: " + studentName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            deleteButton.setEnabled(false);
            deleteButton.setText("Deleting...");

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.deleteStudent(studentId).get();
                    return null;
                }

                @Override
                protected void done() {
                    deleteButton.setEnabled(true);
                    deleteButton.setText("Delete Student");

                    try {
                        get(); // Check for exceptions
                        JOptionPane.showMessageDialog(DashboardFrame.this,
                                "Student deleted successfully",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadAllStudents(); // Refresh the table
                    } catch (InterruptedException | ExecutionException e) {
                        JOptionPane.showMessageDialog(DashboardFrame.this,
                                "Failed to delete student: " + e.getCause().getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    public void refreshStudentTable() {
        if (isAdmin()) {
            loadAllStudents();
        }
    }

    /**
     * Logs out and returns to login screen
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Show logout message
            String userType = isAdmin() ? "Admin" : "Student";
            String userName = isAdmin() ? admin.getName() : student.getName();

            JOptionPane.showMessageDialog(this,
                    "Goodbye " + userName + "!",
                    "Logout Successful", JOptionPane.INFORMATION_MESSAGE);

            // Return to login screen
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    /**
     * Generates QR code for selected student (admin only)
     */
    private void generateQRCodeForSelectedStudent() {
        if (!isAdmin()) {
            JOptionPane.showMessageDialog(this,
                    "Only administrators can generate QR codes.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to generate QR code",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);
        String hostelStatus = (String) tableModel.getValueAt(selectedRow, 5);

        if (!"Yes".equals(hostelStatus)) {
            JOptionPane.showMessageDialog(this,
                    "QR Code is only available for hostel students.\n" +
                            "Student '" + studentName + "' does not have hostel accommodation.",
                    "Not a Hostel Student", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Load student and show QR code
        new SwingWorker<Student, Void>() {
            @Override
            protected Student doInBackground() throws Exception {
                return controller.getStudentById(studentId).get();
            }

            @Override
            protected void done() {
                try {
                    Student student = get();
                    if (student != null) {
                        qrCodeService.showQRCodeDialog(student, DashboardFrame.this);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DashboardFrame.this,
                            "Failed to load student: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}