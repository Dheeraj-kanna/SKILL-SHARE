
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class SkillOperations {
    private final Scanner sc;

    public SkillOperations() {
        this(new Scanner(System.in));
    }

    public SkillOperations(Scanner scanner) {
        this.sc = scanner;
    }

    public void addSkill(int studentId, String skillType) {
        try {
            System.out.print("Enter skill name: ");
            String skillName = sc.nextLine().trim();

            if (skillName.isEmpty()) {
                System.out.println("Skill name cannot be empty.");
                return;
            }

            String sql = "INSERT INTO skills (student_id, skill_name, skill_type) VALUES (?, ?, ?)";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, studentId);
                ps.setString(2, skillName);
                ps.setString(3, skillType);

                ps.executeUpdate();

                if (skillType.equals("TEACH")) {
                    System.out.println("Teaching skill added successfully!");
                } else {
                    System.out.println("Learning skill added successfully!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to add skill: " + e.getMessage());
        }
    }
}
