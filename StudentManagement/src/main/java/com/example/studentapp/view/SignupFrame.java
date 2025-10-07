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
 * Signup Frame for new student registration
 * Allows new students to create an account in the system
 */
public class SignupFrame extends JDialog {
    private StudentController controller;
    private JTextField nameField, emailField, branchField, semesterField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JCheckBox hostelCheckBox;
    private JButton signupButton, cancelButton;
    private LoginFrame loginFrame;

    /**
     * Constructor for student signup form
     * @param loginFrame parent login frame
     */
    public SignupFrame(LoginFrame loginFrame) {
        super(loginFrame, "Student Registration", true);
        this.controller = new StudentController();
        this.loginFrame = loginFrame;
        initializeUI();
    }

    /**
     * Initializes the signup UI components
     */
    private void initializeUI() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(loginFrame);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title label
        JLabel titleLabel = new JLabel("Student Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 100, 200));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

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
        addFormRow(formPanel, gbc, "Full Name:*", nameField, 0);
        addFormRow(formPanel, gbc, "Email:*", emailField, 1);
        addPasswordRow(formPanel, gbc, "Password:*", passwordField, 2);
        addPasswordRow(formPanel, gbc, "Confirm Password:*", confirmPasswordField, 3);
        addFormRow(formPanel, gbc, "Branch:*", branchField, 4);
        addFormRow(formPanel, gbc, "Semester:*", semesterField, 5);

        // Hostel checkbox row
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Hostel Accommodation:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(hostelCheckBox, gbc);

        addFormRow(formPanel, gbc, "Phone Number:*", phoneField, 7);

        // Required fields note
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        JLabel requiredLabel = new JLabel("* Required fields");
        requiredLabel.setForeground(Color.RED);
        requiredLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        formPanel.add(requiredLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        signupButton = new JButton("Create Account");
        cancelButton = new JButton("Cancel");

        // Style buttons
        signupButton.setBackground(new Color(40, 167, 69));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        buttonPanel.add(signupButton);
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
        confirmPasswordField = new JPasswordField(20);
        branchField = new JTextField(20);
        semesterField = new JTextField(20);
        phoneField = new JTextField(20);
        hostelCheckBox = new JCheckBox();
    }

    /**
     * Adds a form row with label and field
     */
    private void addFormRow(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        panel.add(field, gbc);
    }

    /**
     * Adds a password row with label and password field
     */
    private void addPasswordRow(JPanel panel, GridBagConstraints gbc, String labelText, JPasswordField field, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        panel.add(field, gbc);
    }

    /**
     * Sets up event listeners for buttons
     */
    private void setupEventListeners() {
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerStudent();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Enter key support for form fields
        ActionListener enterKeyListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerStudent();
            }
        };

        nameField.addActionListener(enterKeyListener);
        emailField.addActionListener(enterKeyListener);
        passwordField.addActionListener(enterKeyListener);
        confirmPasswordField.addActionListener(enterKeyListener);
        branchField.addActionListener(enterKeyListener);
        semesterField.addActionListener(enterKeyListener);
        phoneField.addActionListener(enterKeyListener);
    }

    /**
     * Validates form and registers new student
     */
    private void registerStudent() {
        // Collect form data
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String branch = branchField.getText().trim();
        String semester = semesterField.getText().trim();
        boolean hostel = hostelCheckBox.isSelected();
        String phone = phoneField.getText().trim();


        // Validation
        if (!validateForm(name, email, password, confirmPassword, branch, semester, phone)) {
            return;
        }

        // Show loading
        signupButton.setEnabled(false);
        signupButton.setText("Creating Account...");

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


        // Perform registration in background
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // Check if email already exists
                boolean emailExists = controller.emailExists(email, "").get();
                if (emailExists) {
                    throw new Exception("Email already registered: " + email);
                }

                // Register new student
                return controller.addStudent(student).get();
            }

            @Override
            protected void done() {
                signupButton.setEnabled(true);
                signupButton.setText("Create Account");

                try {
                    String studentId = get();
                    JOptionPane.showMessageDialog(SignupFrame.this,
                            "Account created successfully!\n\n" +
                                    "You can now login with your email and password.",
                            "Registration Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    dispose(); // Close signup window

                } catch (InterruptedException | ExecutionException e) {
                    String errorMessage = e.getCause().getMessage();
                    JOptionPane.showMessageDialog(SignupFrame.this,
                            "Registration failed: " + errorMessage,
                            "Registration Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Validates all form fields
     * @return true if validation passes, false otherwise
     */
    private boolean validateForm(String name, String email, String password,
                                 String confirmPassword, String branch,
                                 String semester, String phone) {

        // Check empty fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || branch.isEmpty() ||
                semester.isEmpty() || phone.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Email validation
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        // Password validation
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters long.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocus();
            return false;
        }

        // Password confirmation
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match. Please re-enter your password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocus();
            return false;
        }

        // Phone validation (basic)
        if (!isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid phone number (10 digits).",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return false;
        }

        // Semester validation
        if (!isValidSemester(semester)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid semester (1-8).",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            semesterField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Validates email format
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Validates phone number format
     */
    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    /**
     * Validates semester (1-8)
     */
    private boolean isValidSemester(String semester) {
        try {
            int sem = Integer.parseInt(semester);
            return sem >= 1 && sem <= 8;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}