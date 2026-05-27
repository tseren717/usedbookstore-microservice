package mn.icsi486.userservice.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInit {

    private static final String URL = "jdbc:h2:./data/userdb;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private DatabaseInit() {}

    public static void initialize() {
        String usersSql = """
                CREATE TABLE IF NOT EXISTS app_users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(100) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    role VARCHAR(30) NOT NULL DEFAULT 'USER'
                )
                """;

        String seedAdminSql = """
                MERGE INTO app_users (username, password, role)
                KEY (username)
                VALUES ('admin', 'password123', 'ADMIN')
                """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(usersSql);
            stmt.execute(seedAdminSql);
            System.out.println("[UserService] Database initialized. Admin user seeded.");
        } catch (SQLException e) {
            throw new RuntimeException("User DB init failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
