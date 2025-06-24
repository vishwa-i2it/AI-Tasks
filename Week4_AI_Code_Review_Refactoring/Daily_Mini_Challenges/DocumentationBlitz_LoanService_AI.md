# Daily Mini-Challenge: Documentation Blitz — LoanService.borrowBook

## 1. Original Function (Before)

```java
@Transactional
public Loan borrowBook(Long bookId, Long memberId) {
    // Validate book exists and is available
    Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
    
    if (!book.isAvailable()) {
        throw new BusinessRuleException("Book is not available for borrowing");
    }
    
    // Validate member exists and is active
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));
    
    if (member.getStatus() != Member.Status.ACTIVE) {
        throw new BusinessRuleException("Member is not active and cannot borrow books");
    }
    
    // Check if member has too many active loans (optional business rule)
    long activeLoanCount = loanRepository.countActiveLoansByMember(memberId);
    if (activeLoanCount >= 5) { // Assuming max 5 books per member
        throw new BusinessRuleException("Member has reached the maximum number of active loans");
    }
    
    // Create loan
    Loan loan = new Loan();
    loan.setBook(book);
    loan.setMember(member);
    loan.setStatus(Loan.Status.BORROWED);
    // Due date is automatically set to 14 days from now in the entity's @PrePersist method
    
    // Update book availability
    book.decrementAvailableQuantity();
    bookRepository.save(book);
    
    return loanRepository.save(loan);
}
```

## 2. With AI-Generated Javadoc (After)

```java
/**
 * Borrows a book for a member, enforcing all business rules.
 * <p>
 * This method performs the following steps:
 * <ul>
 *   <li>Validates that the book exists and is available for borrowing.</li>
 *   <li>Validates that the member exists and is active.</li>
 *   <li>Checks that the member has not exceeded the maximum number of active loans (5).</li>
 *   <li>Creates a new loan record with status BORROWED.</li>
 *   <li>Decrements the book's available quantity.</li>
 *   <li>Saves the updated book and loan entities.</li>
 * </ul>
 *
 * @param bookId   the ID of the book to borrow
 * @param memberId the ID of the member borrowing the book
 * @return the created Loan entity
 * @throws ResourceNotFoundException if the book or member does not exist
 * @throws BusinessRuleException     if the book is unavailable, the member is inactive, or the member has too many active loans
 */
@Transactional
public Loan borrowBook(Long bookId, Long memberId) {
    // ... method body unchanged ...
}
```

## 3. Rationale & Best Practices
- **Clarity:** Javadoc explains the method's purpose, steps, and business rules.
- **API Usability:** Documents parameters, return value, and exceptions.
- **Maintainability:** Future developers can quickly understand and safely modify the method.
- **Professionalism:** Follows Java documentation standards for enterprise codebases.

---

**Summary:**
This documentation blitz shows how AI can instantly generate clear, professional Javadoc for complex business logic, improving code readability and maintainability for teams. 