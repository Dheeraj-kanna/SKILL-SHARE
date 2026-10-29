
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SessionOperations {
    private final Scanner sc;

    public SessionOperations() {
        this(new Scanner(System.in));
    }

    public SessionOperations(Scanner scanner) {
        this.sc = scanner;
    }

    public void scheduleSession(int studentId) {
        String selectSql = "SELECT e.exchange_id, s.name, e.skill_name " + "FROM exchanges e JOIN students s " + "ON s.student_id = CASE " + "WHEN e.requester_id = ? THEN e.receiver_id " + "ELSE e.requester_id END " + "WHERE e.status = 'Accepted' " + "AND (e.requester_id = ? OR e.receiver_id = ?) " + "ORDER BY s.name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement select = con.prepareStatement(selectSql)) {

            select.setInt(1, studentId);
            select.setInt(2, studentId);
            select.setInt(3, studentId);

            int[] exchangeIds = new int[100];
            int count = 0;

            try (ResultSet rs = select.executeQuery()) {
                System.out.println("\n--- ACCEPTED EXCHANGES ---");

                while (rs.next()) {
                    if (count == exchangeIds.length) {
                        System.out.println("Too many exchanges to display.");
                        return;
                    }

                    exchangeIds[count] = rs.getInt("exchange_id");
                    count++;

                    System.out.println(count + ". " + rs.getString("name") + " - " + rs.getString("skill_name"));
                }
            }

            if (count == 0) {
                System.out.println("No accepted exchanges available for scheduling.");
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

            System.out.print("Enter session date (YYYY-MM-DD): ");
            String sessionDate = sc.nextLine().trim();

            System.out.print("Enter session time (HH:MM:SS): ");
            String sessionTime = sc.nextLine().trim();

            System.out.print("Enter meeting location: ");
            String location = sc.nextLine().trim();

            if (sessionDate.isEmpty() || sessionTime.isEmpty()
                    || location.isEmpty()) {
                System.out.println("All session details are required.");
                return;
            }

            String sql = "INSERT INTO sessions " +
                         "(exchange_id, session_date, session_time, location, status) " +
                         "VALUES (?, ?, ?, ?, 'Scheduled')";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, exchangeIds[choice - 1]);
                ps.setString(2, sessionDate);
                ps.setString(3, sessionTime);
                ps.setString(4, location);

                ps.executeUpdate();
                System.out.println("Session scheduled successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Failed to schedule session: " + e.getMessage());
        }
    }

    public void viewSessions(int studentId) {
        String sql = "SELECT s.name AS partner_name, e.skill_name, " +
                     "se.session_date, se.session_time, se.location, se.status " +
                     "FROM sessions se " +
                     "JOIN exchanges e ON se.exchange_id = e.exchange_id " +
                     "JOIN students s ON s.student_id = CASE " +
                     "WHEN e.requester_id = ? THEN e.receiver_id " +
                     "ELSE e.requester_id END " +
                     "WHERE e.requester_id = ? OR e.receiver_id = ? " +
                     "ORDER BY se.session_date, se.session_time";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, studentId);
            ps.setInt(3, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    System.out.println("\n--- SESSION DETAILS ---");
                    System.out.println("Student: " + rs.getString("partner_name"));
                    System.out.println("Skill: " + rs.getString("skill_name"));
                    System.out.println("Date: " + rs.getDate("session_date"));
                    System.out.println("Time: " + rs.getTime("session_time"));
                    System.out.println("Location: " + rs.getString("location"));
                    System.out.println("Status: " + rs.getString("status"));
                }

                if (!found) {
                    System.out.println("No sessions found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to view sessions: " + e.getMessage());
        }
    }
}
