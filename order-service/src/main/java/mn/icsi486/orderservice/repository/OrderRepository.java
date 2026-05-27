package mn.icsi486.orderservice.repository;

import mn.icsi486.orderservice.config.DatabaseInit;
import mn.icsi486.orderservice.domain.Order;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {

    public Order save(Order order) {
        String sql = "INSERT INTO book_orders (book_id, buyer_username, seller_username, price, status, created_at) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, order.getBookId());
            ps.setString(2, order.getBuyerUsername());
            ps.setString(3, order.getSellerUsername());
            ps.setDouble(4, order.getPrice());
            ps.setString(5, order.getStatus() != null ? order.getStatus() : "PENDING");
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                order.setId(keys.getLong(1));
            }
            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save order", e);
        }
    }

    public Optional<Order> findById(Long id) {
        String sql = "SELECT * FROM book_orders WHERE id = ?";
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find order", e);
        }
    }

    public List<Order> findAll() {
        String sql = "SELECT * FROM book_orders ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseInit.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) orders.add(mapRow(rs));
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list orders", e);
        }
    }

    public List<Order> findByBuyer(String username) {
        String sql = "SELECT * FROM book_orders WHERE buyer_username = ? ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) orders.add(mapRow(rs));
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find orders by buyer", e);
        }
    }

    public Order updateStatus(Long id, String status) {
        String sql = "UPDATE book_orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();
            return findById(id).orElseThrow();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        return new Order(
                rs.getLong("id"),
                rs.getLong("book_id"),
                rs.getString("buyer_username"),
                rs.getString("seller_username"),
                rs.getDouble("price"),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
