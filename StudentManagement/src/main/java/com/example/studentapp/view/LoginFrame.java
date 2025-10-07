package com.example.studentapp.view;

import com.example.studentapp.controller.StudentController;
import com.example.studentapp.model.Admin;
import com.example.studentapp.model.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

/**
 * Login Frame for both Admin and Student authentication
 */
public class LoginFrame extends JFrame {
    private StudentController controller;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;
    private JComboBox<String> userTypeComboBox;
    private JPanel mainPanel;

    public LoginFrame() {
        this.controller = new StudentController();
        initializeUI();

        // Create default admin on startup
        controller.createDefaultAdmin();
    }

    private void initializeUI() {
        setTitle("Student Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // Create main panel with border
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title label
        JLabel titleLabel = new JLabel("Student Management System", JLabel.CENTER);
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

        // User type selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Login As:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        String[] userTypes = {"Student", "Admin"};
        userTypeComboBox = new JComboBox<>(userTypes);
        formPanel.add(userTypeComboBox, gbc);

        // Username/Email field
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel userLabel = new JLabel("Email:");
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 100, 200));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        formPanel.add(loginButton, gbc);

        // Separator - Only show signup for students
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 5, 0);
        JSeparator separator = new JSeparator();
        formPanel.add(separator, gbc);

        // New student label
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 5, 0);
        JLabel newUserLabel = new JLabel("New Student? Create an Account", JLabel.CENTER);
        newUserLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        newUserLabel.setForeground(Color.GRAY);
        formPanel.add(newUserLabel, gbc);

        // Signup button
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        signupButton = new JButton("Student Sign Up");
        signupButton.setBackground(new Color(40, 167, 69));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        formPanel.add(signupButton, gbc);

        // Admin credentials hint
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 0, 0);
        JLabel adminHint = new JLabel("Admin: username='admin', password='admin123'", JLabel.CENTER);
        adminHint.setFont(new Font("Arial", Font.ITALIC, 10));
        adminHint.setForeground(Color.GRAY);
        formPanel.add(adminHint, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Add action listeners
        setupEventListeners();

        // Update labels based on initial selection
        updateLabels();

        add(mainPanel);
        pack();
    }

    private void setupEventListeners() {
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        // Signup button action
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSignup();
            }
        });

        // User type change listener
        userTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLabels();
            }
        });

        // Enter key support for password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
    }

    /**
     * Updates form labels based on selected user type
     */
    /**
     * Updates form labels based on selected user type
     */
    /**
     * Updates form labels based on selected user type
     */
    private void updateLabels() {
        String userType = (String) userTypeComboBox.getSelectedItem();

        // Find and update the user label in a simpler way
        Component[] mainComponents = mainPanel.getComponents();
        for (Component mainComp : mainComponents) {
            if (mainComp instanceof JPanel) {
                JPanel formPanel = (JPanel) mainComp;
                Component[] formComponents = formPanel.getComponents();
                for (Component formComp : formComponents) {
                    if (formComp instanceof JLabel) {
                        JLabel label = (JLabel) formComp;
                        String currentText = label.getText();
                        if ("Email:".equals(currentText) || "Username:".equals(currentText)) {
                            if ("Admin".equals(userType)) {
                                label.setText("Username:");
                            } else {
                                label.setText("Email:");
                            }
                            break;
                        }
                    }
                }
            }
        }

        // Show/hide signup button
        signupButton.setVisible(!"Admin".equals(userType));

        revalidate();
        repaint();
    }

    /**
     * Attempts to authenticate user based on selected type
     */
    private void attemptLogin() {
        String userType = (String) userTypeComboBox.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both " + ("Admin".equals(userType) ? "username" : "email") + " and password",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Student".equals(userType) && !isValidEmail(username)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show loading indicator
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Perform authentication based on user type
        if ("Admin".equals(userType)) {
            authenticateAdmin(username, password);
        } else {
            authenticateStudent(username, password);
        }
    }

    /**
     * Authenticates admin user
     */
    private void authenticateAdmin(String username, String password) {
        new SwingWorker<Admin, Void>() {
            @Override
            protected Admin doInBackground() throws Exception {
                return controller.authenticateAdmin(username, password).get();
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Login");

                try {
                    Admin admin = get();
                    if (admin != null) {
                        // Admin login successful
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Admin login successful! Welcome " + admin.getName(),
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Open admin dashboard
                        new DashboardFrame(admin, null).setVisible(true);
                        dispose();
                    } else {
                        // Admin login failed
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Invalid admin credentials",
                                "Login Failed", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Login failed: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Authenticates student user
     */
    private void authenticateStudent(String email, String password) {
        new SwingWorker<Student, Void>() {
            @Override
            protected Student doInBackground() throws Exception {
                return controller.authenticateStudent(email, password).get();
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Login");

                try {
                    Student student = get();
                    if (student != null) {
                        // Student login successful
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Login successful! Welcome " + student.getName(),
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Open student dashboard (limited access)
                        new DashboardFrame(null, student).setVisible(true);
                        dispose();
                    } else {
                        // Student login failed
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Invalid email or password",
                                "Login Failed", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Login failed: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Opens the student signup registration form
     */
    private void openSignup() {
        SignupFrame signupFrame = new SignupFrame(this);
        signupFrame.setVisible(true);
    }

    /**
     * Basic email validation
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}