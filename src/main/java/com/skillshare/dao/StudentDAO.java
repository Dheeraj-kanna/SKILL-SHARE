package com.skillshare.dao;

import com.skillshare.config.DBConnection;
import com.skillshare.model.Student;
import com.skillshare.util.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access operations for students in MySQL.
 */
public class StudentDAO {

    public Student register(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, email, password_hash, department) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getPasswordHash());
            ps.setString(4, student.getDepartment());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setStudentId(rs.getInt(1));
                    }
                }
            }
            return student;
        }
    }

    public Student findByEmail(String email) throws SQLException {
        String sql = "SELECT student_id, name, email, password_hash, department FROM students WHERE LOWER(email) = LOWER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        }
        return null;
    }

    public Student findById(int studentId) throws SQLException {
        String sql = "SELECT student_id, name, email, password_hash, department FROM students WHERE student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        }
        return null;
    }

    public List<Student> findByName(String name) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT student_id, name, email, password_hash, department FROM students WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + name.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStudent(rs));
                }
            }
        }
        return list;
    }

    public void updatePassword(int studentId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE students SET password_hash = ? WHERE student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        }
    }

    public void updateProfile(int studentId, String name, String department) throws SQLException {
        String sql = "UPDATE students SET name = ?, department = ? WHERE student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, department);
            ps.setInt(3, studentId);
            ps.executeUpdate();
        }
    }

    public List<Student> getAllOtherStudents(int currentStudentId) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT student_id, name, email, password_hash, department FROM students WHERE student_id != ? ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, currentStudentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStudent(rs));
                }
            }
        }
        return list;
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("student_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("department")
        );
    }
}
