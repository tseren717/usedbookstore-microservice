package mn.icsi486.bookservice.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInit {

    private static final String URL = "jdbc:h2:./data/bookdb;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private DatabaseInit() {}

    public static void initialize() {
        String sql = """
                CREATE TABLE IF NOT EXISTS books (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    author VARCHAR(255) NOT NULL,
                    isbn VARCHAR(20),
                    price DOUBLE NOT NULL,
                    description VARCHAR(1000),
                    book_condition VARCHAR(30) NOT NULL,
                    category VARCHAR(30) NOT NULL,
                    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
                    image_path VARCHAR(500),
                    seller_username VARCHAR(100) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("[BookService] Database initialized.");
        } catch (SQLException e) {
            throw new RuntimeException("Book DB init failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
