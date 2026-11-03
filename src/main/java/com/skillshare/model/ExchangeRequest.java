package com.skillshare.model;

/**
 * Represents a skill exchange request between two students.
 */
public class ExchangeRequest {
    private int exchangeId;
    private int requesterId;
    private String requesterName;
    private String requesterDepartment;
    private int receiverId;
    private String receiverName;
    private String receiverDepartment;
    private String skillName;
    private String status; // "Pending", "Accepted", "Rejected"

    public ExchangeRequest() {
    }

    public ExchangeRequest(int requesterId, int receiverId, String skillName) {
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.skillName = skillName;
        this.status = "Pending";
    }

    public ExchangeRequest(int exchangeId, int requesterId, String requesterName, String requesterDepartment,
                           int receiverId, String receiverName, String receiverDepartment,
                           String skillName, String status) {
        this.exchangeId = exchangeId;
        this.requesterId = requesterId;
        this.requesterName = requesterName;
        this.requesterDepartment = requesterDepartment;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverDepartment = receiverDepartment;
        this.skillName = skillName;
        this.status = status;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterDepartment() {
        return requesterDepartment;
    }

    public void setRequesterDepartment(String requesterDepartment) {
        this.requesterDepartment = requesterDepartment;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverDepartment() {
        return receiverDepartment;
    }

    public void setReceiverDepartment(String receiverDepartment) {
        this.receiverDepartment = receiverDepartment;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPending() {
        return "Pending".equalsIgnoreCase(status);
    }

    public boolean isAccepted() {
        return "Accepted".equalsIgnoreCase(status);
    }

    public boolean isRejected() {
        return "Rejected".equalsIgnoreCase(status);
    }
}
