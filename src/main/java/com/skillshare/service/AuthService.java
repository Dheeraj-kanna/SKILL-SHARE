package com.skillshare.service;

import com.skillshare.dao.StudentDAO;
import com.skillshare.model.Student;
import com.skillshare.util.PasswordHasher;
import com.skillshare.util.ValidationUtil;

import java.sql.SQLException;

/**
 * Handles authentication, registration, and student session lifecycle.
 */
public class AuthService {
    private final StudentDAO studentDAO;

    public AuthService() {
        this.studentDAO = new StudentDAO();
    }

    public AuthService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public Student login(String email, String password) throws Exception {
        if (!ValidationUtil.isNotEmpty(email) || !ValidationUtil.isNotEmpty(password)) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        Student student = studentDAO.findByEmail(email);
        if (student == null) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        if (!PasswordHasher.verifyPassword(password, student.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        // Automatic security upgrade for legacy accounts stored in plain text
        if (!PasswordHasher.isHashed(student.getPasswordHash())) {
            String newHash = PasswordHasher.hashPassword(password);
            studentDAO.updatePassword(student.getStudentId(), newHash);
            student.setPasswordHash(newHash);
        }

        return student;
    }

    public Student register(String name, String email, String password, String department) throws Exception {
        if (!ValidationUtil.isNotEmpty(name)) {
            throw new IllegalArgumentException("Student name is required.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        if (!ValidationUtil.isNotEmpty(department)) {
            throw new IllegalArgumentException("Department is required.");
        }

        Student existing = studentDAO.findByEmail(email);
        if (existing != null) {
            throw new IllegalArgumentException("An account with email '" + email + "' already exists.");
        }

        String passwordHash = PasswordHasher.hashPassword(password);
        Student newStudent = new Student(name.trim(), email.trim(), passwordHash, department.trim());
        return studentDAO.register(newStudent);
    }

    public void updatePassword(int studentId, String currentPassword, String newPassword) throws Exception {
        if (!ValidationUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("New password must be at least 4 characters long.");
        }

        Student student = studentDAO.findById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found.");
        }

        if (!PasswordHasher.verifyPassword(currentPassword, student.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        String newHash = PasswordHasher.hashPassword(newPassword);
        studentDAO.updatePassword(studentId, newHash);
    }

    public void updateProfile(int studentId, String name, String department) throws Exception {
        if (!ValidationUtil.isNotEmpty(name) || !ValidationUtil.isNotEmpty(department)) {
            throw new IllegalArgumentException("Name and department cannot be empty.");
        }
        studentDAO.updateProfile(studentId, name.trim(), department.trim());
    }

    public Student getStudentById(int studentId) throws SQLException {
        return studentDAO.findById(studentId);
    }
}
