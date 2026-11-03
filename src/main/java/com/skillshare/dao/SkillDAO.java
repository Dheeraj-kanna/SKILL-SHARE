package com.skillshare.dao;

import com.skillshare.config.DBConnection;
import com.skillshare.model.Skill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access operations for student skills in MySQL.
 */
public class SkillDAO {

    public Skill addSkill(int studentId, String skillName, String skillType) throws SQLException {
        String sql = "INSERT INTO skills (student_id, skill_name, skill_type) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, studentId);
            ps.setString(2, skillName.trim());
            ps.setString(3, skillType.trim().toUpperCase());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Skill(rs.getInt(1), studentId, skillName.trim(), skillType.trim().toUpperCase());
                }
            }
        }
        return new Skill(studentId, skillName.trim(), skillType.trim().toUpperCase());
    }

    public List<Skill> getSkillsByStudentAndType(int studentId, String skillType) throws SQLException {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT skill_id, student_id, skill_name, skill_type FROM skills " +
                     "WHERE student_id = ? AND skill_type = ? ORDER BY skill_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, skillType.toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Skill(
                            rs.getInt("skill_id"),
                            rs.getInt("student_id"),
                            rs.getString("skill_name"),
                            rs.getString("skill_type")
                    ));
                }
            }
        }
        return list;
    }

    public List<Skill> getAllSkillsByStudent(int studentId) throws SQLException {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT skill_id, student_id, skill_name, skill_type FROM skills " +
                     "WHERE student_id = ? ORDER BY skill_type, skill_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Skill(
                            rs.getInt("skill_id"),
                            rs.getInt("student_id"),
                            rs.getString("skill_name"),
                            rs.getString("skill_type")
                    ));
                }
            }
        }
        return list;
    }

    public boolean deleteSkill(int skillId, int studentId) throws SQLException {
        String sql = "DELETE FROM skills WHERE skill_id = ? AND student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, skillId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hasSkill(int studentId, String skillName, String skillType) throws SQLException {
        String sql = "SELECT 1 FROM skills WHERE student_id = ? AND LOWER(skill_name) = LOWER(?) AND skill_type = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, skillName.trim());
            ps.setString(3, skillType.trim().toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int countSkills(int studentId, String skillType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM skills WHERE student_id = ? AND skill_type = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, skillType.toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<Skill> getRecentSkills(int limit) throws SQLException {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT skill_id, student_id, skill_name, skill_type FROM skills ORDER BY skill_id DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Skill(
                            rs.getInt("skill_id"),
                            rs.getInt("student_id"),
                            rs.getString("skill_name"),
                            rs.getString("skill_type")
                    ));
                }
            }
        }
        return list;
    }
}
