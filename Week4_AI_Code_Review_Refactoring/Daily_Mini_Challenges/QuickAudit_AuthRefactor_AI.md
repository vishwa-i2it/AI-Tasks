# Daily Mini-Challenge: The Quick Audit — Authentication Refactor (Node.js → Java)

## 1. The "Ugly" Code (Node.js)

> The original `auth.js` mixes validation, storage, and token logic in long, repetitive functions with global state.

```js
// ... excerpt from authenticateUser and registerUser ...
async function authenticateUser(email, password) {
    // ... validation ...
    // ... user lookup ...
    // ... bcrypt compare ...
    // ... JWT sign ...
}

async function registerUser(email, password) {
    // ... validation ...
    // ... check for existing user ...
    // ... bcrypt hash ...
    // ... store user ...
    // ... JWT sign ...
}
```

## 2. The Refactored Code (Java)

### User Entity
```java
public class User {
    private String id;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    // getters, setters, constructor
}
```

### UserRepository (In-Memory)
```java
public interface UserRepository {
    Optional<User> findByEmail(String email);
    void save(User user);
    boolean existsByEmail(String email);
}

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    // ... implementation ...
}
```

### ValidationService
```java
public class ValidationService {
    public boolean isValidEmail(String email) { /* regex */ }
    public boolean isValidPassword(String password) { /* regex */ }
}
```

### TokenService (JWT)
```java
public class TokenService {
    public String generateToken(User user) { /* use jjwt or similar */ }
    public boolean validateToken(String token) { /* ... */ }
}
```

### AuthService
```java
public class AuthService {
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final TokenService tokenService;
    // ... constructor ...

    public AuthResult register(String email, String password) { /* ... */ }
    public AuthResult authenticate(String email, String password) { /* ... */ }
}
```

### DTOs
```java
public class AuthResult {
    private boolean success;
    private String token;
    private String error;
    private User user;
    // ... constructor, getters ...
}
```

## 3. Rationale & Benefits
- **Separation of Concerns:** Each class has a single responsibility.
- **No Global State:** All dependencies are injected, making the code testable.
- **Extensible:** Easy to swap out storage, validation, or token logic.
- **Testable:** Each service can be unit tested in isolation.
- **Production-Ready:** Ready for real database, secure JWT, and robust validation.

---

**Summary:**
This refactor demonstrates how AI can guide the transformation of legacy, hard-to-maintain code into clean, modular, and production-ready Java code using modern best practices. 