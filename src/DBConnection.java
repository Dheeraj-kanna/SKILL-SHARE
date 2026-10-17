import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/campus_skill_exchange";

    public static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault("CAMPUS_DB_URL", DEFAULT_URL);
        String user = System.getenv("CAMPUS_DB_USER");
        String password = System.getenv("CAMPUS_DB_PASSWORD");

        if (user == null || user.isBlank() || password == null || password.isBlank()) {
            throw new SQLException("Set CAMPUS_DB_USER and CAMPUS_DB_PASSWORD before connecting.");
        }

        return DriverManager.getConnection(url, user, password);
    }
}
