package com.example.studentapp.view;

import com.example.studentapp.controller.StudentController;
import com.example.studentapp.model.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import com.example.studentapp.model.UserRole;





/**
 * Form panel for adding/editing student information
 * Reusable component for both create and update operations
 */
public class StudentFormPanel extends JDialog {
    private StudentController controller;
    private DashboardFrame parentFrame;
    private Student existingStudent;

    private JTextField nameField, emailField, passwordField, branchField, semesterField, phoneField;
    private JCheckBox hostelCheckBox;
    private JButton saveButton, cancelButton;

    /**
     * Constructor for student form
     * @param student existing student (null for new student)
     * @param controller student controller
     * @param parentFrame parent dashboard frame
     */
    public StudentFormPanel(Student student, StudentController controller, DashboardFrame parentFrame) {
        super(parentFrame, true); // Modal dialog
        this.controller = controller;
        this.parentFrame = parentFrame;
        this.existingStudent = student;

        initializeUI();
        if (student != null) {
            populateForm(student);
        }
    }

    /**
     * Initializes the form UI components
     */
    private void initializeUI() {
        setTitle(existingStudent == null ? "Add New Student" : "Edit Student");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(parentFrame);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Initialize form fields
        initializeFormFields();

        // Add form components
        addFormRow(formPanel, gbc, "Name:", nameField, 0);
        addFormRow(formPanel, gbc, "Email:", emailField, 1);
        addFormRow(formPanel, gbc, "Password:", passwordField, 2);
        addFormRow(formPanel, gbc, "Branch:", branchField, 3);
        addFormRow(formPanel, gbc, "Semester:", semesterField, 4);

        // Hostel checkbox row
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Hostel Accommodation:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(hostelCheckBox, gbc);

        addFormRow(formPanel, gbc, "Phone:", phoneField, 6);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        setupEventListeners();

        add(mainPanel);
    }

    /**
     * Initializes all form fields
     */
    private void initializeFormFields() {
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        branchField = new JTextField(20);
        semesterField = new JTextField(20);
        phoneField = new JTextField(20);
        hostelCheckBox = new JCheckBox();
    }

    /**
     * Adds a form row with label and field
     * @param panel parent panel
     * @param gbc constraints
     * @param labelText field label
     * @param field input field
     * @param row row number
     */
    private void addFormRow(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        panel.add(field, gbc);
    }

    /**
     * Sets up event listeners for buttons
     */
    private void setupEventListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveStudent();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * Populates form with existing student data
     * @param student student data to populate
     */
    private void populateForm(Student student) {
        nameField.setText(student.getName());
        emailField.setText(student.getEmail());
        passwordField.setText(student.getPassword());
        branchField.setText(student.getBranch());
        semesterField.setText(student.getSemester());
        hostelCheckBox.setSelected(student.isHostel());
        phoneField.setText(student.getPhone());
    }

    /**
     * Validates form input and saves student data
     */
    private void saveStudent() {
        // Collect and validate form data
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(((JPasswordField) passwordField).getPassword()).trim();
        String branch = branchField.getText().trim();
        String semester = semesterField.getText().trim();
        boolean hostel = hostelCheckBox.isSelected();
        String phone = phoneField.getText().trim();
        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                branch.isEmpty() || semester.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create student object
        Student student = new Student();
        student.setName(name);
        student.setEmail(email);
        student.setPassword(password);
        student.setBranch(branch);
        student.setSemester(semester);
        student.setHostel(hostel);
        student.setPhone(phone);
        student.setRole(UserRole.STUDENT);

        // Show loading
        saveButton.setEnabled(false);
        saveButton.setText("Saving...");

        // Perform save operation
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Check if email already exists (for new students or when email changed)
                if (existingStudent == null || !existingStudent.getEmail().equals(email)) {
                    boolean emailExists = controller.emailExists(email,
                            existingStudent != null ? existingStudent.getStudentId() : "").get();
                    if (emailExists) {
                        throw new Exception("Email already exists: " + email);
                    }
                }

                // Save student
                if (existingStudent == null) {
                    controller.addStudent(student).get();
                } else {
                    student.setStudentId(existingStudent.getStudentId());
                    controller.updateStudent(student).get();
                }
                return true;
            }

            @Override
            protected void done() {
                saveButton.setEnabled(true);
                saveButton.setText("Save");

                try {
                    get(); // Check for exceptions
                    JOptionPane.showMessageDialog(StudentFormPanel.this,
                            "Student " + (existingStudent == null ? "added" : "updated") + " successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    parentFrame.refreshStudentTable();
                    dispose();
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(StudentFormPanel.this,
                            "Failed to save student: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    // Add this method to StudentFormPanel.java if you want a logout button there too
    private JButton createLogoutButton() {
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(StudentFormPanel.this,
                    "Are you sure you want to logout?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                parentFrame.dispose();
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        return logoutBtn;
    }

// Then add it to your button panel in initializeUI():
// In the button panel section, add:
// buttonPanel.add(createLogoutButton());

    /**
     * Basic email validation
     * @param email email to validate
     * @return true if valid email format
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}

