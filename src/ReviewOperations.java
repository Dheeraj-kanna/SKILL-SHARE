
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ReviewOperations {
    private final Scanner sc;

    public ReviewOperations() {
        this(new Scanner(System.in));
    }

    public ReviewOperations(Scanner scanner) {
        this.sc = scanner;
    }

    public void submitReview(int studentId) {
        String selectSql = "SELECT e.exchange_id, s.name, e.skill_name " +
                "FROM exchanges e JOIN students s ON s.student_id = " +
                "CASE WHEN e.requester_id = ? THEN e.receiver_id " +
                "ELSE e.requester_id END " +
                "WHERE e.status = 'Accepted' " +
                "AND (e.requester_id = ? OR e.receiver_id = ?) " +
                "AND NOT EXISTS (SELECT 1 FROM reviews r " +
                "WHERE r.exchange_id = e.exchange_id AND r.reviewer_id = ?) " +
                "ORDER BY s.name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement select = con.prepareStatement(selectSql)) {

            select.setInt(1, studentId);
            select.setInt(2, studentId);
            select.setInt(3, studentId);
            select.setInt(4, studentId);

            int[] exchangeIds = new int[100];
            int count = 0;

            try (ResultSet rs = select.executeQuery()) {
                System.out.println("\n--- EXCHANGES AVAILABLE FOR REVIEW ---");

                while (rs.next()) {
                    if (count == exchangeIds.length) {
                        System.out.println("Too many exchanges to display.");
                        return;
                    }

                    exchangeIds[count] = rs.getInt("exchange_id");
                    count++;

                    System.out.println(count + ". " + rs.getString("name")
                            + " - " + rs.getString("skill_name"));
                }
            }

            if (count == 0) {
                System.out.println("No accepted exchanges available for review.");
                return;
            }

            System.out.print("Choose an exchange: ");
            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                return;
            }

            if (choice < 1 || choice > count) {
                System.out.println("Invalid selection.");
                return;
            }

            System.out.print("Enter rating (1-5): ");
            int rating;

            try {
                rating = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid rating.");
                return;
            }

            if (rating < 1 || rating > 5) {
                System.out.println("Rating must be between 1 and 5.");
                return;
            }

            System.out.print("Enter comment: ");
            String comment = sc.nextLine().trim();

            String sql = "INSERT INTO reviews " +
                    "(exchange_id, reviewer_id, rating, comment) VALUES (?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, exchangeIds[choice - 1]);
                ps.setInt(2, studentId);
                ps.setInt(3, rating);
                ps.setString(4, comment);

                ps.executeUpdate();
                System.out.println("Review submitted successfully!");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("You have already reviewed this exchange.");
            } else {
                System.out.println("Failed to submit review: " + e.getMessage());
            }
        }
    }

    public void viewReviews(int studentId) {
        String sql = "SELECT reviewer.name AS reviewer_name, partner.name AS partner_name, " +
                "e.skill_name, r.rating, r.comment " +
                "FROM reviews r " +
                "JOIN exchanges e ON r.exchange_id = e.exchange_id " +
                "JOIN students reviewer ON r.reviewer_id = reviewer.student_id " +
                "JOIN students partner ON partner.student_id = " +
                "CASE WHEN e.requester_id = r.reviewer_id THEN e.receiver_id " +
                "ELSE e.requester_id END " +
                "WHERE e.requester_id = ? OR e.receiver_id = ? " +
                "ORDER BY reviewer.name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    System.out.println("\n--- REVIEW ---");
                    System.out.println("Reviewer: " + rs.getString("reviewer_name"));
                    System.out.println("Student reviewed: " + rs.getString("partner_name"));
                    System.out.println("Skill: " + rs.getString("skill_name"));
                    System.out.println("Rating: " + rs.getInt("rating") + "/5");
                    System.out.println("Comment: " + rs.getString("comment"));
                }

                if (!found) {
                    System.out.println("No reviews found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to view reviews: " + e.getMessage());
        }
    }
}
