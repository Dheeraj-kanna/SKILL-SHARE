
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ExchangeOperations {
    private final Scanner sc;

    public ExchangeOperations() {
        this(new Scanner(System.in));
    }

    public ExchangeOperations(Scanner scanner) {
        this.sc = scanner;
    }

    public void sendRequest(int requesterId) {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter the receiver's name: ");
            String receiverName = sc.nextLine().trim();

            System.out.print("Enter the skill name: ");
            String skillName = sc.nextLine().trim();

            if (receiverName.isEmpty() || skillName.isEmpty()) {
                System.out.println("Name and skill cannot be empty.");
                return;
            }

            String findSql = "SELECT student_id, name, department FROM students WHERE LOWER(name) = LOWER(?)";

            try (PreparedStatement ps = con.prepareStatement(findSql)) {
                ps.setString(1, receiverName);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Student not found.");
                        return;
                    }

                    int receiverId = rs.getInt("student_id");
                    String name = rs.getString("name");
                    String department = rs.getString("department");

                    if (rs.next()) {
                        System.out.println("Multiple students have this name. Please use a unique name.");
                        return;
                    }

                    if (requesterId == receiverId) {
                        System.out.println("You cannot send a request to yourself.");
                        return;
                    }

                    String skillCheckSql = "SELECT 1 FROM skills WHERE student_id = ? "
                            + "AND skill_type = 'TEACH' AND LOWER(skill_name) = LOWER(?) LIMIT 1";
                    try (PreparedStatement skillCheck = con.prepareStatement(skillCheckSql)) {
                        skillCheck.setInt(1, receiverId);
                        skillCheck.setString(2, skillName);
                        try (ResultSet offeredSkill = skillCheck.executeQuery()) {
                            if (!offeredSkill.next()) {
                                System.out.println("That student has not listed this skill as one they can teach.");
                                return;
                            }
                        }
                    }

                    String sql = "INSERT INTO exchanges (requester_id, receiver_id, skill_name, status) VALUES (?, ?, ?, 'Pending')";

                    try (PreparedStatement insert = con.prepareStatement(sql)) {
                        insert.setInt(1, requesterId);
                        insert.setInt(2, receiverId);
                        insert.setString(3, skillName);
                        insert.executeUpdate();

                        System.out.println("Request sent to " + name + " (" + department + ") successfully!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to send request: " + e.getMessage());
        }
    }

    public void viewRequests(int studentId) {
        String sql = "SELECT e.exchange_id, s.name, s.department, e.skill_name, e.status " +
                     "FROM exchanges e JOIN students s ON e.requester_id = s.student_id " +
                     "WHERE e.receiver_id = ? ORDER BY e.exchange_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                int number = 0;

                while (rs.next()) {
                    found = true;
                    number++;

                    System.out.println("\n" + number + ". From: " + rs.getString("name"));
                    System.out.println("   Department: " + rs.getString("department"));
                    System.out.println("   Skill: " + rs.getString("skill_name"));
                    System.out.println("   Status: " + rs.getString("status"));
                }

                if (!found) {
                    System.out.println("No incoming requests found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to view requests: " + e.getMessage());
        }
    }

    public void respondToRequest(int studentId) {
        String selectSql = "SELECT e.exchange_id, s.name, s.department, e.skill_name " +
                           "FROM exchanges e JOIN students s ON e.requester_id = s.student_id " +
                           "WHERE e.receiver_id = ? AND e.status = 'Pending' " +
                           "ORDER BY e.exchange_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement select = con.prepareStatement(selectSql)) {

            select.setInt(1, studentId);

            try (ResultSet rs = select.executeQuery()) {
                int[] requestIds = new int[100];
                int count = 0;

                System.out.println("\n--- PENDING EXCHANGE REQUESTS ---");

                while (rs.next()) {
                    if (count == requestIds.length) {
                        System.out.println("Too many requests to display.");
                        return;
                    }

                    requestIds[count] = rs.getInt("exchange_id");
                    count++;

                    System.out.println("\n" + count + ". " + rs.getString("name"));
                    System.out.println("   Department: " + rs.getString("department"));
                    System.out.println("   Skill: " + rs.getString("skill_name"));
                }

                if (count == 0) {
                    System.out.println("No pending requests.");
                    return;
                }

                System.out.print("\nChoose the request number: ");
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

                System.out.print("Enter A to accept or R to reject: ");
                String response = sc.nextLine().trim();

                String status;

                if (response.equalsIgnoreCase("A")) {
                    status = "Accepted";
                } else if (response.equalsIgnoreCase("R")) {
                    status = "Rejected";
                } else {
                    System.out.println("Invalid response.");
                    return;
                }

                String updateSql = "UPDATE exchanges SET status = ? " +
                                   "WHERE exchange_id = ? AND receiver_id = ? AND status = 'Pending'";

                try (PreparedStatement update = con.prepareStatement(updateSql)) {
                    update.setString(1, status);
                    update.setInt(2, requestIds[choice - 1]);
                    update.setInt(3, studentId);

                    if (update.executeUpdate() > 0) {
                        System.out.println("Request " + status.toLowerCase() + " successfully!");
                    } else {
                        System.out.println("Request could not be updated.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to respond: " + e.getMessage());
        }
    }
}
