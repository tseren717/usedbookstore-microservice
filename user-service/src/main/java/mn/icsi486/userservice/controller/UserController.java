package mn.icsi486.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mn.icsi486.userservice.domain.User;
import mn.icsi486.userservice.service.UserService;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UserController extends HttpServlet {

    private final UserService userService = new UserService();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(200);
            return;
        }
        super.service(req, resp);
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.startsWith("/")) {
                String username = pathInfo.substring(1);
                var user = userService.getByUsername(username);
                if (user.isPresent()) {
                    User u = user.get();
                    writeJson(resp, 200, Map.of(
                            "id", u.getId(),
                            "username", u.getUsername(),
                            "role", u.getRole()
                    ));
                } else {
                    writeJson(resp, 404, Map.of("error", "Хэрэглэгч олдсонгүй"));
                }
            } else {
                List<User> users = userService.listAll();
                var result = users.stream().map(u -> Map.of(
                        "id", (Object) u.getId(),
                        "username", (Object) u.getUsername(),
                        "role", (Object) u.getRole()
                )).toList();
                writeJson(resp, 200, result);
            }
        } catch (Exception e) {
            writeJson(resp, 500, Map.of("error", e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();

        try {
            if ("/login".equals(pathInfo)) {
                Map<String, String> body = mapper.readValue(req.getInputStream(), Map.class);
                User user = userService.login(body.get("username"), body.get("password"));
                writeJson(resp, 200, Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "message", "Амжилттай нэвтэрлээ"
                ));
            } else if ("/register".equals(pathInfo)) {
                Map<String, String> body = mapper.readValue(req.getInputStream(), Map.class);
                User user = userService.register(
                        body.get("username"),
                        body.get("password"),
                        body.get("confirmPassword")
                );
                writeJson(resp, 201, Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "message", "Бүртгэл амжилттай"
                ));
            } else {
                writeJson(resp, 400, Map.of("error", "Use /api/users/login or /api/users/register"));
            }
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
