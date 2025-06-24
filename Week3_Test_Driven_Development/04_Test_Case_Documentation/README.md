# Calculator TDD Project

This project demonstrates Test-Driven Development (TDD) by implementing a simple calculator function that can add two numbers.

## Project Structure

```
├── src/
│   ├── main/java/
│   │   └── Calculator.java          # Calculator class (initially empty implementation)
│   └── test/java/
│       └── CalculatorTest.java      # JUnit tests for Calculator
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## TDD Approach

This project follows the TDD cycle:
1. **Red**: Write failing tests first
2. **Green**: Implement the minimum code to make tests pass
3. **Refactor**: Improve the code while keeping tests green

## Current State: Red Phase

The tests are currently **failing** because the `Calculator.add()` method returns `0.0` instead of the actual sum. This is intentional for the TDD approach.

## Test Cases

The test suite includes:
- ✅ Adding two positive integers (2 + 3 = 5)
- ✅ Adding negative numbers (-5 + (-3) = -8)
- ✅ Adding positive and negative numbers (10 + (-4) = 6)
- ✅ Adding decimal numbers (3.5 + 2.7 = 6.2)
- ✅ Adding decimal and integer (4.25 + 3 = 7.25)
- ✅ Adding zero to a number (7 + 0 = 7)
- ✅ Adding two zeros (0 + 0 = 0)

## Running the Tests

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Run Tests
```bash
mvn test
```

### Run Tests with Details
```bash
mvn test -Dtest=CalculatorTest
```

## Expected Output

Currently, all tests should **FAIL** because the `Calculator.add()` method is not implemented yet. This is the "Red" phase of TDD.

Example failure output:
```
[ERROR] Tests run: 7, Failures: 7, Errors: 0, Skipped: 0
```

## Next Steps (Green Phase)

To complete the TDD cycle, implement the `Calculator.add()` method:

```java
public double add(double a, double b) {
    return a + b;
}
```

After implementation, run `mvn test` again to see all tests pass (Green phase). 