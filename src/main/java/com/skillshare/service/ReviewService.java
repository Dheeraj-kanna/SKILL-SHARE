package com.skillshare.service;

import com.skillshare.dao.ReviewDAO;
import com.skillshare.model.ExchangeRequest;
import com.skillshare.model.Review;
import com.skillshare.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Handles exchange reviews, ratings validation, and statistics.
 */
public class ReviewService {
    private final ReviewDAO reviewDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAO();
    }

    public Review submitReview(int exchangeId, int reviewerId, int rating, String comment) throws Exception {
        if (!ValidationUtil.isValidRating(rating)) {
            throw new IllegalArgumentException("Rating must be an integer between 1 and 5.");
        }

        if (reviewDAO.hasReviewed(exchangeId, reviewerId)) {
            throw new IllegalArgumentException("You have already submitted a review for this exchange.");
        }

        Review review = new Review(exchangeId, reviewerId, rating, comment);
        return reviewDAO.createReview(review);
    }

    public List<Review> getReviewsReceived(int studentId) throws SQLException {
        return reviewDAO.getReviewsReceived(studentId);
    }

    public List<Review> getReviewsGiven(int studentId) throws SQLException {
        return reviewDAO.getReviewsGiven(studentId);
    }

    public double getAverageRating(int studentId) throws SQLException {
        return reviewDAO.getAverageRating(studentId);
    }

    public List<ExchangeRequest> getExchangesAvailableForReview(int studentId) throws SQLException {
        return reviewDAO.getExchangesAvailableForReview(studentId);
    }
}
