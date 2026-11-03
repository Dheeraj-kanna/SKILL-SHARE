package com.skillshare.dao;

import com.skillshare.config.DBConnection;
import com.skillshare.model.Session;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access operations for learning sessions in MySQL.
 */
public class SessionDAO {

    public Session scheduleSession(int exchangeId, Date date, Time time, String location) throws SQLException {
        String sql = "INSERT INTO sessions (exchange_id, session_date, session_time, location, status) VALUES (?, ?, ?, ?, 'Scheduled')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, exchangeId);
            ps.setDate(2, date);
            ps.setTime(3, time);
            ps.setString(4, location.trim());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Session(rs.getInt(1), exchangeId, null, null, null, date, time, location.trim(), "Scheduled");
                }
            }
        }
        return new Session(exchangeId, date, time, location.trim(), "Scheduled");
    }

    public List<Session> getSessionsByStudent(int studentId, boolean upcomingOnly) throws SQLException {
        List<Session> list = new ArrayList<>();
        String sql = "SELECT se.session_id, se.exchange_id, " +
                     "s.name AS partner_name, s.department AS partner_dept, " +
                     "e.skill_name, se.session_date, se.session_time, se.location, se.status " +
                     "FROM sessions se " +
                     "JOIN exchanges e ON se.exchange_id = e.exchange_id " +
                     "JOIN students s ON s.student_id = CASE " +
                     "    WHEN e.requester_id = ? THEN e.receiver_id " +
                     "    ELSE e.requester_id END " +
                     "WHERE (e.requester_id = ? OR e.receiver_id = ?) " +
                     (upcomingOnly ? "AND (se.session_date > CURRENT_DATE() OR (se.session_date = CURRENT_DATE() AND se.session_time >= CURRENT_TIME())) " : "") +
                     "ORDER BY se.session_date ASC, se.session_time ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, studentId);
            ps.setInt(3, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Session(
                            rs.getInt("session_id"),
                            rs.getInt("exchange_id"),
                            rs.getString("partner_name"),
                            rs.getString("partner_dept"),
                            rs.getString("skill_name"),
                            rs.getDate("session_date"),
                            rs.getTime("session_time"),
                            rs.getString("location"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }

    public List<Session> getPastSessions(int studentId) throws SQLException {
        List<Session> list = new ArrayList<>();
        String sql = "SELECT se.session_id, se.exchange_id, " +
                     "s.name AS partner_name, s.department AS partner_dept, " +
                     "e.skill_name, se.session_date, se.session_time, se.location, se.status " +
                     "FROM sessions se " +
                     "JOIN exchanges e ON se.exchange_id = e.exchange_id " +
                     "JOIN students s ON s.student_id = CASE " +
                     "    WHEN e.requester_id = ? THEN e.receiver_id " +
                     "    ELSE e.requester_id END " +
                     "WHERE (e.requester_id = ? OR e.receiver_id = ?) " +
                     "AND (se.session_date < CURRENT_DATE() OR (se.session_date = CURRENT_DATE() AND se.session_time < CURRENT_TIME())) " +
                     "ORDER BY se.session_date DESC, se.session_time DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, studentId);
            ps.setInt(3, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Session(
                            rs.getInt("session_id"),
                            rs.getInt("exchange_id"),
                            rs.getString("partner_name"),
                            rs.getString("partner_dept"),
                            rs.getString("skill_name"),
                            rs.getDate("session_date"),
                            rs.getTime("session_time"),
                            rs.getString("location"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }

    public int countUpcomingSessions(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sessions se " +
                     "JOIN exchanges e ON se.exchange_id = e.exchange_id " +
                     "WHERE (e.requester_id = ? OR e.receiver_id = ?) " +
                     "AND (se.session_date > CURRENT_DATE() OR (se.session_date = CURRENT_DATE() AND se.session_time >= CURRENT_TIME()))";
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
}
