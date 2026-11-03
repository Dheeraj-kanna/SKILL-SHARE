package com.skillshare.service;

import com.skillshare.dao.SessionDAO;
import com.skillshare.model.Session;
import com.skillshare.util.DateUtil;
import com.skillshare.util.ValidationUtil;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Handles learning session creation, listing, and timeline sorting.
 */
public class SessionService {
    private final SessionDAO sessionDAO;

    public SessionService() {
        this.sessionDAO = new SessionDAO();
    }

    public Session scheduleSession(int exchangeId, String dateStr, String timeStr, String location) throws Exception {
        if (!ValidationUtil.isNotEmpty(dateStr) || !ValidationUtil.isNotEmpty(timeStr) || !ValidationUtil.isNotEmpty(location)) {
            throw new IllegalArgumentException("Session date, time, and meeting location are required.");
        }

        Date date;
        Time time;

        try {
            date = DateUtil.parseSqlDate(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD (e.g. 2026-11-15).");
        }

        try {
            time = DateUtil.parseSqlTime(timeStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format. Please use HH:MM or HH:MM:SS (e.g. 14:30:00).");
        }

        return sessionDAO.scheduleSession(exchangeId, date, time, location.trim());
    }

    public List<Session> getUpcomingSessions(int studentId) throws SQLException {
        return sessionDAO.getSessionsByStudent(studentId, true);
    }

    public List<Session> getAllSessions(int studentId) throws SQLException {
        return sessionDAO.getSessionsByStudent(studentId, false);
    }

    public List<Session> getPastSessions(int studentId) throws SQLException {
        return sessionDAO.getPastSessions(studentId);
    }

    public int countUpcomingSessions(int studentId) throws SQLException {
        return sessionDAO.countUpcomingSessions(studentId);
    }
}
