package mn.icsi486.orderservice.service;

import mn.icsi486.orderservice.domain.Order;
import mn.icsi486.orderservice.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrderService {

    private final OrderRepository repository = new OrderRepository();

    public Order createOrder(Order order) {
        if (order.getBookId() == null) {
            throw new IllegalArgumentException("Номын ID заавал бөглөнө");
        }
        if (order.getBuyerUsername() == null || order.getBuyerUsername().isBlank()) {
            throw new IllegalArgumentException("Худалдан авагчийн нэр заавал бөглөнө");
        }
        if (order.getSellerUsername() == null || order.getSellerUsername().isBlank()) {
            throw new IllegalArgumentException("Худалдагчийн нэр заавал бөглөнө");
        }
        if (order.getPrice() <= 0) {
            throw new IllegalArgumentException("Үнэ 0-ээс их байх ёстой");
        }
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        return repository.save(order);
    }

    public Optional<Order> getById(Long id) {
        return repository.findById(id);
    }

    public List<Order> listAll() {
        return repository.findAll();
    }

    public List<Order> listByBuyer(String username) {
        return repository.findByBuyer(username);
    }

    public Order confirmOrder(Long id) {
        return repository.updateStatus(id, "CONFIRMED");
    }

    public Order cancelOrder(Long id) {
        return repository.updateStatus(id, "CANCELLED");
    }

    public Order completeOrder(Long id) {
        return repository.updateStatus(id, "COMPLETED");
    }
}
