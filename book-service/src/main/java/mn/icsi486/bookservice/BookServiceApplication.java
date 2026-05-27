package mn.icsi486.bookservice;

import mn.icsi486.bookservice.config.DatabaseInit;
import mn.icsi486.bookservice.controller.BookController;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class BookServiceApplication {

    public static void main(String[] args) throws Exception {
        DatabaseInit.initialize();

        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        context.addServlet(new ServletHolder(new BookController()), "/api/books/*");

        server.setHandler(context);
        server.start();

        System.out.println("=== Book Service started on port 8081 ===");
        System.out.println("=== Endpoints: GET/POST /api/books ===");

        server.join();
    }
}
