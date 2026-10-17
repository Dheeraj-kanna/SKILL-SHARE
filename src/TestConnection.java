import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            if (con.isValid(2)) {
                System.out.println("MySQL connected successfully!");
            } else {
                System.out.println("MySQL connection is not valid.");
            }
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
