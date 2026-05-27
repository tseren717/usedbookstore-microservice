package mn.icsi486.userservice;

import mn.icsi486.userservice.config.DatabaseInit;
import mn.icsi486.userservice.controller.UserController;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class UserServiceApplication {

    public static void main(String[] args) throws Exception {
        DatabaseInit.initialize();

        Server server = new Server(8083);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        context.addServlet(new ServletHolder(new UserController()), "/api/users/*");

        server.setHandler(context);
        server.start();

        System.out.println("=== User Service started on port 8083 ===");
        System.out.println("=== Endpoints: POST /api/users/register, POST /api/users/login ===");

        server.join();
    }
}
