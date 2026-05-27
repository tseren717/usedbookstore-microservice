package mn.icsi486.orderservice.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInit {

    private static final String URL = "jdbc:h2:./data/orderdb;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private DatabaseInit() {}

    public static void initialize() {
        String sql = """
                CREATE TABLE IF NOT EXISTS book_orders (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    book_id BIGINT NOT NULL,
                    buyer_username VARCHAR(100) NOT NULL,
                    seller_username VARCHAR(100) NOT NULL,
                    price DOUBLE NOT NULL,
                    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("[OrderService] Database initialized.");
        } catch (SQLException e) {
            throw new RuntimeException("Order DB init failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
