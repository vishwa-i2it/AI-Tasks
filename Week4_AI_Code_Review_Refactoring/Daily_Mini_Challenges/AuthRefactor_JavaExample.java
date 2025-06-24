import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// User entity
class User {
    private String id;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    public User(String id, String email, String passwordHash, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

// UserRepository interface and in-memory implementation
interface UserRepository {
    Optional<User> findByEmail(String email);
    void save(User user);
    boolean existsByEmail(String email);
}
class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(users.get(email.toLowerCase()));
    }
    public void save(User user) {
        users.put(user.getEmail().toLowerCase(), user);
    }
    public boolean existsByEmail(String email) {
        return users.containsKey(email.toLowerCase());
    }
}

// ValidationService
class ValidationService {
    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    }
    public boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }
}

// TokenService (stub JWT logic)
class TokenService {
    public String generateToken(User user) {
        // Stub: In real code, use jjwt or similar
        return Base64.getEncoder().encodeToString((user.getId() + ":" + user.getEmail()).getBytes());
    }
    public boolean validateToken(String token) {
        // Stub: Always valid for demo
        return token != null && !token.isEmpty();
    }
}

// DTO for auth result
class AuthResult {
    public final boolean success;
    public final String token;
    public final String error;
    public final User user;
    public AuthResult(boolean success, String token, String error, User user) {
        this.success = success;
        this.token = token;
        this.error = error;
        this.user = user;
    }
}

// AuthService
class AuthService {
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final TokenService tokenService;
    public AuthService(UserRepository userRepository, ValidationService validationService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
        this.tokenService = tokenService;
    }
    public AuthResult register(String email, String password) {
        if (email == null || password == null)
            return new AuthResult(false, null, "Email and password required", null);
        if (!validationService.isValidEmail(email))
            return new AuthResult(false, null, "Invalid email format", null);
        if (!validationService.isValidPassword(password))
            return new AuthResult(false, null, "Password too weak", null);
        if (userRepository.existsByEmail(email))
            return new AuthResult(false, null, "User already exists", null);
        // For demo, store plain password (never do this in production!)
        User user = new User(UUID.randomUUID().toString(), email, password, LocalDateTime.now());
        userRepository.save(user);
        String token = tokenService.generateToken(user);
        return new AuthResult(true, token, null, user);
    }
    public AuthResult authenticate(String email, String password) {
        if (email == null || password == null)
            return new AuthResult(false, null, "Email and password required", null);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty())
            return new AuthResult(false, null, "Invalid credentials", null);
        User user = userOpt.get();
        // For demo, compare plain password (use bcrypt in real code)
        if (!user.getPasswordHash().equals(password))
            return new AuthResult(false, null, "Invalid credentials", null);
        String token = tokenService.generateToken(user);
        return new AuthResult(true, token, null, user);
    }
}

// Demo usage
class Demo {
    public static void main(String[] args) {
        UserRepository repo = new InMemoryUserRepository();
        ValidationService validator = new ValidationService();
        TokenService tokenService = new TokenService();
        AuthService authService = new AuthService(repo, validator, tokenService);
        AuthResult reg = authService.register("user@example.com", "Password1!");
        System.out.println("Register: " + reg.success + ", token=" + reg.token);
        AuthResult auth = authService.authenticate("user@example.com", "Password1!");
        System.out.println("Authenticate: " + auth.success + ", token=" + auth.token);
    }
} 