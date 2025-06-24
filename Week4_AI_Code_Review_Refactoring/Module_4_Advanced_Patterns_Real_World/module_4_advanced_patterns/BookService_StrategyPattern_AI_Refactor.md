# AI-Powered Refactor: Strategy Pattern for BookService Search

## AI Prompt
```
Refactor the BookService's searchBooksAdvanced method to use the Strategy Pattern. Each search type (by term, by advanced criteria) should be encapsulated in its own strategy class. The service should delegate to the appropriate strategy at runtime.
```

---

## Before (Current Implementation)
```java
public BookSearchResponse searchBooksAdvanced(BookSearchRequest request) {
    Sort sort = Sort.by(
        request.getSortDir().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
        request.getSortBy()
    );
    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

    Page<Book> page;
    if (request.getSearchTerm() != null && !request.getSearchTerm().trim().isEmpty()) {
        page = bookRepository.searchByTerm(request.getSearchTerm().trim(), pageable);
    } else {
        page = bookRepository.searchBooksAdvanced(
            request.getTitle(),
            request.getAuthor(),
            request.getGenre(),
            request.getIsbn(),
            request.getAvailable(),
            request.getMinYear(),
            request.getMaxYear(),
            pageable
        );
    }
    return BookSearchResponse.fromPage(page);
}
```

---

## After (Strategy Pattern Implementation)

### 1. Define the Strategy Interface
```java
public interface BookSearchStrategy {
    BookSearchResponse search(BookSearchRequest request);
}
```

### 2. Implement Concrete Strategies
```java
// Search by term
public class TermSearchStrategy implements BookSearchStrategy {
    private final BookRepository bookRepository;
    public TermSearchStrategy(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    @Override
    public BookSearchResponse search(BookSearchRequest request) {
        Sort sort = Sort.by(
            request.getSortDir().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            request.getSortBy()
        );
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        Page<Book> page = bookRepository.searchByTerm(request.getSearchTerm().trim(), pageable);
        return BookSearchResponse.fromPage(page);
    }
}

// Advanced search
public class AdvancedSearchStrategy implements BookSearchStrategy {
    private final BookRepository bookRepository;
    public AdvancedSearchStrategy(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    @Override
    public BookSearchResponse search(BookSearchRequest request) {
        Sort sort = Sort.by(
            request.getSortDir().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            request.getSortBy()
        );
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        Page<Book> page = bookRepository.searchBooksAdvanced(
            request.getTitle(),
            request.getAuthor(),
            request.getGenre(),
            request.getIsbn(),
            request.getAvailable(),
            request.getMinYear(),
            request.getMaxYear(),
            pageable
        );
        return BookSearchResponse.fromPage(page);
    }
}
```

### 3. Strategy Selector (Factory)
```java
public class BookSearchStrategyFactory {
    private final BookRepository bookRepository;
    public BookSearchStrategyFactory(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    public BookSearchStrategy getStrategy(BookSearchRequest request) {
        if (request.getSearchTerm() != null && !request.getSearchTerm().trim().isEmpty()) {
            return new TermSearchStrategy(bookRepository);
        } else {
            return new AdvancedSearchStrategy(bookRepository);
        }
    }
}
```

### 4. Refactor BookService to Use the Strategy
```java
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    public BookSearchResponse searchBooksAdvanced(BookSearchRequest request) {
        BookSearchStrategyFactory factory = new BookSearchStrategyFactory(bookRepository);
        BookSearchStrategy strategy = factory.getStrategy(request);
        return strategy.search(request);
    }
    // ... other methods unchanged ...
}
```

---

## Rationale: Why Use the Strategy Pattern?
- **Separation of Concerns:** Each search type is encapsulated in its own class, making the code easier to maintain and extend.
- **Open/Closed Principle:** Adding new search strategies (e.g., by publisher, by rating) requires only new strategy classes, not changes to existing logic.
- **Testability:** Each strategy can be unit tested independently.

---

**Summary:**
This refactor demonstrates how AI can guide the application of advanced design patterns to real code, improving maintainability, extensibility, and clarity in complex service logic. 