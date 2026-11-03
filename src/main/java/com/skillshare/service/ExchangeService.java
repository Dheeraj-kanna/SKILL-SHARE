package com.skillshare.service;

import com.skillshare.dao.ExchangeDAO;
import com.skillshare.dao.SkillDAO;
import com.skillshare.model.ExchangeRequest;
import com.skillshare.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Handles exchange request dispatch, status updates, and tracking.
 */
public class ExchangeService {
    private final ExchangeDAO exchangeDAO;
    private final SkillDAO skillDAO;

    public ExchangeService() {
        this.exchangeDAO = new ExchangeDAO();
        this.skillDAO = new SkillDAO();
    }

    public ExchangeRequest sendRequest(int requesterId, int receiverId, String skillName) throws Exception {
        if (requesterId == receiverId) {
            throw new IllegalArgumentException("You cannot send an exchange request to yourself.");
        }
        if (!ValidationUtil.isNotEmpty(skillName)) {
            throw new IllegalArgumentException("Skill name cannot be empty.");
        }

        // Validate that receiver actually teaches this skill
        if (!skillDAO.hasSkill(receiverId, skillName, "TEACH")) {
            throw new IllegalArgumentException("That student has not listed '" + skillName + "' as a skill they can teach.");
        }

        // Prevent duplicate pending/accepted requests
        if (exchangeDAO.hasExistingExchange(requesterId, receiverId, skillName)) {
            throw new IllegalArgumentException("An exchange request for '" + skillName + "' is already pending or accepted with this student.");
        }

        return exchangeDAO.createExchange(requesterId, receiverId, skillName);
    }

    public boolean respondToRequest(int exchangeId, int receiverId, boolean accept) throws SQLException {
        String newStatus = accept ? "Accepted" : "Rejected";
        return exchangeDAO.updateStatus(exchangeId, receiverId, newStatus);
    }

    public List<ExchangeRequest> getIncomingRequests(int studentId) throws SQLException {
        return exchangeDAO.getIncomingRequests(studentId);
    }

    public List<ExchangeRequest> getSentRequests(int studentId) throws SQLException {
        return exchangeDAO.getSentRequests(studentId);
    }

    public List<ExchangeRequest> getAcceptedExchanges(int studentId) throws SQLException {
        return exchangeDAO.getAcceptedExchanges(studentId);
    }

    public List<ExchangeRequest> getRejectedExchanges(int studentId) throws SQLException {
        return exchangeDAO.getRejectedExchanges(studentId);
    }

    public int countPendingIncoming(int studentId) throws SQLException {
        return exchangeDAO.countPendingIncoming(studentId);
    }

    public int countAcceptedExchanges(int studentId) throws SQLException {
        return exchangeDAO.countAcceptedExchanges(studentId);
    }
}
