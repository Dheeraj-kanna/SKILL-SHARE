
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class StudentOperations {
    private final Scanner sc;

    public StudentOperations() {
        this(new Scanner(System.in));
    }

    public StudentOperations(Scanner scanner) {
        this.sc = scanner;
    }

    public void registerStudent() {
        try {
            System.out.print("Enter name: ");
            String name = sc.nextLine().trim();

            System.out.print("Enter email: ");
            String email = sc.nextLine().trim();

            System.out.print("Enter password: ");
            String password = sc.nextLine();

            System.out.print("Enter department: ");
            String department = sc.nextLine().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || department.isEmpty()) {
                System.out.println("Name, email, password, and department are required.");
                return;
            }

            Student s = new Student(name, email, PasswordHasher.hashPassword(password), department);

            String sql = "INSERT INTO students (name, email, password_hash, department) VALUES (?, ?, ?, ?)";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, s.getName());
                ps.setString(2, s.getEmail());
                ps.setString(3, s.getPasswordHash());
                ps.setString(4, s.getDepartment());

                ps.executeUpdate();
                System.out.println("Registration successful!");
            }

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    public int loginStudent() {
        try {
            System.out.print("Enter email: ");
            String email = sc.nextLine().trim();

            System.out.print("Enter password: ");
            String password = sc.nextLine();

            String sql = "SELECT student_id, name, password_hash FROM students WHERE email = ?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int studentId = rs.getInt("student_id");
                        String name = rs.getString("name");
                        String storedPasswordHash = rs.getString("password_hash");

                        if (!PasswordHasher.verifyPassword(password, storedPasswordHash)) {
                            System.out.println("Invalid email or password.");
                            return -1;
                        }

                        if (!PasswordHasher.isHashed(storedPasswordHash)) {
                            String upgradeSql = "UPDATE students SET password_hash = ? WHERE student_id = ?";
                            try (PreparedStatement upgrade = con.prepareStatement(upgradeSql)) {
                                upgrade.setString(1, PasswordHasher.hashPassword(password));
                                upgrade.setInt(2, studentId);
                                upgrade.executeUpdate();
                            }
                        }

                        System.out.println("Login successful!");
                        System.out.println("Welcome, " + name + "!");
                        return studentId;
                    } else {
                        System.out.println("Invalid email or password.");
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }

        return -1;
    }
}
