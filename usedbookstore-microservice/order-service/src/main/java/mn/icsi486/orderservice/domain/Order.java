package mn.icsi486.orderservice.domain;

import java.time.LocalDateTime;

public class Order {
    private Long id;
    private Long bookId;
    private String buyerUsername;
    private String sellerUsername;
    private double price;
    private String status;
    private LocalDateTime createdAt;

    public Order() {}

    public Order(Long id, Long bookId, String buyerUsername, String sellerUsername,
                 double price, String status, LocalDateTime createdAt) {
        this.id = id;
        this.bookId = bookId;
        this.buyerUsername = buyerUsername;
        this.sellerUsername = sellerUsername;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getBuyerUsername() { return buyerUsername; }
    public void setBuyerUsername(String buyerUsername) { this.buyerUsername = buyerUsername; }
    public String getSellerUsername() { return sellerUsername; }
    public void setSellerUsername(String sellerUsername) { this.sellerUsername = sellerUsername; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
