package com.skillshare.model;

/**
 * Represents a review and rating for an accepted/completed skill exchange.
 */
public class Review {
    private int reviewId;
    private int exchangeId;
    private int reviewerId;
    private String reviewerName;
    private String reviewedStudentName;
    private String skillName;
    private int rating; // 1 to 5
    private String comment;

    public Review() {
    }

    public Review(int exchangeId, int reviewerId, int rating, String comment) {
        this.exchangeId = exchangeId;
        this.reviewerId = reviewerId;
        this.rating = rating;
        this.comment = comment;
    }

    public Review(int reviewId, int exchangeId, int reviewerId, String reviewerName,
                  String reviewedStudentName, String skillName, int rating, String comment) {
        this.reviewId = reviewId;
        this.exchangeId = exchangeId;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewedStudentName = reviewedStudentName;
        this.skillName = skillName;
        this.rating = rating;
        this.comment = comment;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewedStudentName() {
        return reviewedStudentName;
    }

    public void setReviewedStudentName(String reviewedStudentName) {
        this.reviewedStudentName = reviewedStudentName;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
