package mn.icsi486.userservice.repository;

import mn.icsi486.userservice.config.DatabaseInit;
import mn.icsi486.userservice.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    public User save(User user) {
        String sql = "INSERT INTO app_users (username, password, role) VALUES (?,?,?)";
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole() != null ? user.getRole() : "USER");
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getLong(1));
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM app_users WHERE username = ?";
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user", e);
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM app_users ORDER BY id";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseInit.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) users.add(mapRow(rs));
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list users", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
        );
    }
}
