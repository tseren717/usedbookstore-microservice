package mn.icsi486.orderservice;

import mn.icsi486.orderservice.config.DatabaseInit;
import mn.icsi486.orderservice.controller.OrderController;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class OrderServiceApplication {

    public static void main(String[] args) throws Exception {
        DatabaseInit.initialize();

        Server server = new Server(8082);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        context.addServlet(new ServletHolder(new OrderController()), "/api/orders/*");

        server.setHandler(context);
        server.start();

        System.out.println("=== Order Service started on port 8082 ===");
        System.out.println("=== Endpoints: GET/POST/PUT /api/orders ===");

        server.join();
    }
}
