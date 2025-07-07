# Week 5 Buggy Demo Spring Boot Project

This project intentionally contains common bugs and vulnerabilities for demonstration and debugging purposes.

## Challenges Present

### Challenge 1: State Management Bug (Beginner)
- The `Cart` model exposes its internal list directly, allowing external mutation and breaking encapsulation.

### Challenge 2: Performance Issue (Intermediate)
- The `CartService` uses an O(n^2) algorithm to check if an item exists in the cart, causing performance degradation as the cart grows.

### Challenge 3: Error Handling Gap (Intermediate)
- The `CartController` does not validate input or handle exceptions, leading to possible server errors and unclear responses.

### Challenge 4: Security Vulnerability (Advanced)
- The `SecurityController` implements an open redirect with no validation, allowing attackers to redirect users to malicious sites.

## Workflow
- **Initial Analysis:** AI tools and code review were used to identify bugs, bottlenecks, and vulnerabilities.
- **Core Bug Fixes:** Will be addressed in the `week5-buggy-demo-fixed` project.
- **Advanced Debugging:** Error handling and security issues will be fixed in the next version.
- **Production Readiness:** Monitoring, error handling, and documentation will be improved in the fixed version.

---

See the `week5-buggy-demo-fixed` folder for the corrected implementation and detailed documentation of fixes. 