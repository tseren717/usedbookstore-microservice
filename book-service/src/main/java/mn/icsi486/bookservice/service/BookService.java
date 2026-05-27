package mn.icsi486.bookservice.service;

import mn.icsi486.bookservice.domain.Book;
import mn.icsi486.bookservice.repository.BookRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BookService {

    private final BookRepository repository = new BookRepository();

    public Book createBook(Book book) {
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new IllegalArgumentException("Номын нэр заавал бөглөнө");
        }
        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            throw new IllegalArgumentException("Зохиолчийн нэр заавал бөглөнө");
        }
        if (book.getPrice() <= 0 || book.getPrice() > 200000) {
            throw new IllegalArgumentException("Үнэ 1-200,000 хооронд байх ёстой");
        }
        if (book.getSellerUsername() == null || book.getSellerUsername().isBlank()) {
            throw new IllegalArgumentException("Худалдагчийн нэр заавал бөглөнө");
        }
        book.setStatus("AVAILABLE");
        book.setCreatedAt(LocalDateTime.now());
        return repository.save(book);
    }

    public Optional<Book> getById(Long id) {
        return repository.findById(id);
    }

    public List<Book> listAvailable() {
        return repository.findByStatus("AVAILABLE");
    }

    public List<Book> listAll() {
        return repository.findAll();
    }

    public List<Book> listBySeller(String username) {
        return repository.findBySellerUsername(username);
    }

    public Book updateBook(Long id, Book updated) {
        Book existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ном олдсонгүй: " + id));
        existing.setTitle(updated.getTitle() != null ? updated.getTitle() : existing.getTitle());
        existing.setAuthor(updated.getAuthor() != null ? updated.getAuthor() : existing.getAuthor());
        existing.setIsbn(updated.getIsbn());
        existing.setPrice(updated.getPrice() > 0 ? updated.getPrice() : existing.getPrice());
        existing.setDescription(updated.getDescription());
        existing.setCondition(updated.getCondition() != null ? updated.getCondition() : existing.getCondition());
        existing.setCategory(updated.getCategory() != null ? updated.getCategory() : existing.getCategory());
        return repository.update(existing);
    }

    public Book markAsSold(Long id) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ном олдсонгүй: " + id));
        book.setStatus("SOLD");
        return repository.update(book);
    }

    public Book updateImagePath(Long id, String imagePath) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ном олдсонгүй: " + id));
        book.setImagePath(imagePath);
        return repository.update(book);
    }

    public void deleteBook(Long id) {
        repository.deleteById(id);
    }
}
