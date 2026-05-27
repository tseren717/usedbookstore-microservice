package mn.icsi486.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mn.icsi486.orderservice.domain.Order;
import mn.icsi486.orderservice.service.OrderService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderController extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                String buyer = req.getParameter("buyer");
                List<Order> orders;
                if (buyer != null && !buyer.isBlank()) {
                    orders = orderService.listByBuyer(buyer);
                } else {
                    orders = orderService.listAll();
                }
                writeJson(resp, 200, orders);
            } else {
                Long id = Long.parseLong(pathInfo.substring(1));
                Optional<Order> order = orderService.getById(id);
                if (order.isPresent()) {
                    writeJson(resp, 200, order.get());
                } else {
                    writeJson(resp, 404, Map.of("error", "Захиалга олдсонгүй"));
                }
            }
        } catch (Exception e) {
            writeJson(resp, 500, Map.of("error", e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            Order order = mapper.readValue(req.getInputStream(), Order.class);
            Order created = orderService.createOrder(order);
            writeJson(resp, 201, created);
        } catch (IllegalArgumentException e) {
            writeJson(resp, 400, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, 500, Map.of("error", e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                writeJson(resp, 400, Map.of("error", "Order ID required"));
                return;
            }
            Long id = Long.parseLong(pathInfo.substring(1));
            String action = req.getParameter("action");

            Order updated = switch (action) {
                case "confirm" -> orderService.confirmOrder(id);
                case "cancel" -> orderService.cancelOrder(id);
                case "complete" -> orderService.completeOrder(id);
                default -> throw new IllegalArgumentException("Unknown action: " + action);
            };
            writeJson(resp, 200, updated);
        } catch (IllegalArgumentException e) {
            writeJson(resp, 400, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, 500, Map.of("error", e.getMessage()));
        }
    }

    private void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        mapper.writeValue(resp.getOutputStream(), body);
    }
}
