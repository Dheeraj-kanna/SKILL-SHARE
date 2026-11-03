package com.skillshare.dao;

import com.skillshare.config.DBConnection;
import com.skillshare.model.ExchangeRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access operations for exchange requests in MySQL.
 */
public class ExchangeDAO {

    public ExchangeRequest createExchange(int requesterId, int receiverId, String skillName) throws SQLException {
        String sql = "INSERT INTO exchanges (requester_id, receiver_id, skill_name, status) VALUES (?, ?, ?, 'Pending')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, requesterId);
            ps.setInt(2, receiverId);
            ps.setString(3, skillName.trim());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ExchangeRequest req = new ExchangeRequest(requesterId, receiverId, skillName.trim());
                    req.setExchangeId(rs.getInt(1));
                    return req;
                }
            }
        }
        return new ExchangeRequest(requesterId, receiverId, skillName.trim());
    }

    public List<ExchangeRequest> getIncomingRequests(int studentId) throws SQLException {
        List<ExchangeRequest> list = new ArrayList<>();
        String sql = "SELECT e.exchange_id, e.requester_id, s_req.name AS requester_name, s_req.department AS requester_dept, " +
                     "e.receiver_id, s_rec.name AS receiver_name, s_rec.department AS receiver_dept, " +
                     "e.skill_name, e.status " +
                     "FROM exchanges e " +
                     "JOIN students s_req ON e.requester_id = s_req.student_id " +
                     "JOIN students s_rec ON e.receiver_id = s_rec.student_id " +
                     "WHERE e.receiver_id = ? " +
                     "ORDER BY e.exchange_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapExchange(rs));
                }
            }
        }
        return list;
    }

    public List<ExchangeRequest> getSentRequests(int studentId) throws SQLException {
        List<ExchangeRequest> list = new ArrayList<>();
        String sql = "SELECT e.exchange_id, e.requester_id, s_req.name AS requester_name, s_req.department AS requester_dept, " +
                     "e.receiver_id, s_rec.name AS receiver_name, s_rec.department AS receiver_dept, " +
                     "e.skill_name, e.status " +
                     "FROM exchanges e " +
                     "JOIN students s_req ON e.requester_id = s_req.student_id " +
                     "JOIN students s_rec ON e.receiver_id = s_rec.student_id " +
                     "WHERE e.requester_id = ? " +
                     "ORDER BY e.exchange_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapExchange(rs));
                }
            }
        }
        return list;
    }

    public List<ExchangeRequest> getAcceptedExchanges(int studentId) throws SQLException {
        List<ExchangeRequest> list = new ArrayList<>();
        String sql = "SELECT e.exchange_id, e.requester_id, s_req.name AS requester_name, s_req.department AS requester_dept, " +
                     "e.receiver_id, s_rec.name AS receiver_name, s_rec.department AS receiver_dept, " +
                     "e.skill_name, e.status " +
                     "FROM exchanges e " +
                     "JOIN students s_req ON e.requester_id = s_req.student_id " +
                     "JOIN students s_rec ON e.receiver_id = s_rec.student_id " +
                     "WHERE (e.requester_id = ? OR e.receiver_id = ?) AND e.status = 'Accepted' " +
                     "ORDER BY e.exchange_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapExchange(rs));
                }
            }
        }
        return list;
    }

    public List<ExchangeRequest> getRejectedExchanges(int studentId) throws SQLException {
        List<ExchangeRequest> list = new ArrayList<>();
        String sql = "SELECT e.exchange_id, e.requester_id, s_req.name AS requester_name, s_req.department AS requester_dept, " +
                     "e.receiver_id, s_rec.name AS receiver_name, s_rec.department AS receiver_dept, " +
                     "e.skill_name, e.status " +
                     "FROM exchanges e " +
                     "JOIN students s_req ON e.requester_id = s_req.student_id " +
                     "JOIN students s_rec ON e.receiver_id = s_rec.student_id " +
                     "WHERE (e.requester_id = ? OR e.receiver_id = ?) AND e.status = 'Rejected' " +
                     "ORDER BY e.exchange_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapExchange(rs));
                }
            }
        }
        return list;
    }

    public boolean updateStatus(int exchangeId, int receiverId, String newStatus) throws SQLException {
        String sql = "UPDATE exchanges SET status = ? WHERE exchange_id = ? AND receiver_id = ? AND status = 'Pending'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, exchangeId);
            ps.setInt(3, receiverId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hasExistingExchange(int requesterId, int receiverId, String skillName) throws SQLException {
        String sql = "SELECT 1 FROM exchanges WHERE requester_id = ? AND receiver_id = ? " +
                     "AND LOWER(skill_name) = LOWER(?) AND status IN ('Pending', 'Accepted') LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requesterId);
            ps.setInt(2, receiverId);
            ps.setString(3, skillName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int countPendingIncoming(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM exchanges WHERE receiver_id = ? AND status = 'Pending'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int countAcceptedExchanges(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM exchanges WHERE (requester_id = ? OR receiver_id = ?) AND status = 'Accepted'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private ExchangeRequest mapExchange(ResultSet rs) throws SQLException {
        return new ExchangeRequest(
                rs.getInt("exchange_id"),
                rs.getInt("requester_id"),
                rs.getString("requester_name"),
                rs.getString("requester_dept"),
                rs.getInt("receiver_id"),
                rs.getString("receiver_name"),
                rs.getString("receiver_dept"),
                rs.getString("skill_name"),
                rs.getString("status")
        );
    }
}
