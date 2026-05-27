package mn.icsi486.userservice.service;

import mn.icsi486.userservice.domain.User;
import mn.icsi486.userservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository repository = new UserRepository();

    public User register(String username, String password, String confirmPassword) {
        if (username == null || username.trim().length() < 3) {
            throw new IllegalArgumentException("Хэрэглэгчийн нэр 3-аас дээш тэмдэгт байх ёстой");
        }
        if (password == null || password.trim().length() < 4) {
            throw new IllegalArgumentException("Нууц үг 4-өөс дээш тэмдэгт байх ёстой");
        }
        if (!password.trim().equals(confirmPassword == null ? "" : confirmPassword.trim())) {
            throw new IllegalArgumentException("Нууц үг таарахгүй байна");
        }
        if (repository.findByUsername(username.trim()).isPresent()) {
            throw new IllegalArgumentException("Энэ хэрэглэгчийн нэр бүртгэлтэй байна");
        }

        User user = new User(null, username.trim(), password.trim(), "USER");
        return repository.save(user);
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Хэрэглэгчийн нэр заавал бөглөнө");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Нууц үг заавал бөглөнө");
        }

        User user = repository.findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("Хэрэглэгчийн нэр эсвэл нууц үг буруу"));

        if (!user.getPassword().equals(password.trim())) {
            throw new IllegalArgumentException("Хэрэглэгчийн нэр эсвэл нууц үг буруу");
        }
        return user;
    }

    public Optional<User> getByUsername(String username) {
        return repository.findByUsername(username);
    }

    public List<User> listAll() {
        return repository.findAll();
    }
}
