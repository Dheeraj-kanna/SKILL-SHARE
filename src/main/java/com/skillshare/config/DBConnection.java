package com.skillshare.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides database connections and schema verification for the SkillShare application.
 */
public class DBConnection {

    static {
        try {
            Class<?> driverClass = Class.forName("com.mysql.cj.jdbc.Driver");
            java.sql.Driver driver = (java.sql.Driver) driverClass.getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(driver);
        } catch (Exception e) {
            try {
                Class<?> driverClass = Class.forName("com.mysql.jdbc.Driver");
                java.sql.Driver driver = (java.sql.Driver) driverClass.getDeclaredConstructor().newInstance();
                DriverManager.registerDriver(driver);
            } catch (Exception ignored) {
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
        }
        String url = AppConfig.getDbUrl();
        String user = AppConfig.getDbUser();
        String password = AppConfig.getDbPassword();

        if (user == null || user.isBlank()) {
            throw new SQLException("Database username is not configured.");
        }

        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Checks if database connection is valid.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Ensures all required tables exist without modifying or dropping any existing data.
     */
    public static void initializeDatabase() throws SQLException {
        try (Connection con = getConnection();
             Statement stmt = con.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "student_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "department VARCHAR(100) NOT NULL" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS skills (" +
                    "skill_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "student_id INT NOT NULL, " +
                    "skill_name VARCHAR(100) NOT NULL, " +
                    "skill_type VARCHAR(20) NOT NULL, " +
                    "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS exchanges (" +
                    "exchange_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "requester_id INT NOT NULL, " +
                    "receiver_id INT NOT NULL, " +
                    "skill_name VARCHAR(100) NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'Pending', " +
                    "FOREIGN KEY (requester_id) REFERENCES students(student_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (receiver_id) REFERENCES students(student_id) ON DELETE CASCADE" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS sessions (" +
                    "session_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "exchange_id INT NOT NULL, " +
                    "session_date DATE NOT NULL, " +
                    "session_time TIME NOT NULL, " +
                    "location VARCHAR(200) NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'Scheduled', " +
                    "FOREIGN KEY (exchange_id) REFERENCES exchanges(exchange_id) ON DELETE CASCADE" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                    "review_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "exchange_id INT NOT NULL, " +
                    "reviewer_id INT NOT NULL, " +
                    "rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5), " +
                    "comment VARCHAR(500), " +
                    "FOREIGN KEY (exchange_id) REFERENCES exchanges(exchange_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (reviewer_id) REFERENCES students(student_id) ON DELETE CASCADE, " +
                    "UNIQUE (exchange_id, reviewer_id)" +
                    ")");
        }
    }
}
