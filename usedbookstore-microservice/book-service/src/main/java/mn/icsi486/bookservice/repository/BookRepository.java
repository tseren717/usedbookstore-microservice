package mn.icsi486.bookservice.repository;

import mn.icsi486.bookservice.config.DatabaseInit;
import mn.icsi486.bookservice.domain.Book;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository {

    public Book save(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, price, description, book_condition, category, status, image_path, seller_username, created_at) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setDouble(4, book.getPrice());
            ps.setString(5, book.getDescription());
            ps.setString(6, book.getCondition());
            ps.setString(7, book.getCategory());
            ps.setString(8, book.getStatus() != null ? book.getStatus() : "AVAILABLE");
            ps.setString(9, book.getImagePath());
            ps.setString(10, book.getSellerUsername());
            ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                book.setId(keys.getLong(1));
            }
            return book;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save book", e);
        }
    }

    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find book", e);
        }
    }

    public List<Book> findAll() {
        String sql = "SELECT * FROM books ORDER BY created_at DESC";
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseInit.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapRow(rs));
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list books", e);
        }
    }

    public List<Book> findByStatus(String status) {
        String sql = "SELECT * FROM books WHERE status = ? ORDER BY created_at DESC";
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(mapRow(rs));
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find books by status", e);
        }
    }

    public List<Book> findBySellerUsername(String username) {
        String sql = "SELECT * FROM books WHERE seller_username = ? ORDER BY created_at DESC";
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(mapRow(rs));
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find books by seller", e);
        }
    }

    public Book update(Book book) {
        String sql = "UPDATE books SET title=?, author=?, isbn=?, price=?, description=?, book_condition=?, category=?, status=?, image_path=? WHERE id=?";
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setDouble(4, book.getPrice());
            ps.setString(5, book.getDescription());
            ps.setString(6, book.getCondition());
            ps.setString(7, book.getCategory());
            ps.setString(8, book.getStatus());
            ps.setString(9, book.getImagePath());
            ps.setLong(10, book.getId());
            ps.executeUpdate();
            return book;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book", e);
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete book", e);
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getDouble("price"),
                rs.getString("description"),
                rs.getString("book_condition"),
                rs.getString("category"),
                rs.getString("status"),
                rs.getString("image_path"),
                rs.getString("seller_username"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
