// [file name]: DatabaseConfig.java
package com.example.studentapp.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Database configuration loader
 */
public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try {
            InputStream input = DatabaseConfig.class.getClassLoader()
                    .getResourceAsStream("database.properties");
            if (input != null) {
                properties.load(input);
            } else {
                // Default values for development
                properties.setProperty("db.url", "jdbc:mysql://localhost:3306/student_management");
                properties.setProperty("db.username", "root");
                properties.setProperty("db.password", "password");
                properties.setProperty("db.pool.size", "10");
            }
        } catch (Exception e) {
            System.err.println("Failed to load database configuration: " + e.getMessage());
        }
    }

    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }

    public static int getPoolSize() {
        return Integer.parseInt(properties.getProperty("db.pool.size", "10"));
    }
}