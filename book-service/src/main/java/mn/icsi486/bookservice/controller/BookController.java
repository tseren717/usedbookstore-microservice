package mn.icsi486.bookservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mn.icsi486.bookservice.domain.Book;
import mn.icsi486.bookservice.service.BookService;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BookController extends HttpServlet {

    private final BookService bookService = new BookService();
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
            if (pathInfo == null || pathInfo.equals("/")) {
                String seller = req.getParameter("seller");
                String status = req.getParameter("status");
                List<Book> books;
                if (seller != null && !seller.isBlank()) {
                    books = bookService.listBySeller(seller);
                } else if ("all".equals(status)) {
                    books = bookService.listAll();
                } else {
                    books = bookService.listAvailable();
                }
                writeJson(resp, 200, books);
            } else {
                Long id = Long.parseLong(pathInfo.substring(1));
                Optional<Book> book = bookService.getById(id);
                if (book.isPresent()) {
                    writeJson(resp, 200, book.get());
                } else {
                    writeJson(resp, 404, Map.of("error", "Ном олдсонгүй"));
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
            Book book = mapper.readValue(req.getInputStream(), Book.class);
            Book created = bookService.createBook(book);
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
                writeJson(resp, 400, Map.of("error", "Book ID required"));
                return;
            }
            Long id = Long.parseLong(pathInfo.substring(1));

            String action = req.getParameter("action");
            if ("sold".equals(action)) {
                Book updated = bookService.markAsSold(id);
                writeJson(resp, 200, updated);
            } else {
                Book book = mapper.readValue(req.getInputStream(), Book.class);
                Book updated = bookService.updateBook(id, book);
                writeJson(resp, 200, updated);
            }
        } catch (IllegalArgumentException e) {
            writeJson(resp, 400, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, 500, Map.of("error", e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                writeJson(resp, 400, Map.of("error", "Book ID required"));
                return;
            }
            Long id = Long.parseLong(pathInfo.substring(1));
            bookService.deleteBook(id);
            writeJson(resp, 200, Map.of("message", "Ном устгагдлаа"));
        } catch (Exception e) {
            writeJson(resp, 500, Map.of("error", e.getMessage()));
        }
    }

    private void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        mapper.writeValue(resp.getOutputStream(), body);
    }
}
