package com.skillshare.dao;

import com.skillshare.config.DBConnection;
import com.skillshare.model.ExchangeRequest;
import com.skillshare.model.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access operations for reviews and ratings in MySQL.
 */
public class ReviewDAO {

    public Review createReview(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (exchange_id, reviewer_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, review.getExchangeId());
            ps.setInt(2, review.getReviewerId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment() != null ? review.getComment().trim() : "");

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    review.setReviewId(rs.getInt(1));
                }
            }
        }
        return review;
    }

    public boolean hasReviewed(int exchangeId, int reviewerId) throws SQLException {
        String sql = "SELECT 1 FROM reviews WHERE exchange_id = ? AND reviewer_id = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, exchangeId);
            ps.setInt(2, reviewerId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Review> getReviewsReceived(int studentId) throws SQLException {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.review_id, r.exchange_id, r.reviewer_id, " +
                     "reviewer.name AS reviewer_name, " +
                     "partner.name AS reviewed_name, " +
                     "e.skill_name, r.rating, r.comment " +
                     "FROM reviews r " +
                     "JOIN exchanges e ON r.exchange_id = e.exchange_id " +
                     "JOIN students reviewer ON r.reviewer_id = reviewer.student_id " +
                     "JOIN students partner ON partner.student_id = " +
                     "    CASE WHEN e.requester_id = r.reviewer_id THEN e.receiver_id " +
                     "    ELSE e.requester_id END " +
                     "WHERE partner.student_id = ? " +
                     "ORDER BY r.review_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Review(
                            rs.getInt("review_id"),
                            rs.getInt("exchange_id"),
                            rs.getInt("reviewer_id"),
                            rs.getString("reviewer_name"),
                            rs.getString("reviewed_name"),
                            rs.getString("skill_name"),
                            rs.getInt("rating"),
                            rs.getString("comment")
                    ));
                }
            }
        }
        return list;
    }

    public List<Review> getReviewsGiven(int studentId) throws SQLException {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.review_id, r.exchange_id, r.reviewer_id, " +
                     "reviewer.name AS reviewer_name, " +
                     "partner.name AS reviewed_name, " +
                     "e.skill_name, r.rating, r.comment " +
                     "FROM reviews r " +
                     "JOIN exchanges e ON r.exchange_id = e.exchange_id " +
                     "JOIN students reviewer ON r.reviewer_id = reviewer.student_id " +
                     "JOIN students partner ON partner.student_id = " +
                     "    CASE WHEN e.requester_id = r.reviewer_id THEN e.receiver_id " +
                     "    ELSE e.requester_id END " +
                     "WHERE reviewer.student_id = ? " +
                     "ORDER BY r.review_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Review(
                            rs.getInt("review_id"),
                            rs.getInt("exchange_id"),
                            rs.getInt("reviewer_id"),
                            rs.getString("reviewer_name"),
                            rs.getString("reviewed_name"),
                            rs.getString("skill_name"),
                            rs.getInt("rating"),
                            rs.getString("comment")
                    ));
                }
            }
        }
        return list;
    }

    public double getAverageRating(int studentId) throws SQLException {
        String sql = "SELECT AVG(r.rating) FROM reviews r " +
                     "JOIN exchanges e ON r.exchange_id = e.exchange_id " +
                     "JOIN students partner ON partner.student_id = " +
                     "    CASE WHEN e.requester_id = r.reviewer_id THEN e.receiver_id " +
                     "    ELSE e.requester_id END " +
                     "WHERE partner.student_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    public List<ExchangeRequest> getExchangesAvailableForReview(int studentId) throws SQLException {
        List<ExchangeRequest> list = new ArrayList<>();
        String sql = "SELECT e.exchange_id, e.requester_id, s_req.name AS requester_name, s_req.department AS requester_dept, " +
                     "e.receiver_id, s_rec.name AS receiver_name, s_rec.department AS receiver_dept, " +
                     "e.skill_name, e.status " +
                     "FROM exchanges e " +
                     "JOIN students s_req ON e.requester_id = s_req.student_id " +
                     "JOIN students s_rec ON e.receiver_id = s_rec.student_id " +
                     "WHERE e.status = 'Accepted' " +
                     "AND (e.requester_id = ? OR e.receiver_id = ?) " +
                     "AND NOT EXISTS (SELECT 1 FROM reviews r WHERE r.exchange_id = e.exchange_id AND r.reviewer_id = ?) " +
                     "ORDER BY e.exchange_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, studentId);
            ps.setInt(3, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ExchangeRequest(
                            rs.getInt("exchange_id"),
                            rs.getInt("requester_id"),
                            rs.getString("requester_name"),
                            rs.getString("requester_dept"),
                            rs.getInt("receiver_id"),
                            rs.getString("receiver_name"),
                            rs.getString("receiver_dept"),
                            rs.getString("skill_name"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }
}
