package com.skillshare.model;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a learning session scheduled between two students.
 */
public class Session {
    private int sessionId;
    private int exchangeId;
    private String partnerName;
    private String partnerDepartment;
    private String skillName;
    private Date sessionDate;
    private Time sessionTime;
    private String location;
    private String status; // "Scheduled", "Completed", etc.

    public Session() {
    }

    public Session(int exchangeId, Date sessionDate, Time sessionTime, String location, String status) {
        this.exchangeId = exchangeId;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.location = location;
        this.status = status;
    }

    public Session(int sessionId, int exchangeId, String partnerName, String partnerDepartment,
                   String skillName, Date sessionDate, Time sessionTime, String location, String status) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.partnerName = partnerName;
        this.partnerDepartment = partnerDepartment;
        this.skillName = skillName;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.location = location;
        this.status = status;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerDepartment() {
        return partnerDepartment;
    }

    public void setPartnerDepartment(String partnerDepartment) {
        this.partnerDepartment = partnerDepartment;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Date getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(Date sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Time getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(Time sessionTime) {
        this.sessionTime = sessionTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isUpcoming() {
        if (sessionDate == null) return false;
        LocalDate today = LocalDate.now();
        LocalDate sDate = sessionDate.toLocalDate();
        if (sDate.isAfter(today)) return true;
        if (sDate.isEqual(today) && sessionTime != null) {
            return sessionTime.toLocalTime().isAfter(LocalTime.now());
        }
        return false;
    }
}
