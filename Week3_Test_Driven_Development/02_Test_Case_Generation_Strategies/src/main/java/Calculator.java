/**
 * A simple calculator class that provides basic arithmetic operations.
 * This class follows the Test-Driven Development (TDD) approach and
 * includes comprehensive input validation and error handling.
 * 
 * @author TDD Calculator Project
 * @version 1.0.0
 * @since 1.0.0
 */
public class Calculator {
    
    /**
     * Adds two numbers together with comprehensive input validation.
     * 
     * <p>This method performs the following validations:</p>
     * <ul>
     *   <li>Checks for null parameters (throws IllegalArgumentException)</li>
     *   <li>Validates that parameters are finite numbers (not NaN or infinite)</li>
     *   <li>Handles edge cases like overflow gracefully</li>
     * </ul>
     * 
     * <p>Examples of valid inputs:</p>
     * <ul>
     *   <li>Positive integers: add(2, 3) returns 5.0</li>
     *   <li>Negative numbers: add(-5, -3) returns -8.0</li>
     *   <li>Decimal numbers: add(3.5, 2.7) returns 6.2</li>
     *   <li>Mixed signs: add(10, -4) returns 6.0</li>
     * </ul>
     * 
     * @param a the first number to add (must be finite)
     * @param b the second number to add (must be finite)
     * @return the sum of a and b
     * @throws IllegalArgumentException if either parameter is null or not a finite number
     * @throws ArithmeticException if the result would overflow or underflow
     * 
     * @see Double#isFinite(double)
     * @see Double#isNaN(double)
     * @see Double#isInfinite(double)
     */
    public double add(Double a, Double b) {
        // Input validation
        validateInput(a, "First parameter");
        validateInput(b, "Second parameter");
        
        // Convert to primitive double for arithmetic operations
        double first = a;
        double second = b;
        
        // Check for potential overflow/underflow
        checkForOverflow(first, second);
        
        // Perform the addition
        double result = first + second;
        
        // Validate the result
        validateResult(result);
        
        return result;
    }
    
    /**
     * Validates that the input parameter is not null and is a finite number.
     * 
     * @param value the value to validate
     * @param paramName the name of the parameter for error messages
     * @throws IllegalArgumentException if the value is null or not finite
     */
    private void validateInput(Double value, String paramName) {
        if (value == null) {
            throw new IllegalArgumentException(paramName + " cannot be null");
        }
        
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(paramName + " cannot be NaN (Not a Number)");
        }
        
        if (Double.isInfinite(value)) {
            throw new IllegalArgumentException(paramName + " cannot be infinite");
        }
    }
    
    /**
     * Checks for potential arithmetic overflow or underflow before performing addition.
     * 
     * @param a the first operand
     * @param b the second operand
     * @throws ArithmeticException if the operation would cause overflow or underflow
     */
    private void checkForOverflow(double a, double b) {
        // Check for overflow when adding positive numbers
        if (a > 0 && b > 0 && a > Double.MAX_VALUE - b) {
            throw new ArithmeticException("Addition would cause overflow: " + a + " + " + b);
        }
        
        // Check for underflow when adding negative numbers
        if (a < 0 && b < 0 && a < -Double.MAX_VALUE - b) {
            throw new ArithmeticException("Addition would cause underflow: " + a + " + " + b);
        }
    }
    
    /**
     * Validates that the result is a finite number.
     * 
     * @param result the result to validate
     * @throws ArithmeticException if the result is not finite
     */
    private void validateResult(double result) {
        if (!Double.isFinite(result)) {
            throw new ArithmeticException("Result is not finite: " + result);
        }
    }
    
    /**
     * Convenience method for adding primitive double values.
     * This method wraps the primitive values and delegates to the main add method.
     * 
     * @param a the first number to add
     * @param b the second number to add
     * @return the sum of a and b
     * @throws IllegalArgumentException if either parameter is not a finite number
     * @throws ArithmeticException if the result would overflow or underflow
     */
    public double add(double a, double b) {
        return add(Double.valueOf(a), Double.valueOf(b));
    }
} 