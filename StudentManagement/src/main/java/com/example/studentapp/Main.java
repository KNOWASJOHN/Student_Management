// [file name]: Main.java
package com.example.studentapp;

import com.example.studentapp.view.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * Main class to launch the Student Management Application
 */
public class Main {
    /**
     * Application entry point
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize and display the login frame
                new LoginFrame().setVisible(true);
                System.out.println("Student Management Application started successfully");
            } catch (Exception e) {
                System.err.println("Failed to start application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}