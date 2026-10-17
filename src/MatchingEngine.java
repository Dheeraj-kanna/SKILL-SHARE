
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MatchingEngine {

    public void findMatches(int studentId) {
        String sql = "SELECT DISTINCT s.name, s.department, sk.skill_name " +
                     "FROM students s " +
                     "JOIN skills sk ON s.student_id = sk.student_id " +
                     "WHERE sk.skill_type = 'TEACH' " +
                     "AND LOWER(sk.skill_name) IN " +
                     "(SELECT LOWER(skill_name) FROM skills " +
                     "WHERE student_id = ? AND skill_type = 'LEARN') " +
                     "AND s.student_id != ? " +
                     "ORDER BY s.name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    System.out.println("\nStudent: " + rs.getString("name"));
                    System.out.println("Department: " + rs.getString("department"));
                    System.out.println("Can teach: " + rs.getString("skill_name"));
                }

                if (!found) {
                    System.out.println("No matching students found yet.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding matches: " + e.getMessage());
        }
    }
}
