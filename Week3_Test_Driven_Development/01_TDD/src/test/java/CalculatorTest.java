import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {
    
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    void shouldAddTwoPositiveIntegers() {
        // Given: two positive integers 2 and 3
        double a = 2;
        double b = 3;
        
        // When: adding the two numbers
        double result = calculator.add(a, b);
        
        // Then: the result should be 5
        assertEquals(5.0, result, "2 + 3 should equal 5");
    }
    
    @Test
    void shouldAddTwoNegativeNumbers() {
        // Given: two negative numbers -5 and -3
        double a = -5;
        double b = -3;
        
        // When: adding the two negative numbers
        double result = calculator.add(a, b);
        
        // Then: the result should be -8
        assertEquals(-8.0, result, "-5 + (-3) should equal -8");
    }
    
    @Test
    void shouldAddPositiveAndNegativeNumber() {
        // Given: a positive number 10 and a negative number -4
        double a = 10;
        double b = -4;
        
        // When: adding positive and negative number
        double result = calculator.add(a, b);
        
        // Then: the result should be 6
        assertEquals(6.0, result, "10 + (-4) should equal 6");
    }
    
    @Test
    void shouldAddTwoDecimalNumbers() {
        // Given: two decimal numbers 3.5 and 2.7
        double a = 3.5;
        double b = 2.7;
        
        // When: adding the two decimal numbers
        double result = calculator.add(a, b);
        
        // Then: the result should be 6.2
        assertEquals(6.2, result, 0.001, "3.5 + 2.7 should equal 6.2");
    }
    
    @Test
    void shouldAddDecimalAndInteger() {
        // Given: a decimal number 4.25 and an integer 3
        double a = 4.25;
        double b = 3;
        
        // When: adding decimal and integer
        double result = calculator.add(a, b);
        
        // Then: the result should be 7.25
        assertEquals(7.25, result, 0.001, "4.25 + 3 should equal 7.25");
    }
    
    @Test
    void shouldAddZeroToNumber() {
        // Given: a number 7 and zero
        double a = 7;
        double b = 0;
        
        // When: adding zero to the number
        double result = calculator.add(a, b);
        
        // Then: the result should be the original number
        assertEquals(7.0, result, "7 + 0 should equal 7");
    }
    
    @Test
    void shouldAddTwoZeros() {
        // Given: two zeros
        double a = 0;
        double b = 0;
        
        // When: adding two zeros
        double result = calculator.add(a, b);
        
        // Then: the result should be zero
        assertEquals(0.0, result, "0 + 0 should equal 0");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenFirstParameterIsNull() {
        Calculator calculator = new Calculator();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> calculator.add((Double) null, Double.valueOf(2.0)));
        assertTrue(exception.getMessage().contains("First parameter cannot be null"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSecondParameterIsNull() {
        Calculator calculator = new Calculator();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> calculator.add(Double.valueOf(2.0), (Double) null));
        assertTrue(exception.getMessage().contains("Second parameter cannot be null"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenFirstParameterIsNaN() {
        Calculator calculator = new Calculator();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> calculator.add(Double.valueOf(Double.NaN), Double.valueOf(2.0)));
        assertTrue(exception.getMessage().contains("First parameter cannot be NaN"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSecondParameterIsNaN() {
        Calculator calculator = new Calculator();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> calculator.add(Double.valueOf(2.0), Double.valueOf(Double.NaN)));
        assertTrue(exception.getMessage().contains("Second parameter cannot be NaN"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenFirstParameterIsInfinite() {
        Calculator calculator = new Calculator();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> calculator.add(Double.valueOf(Double.POSITIVE_INFINITY), Double.valueOf(2.0)));
        assertTrue(exception.getMessage().contains("First parameter cannot be infinite"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSecondParameterIsInfinite() {
        Calculator calculator = new Calculator();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> calculator.add(Double.valueOf(2.0), Double.valueOf(Double.NEGATIVE_INFINITY)));
        assertTrue(exception.getMessage().contains("Second parameter cannot be infinite"));
    }

    @Test
    void shouldThrowArithmeticExceptionOnOverflow() {
        Calculator calculator = new Calculator();
        double a = Double.MAX_VALUE;
        double b = Double.MAX_VALUE;
        Exception exception = assertThrows(ArithmeticException.class, () -> calculator.add(a, b));
        assertTrue(exception.getMessage().contains("overflow"));
    }

    @Test
    void shouldThrowArithmeticExceptionOnUnderflow() {
        Calculator calculator = new Calculator();
        double a = -Double.MAX_VALUE;
        double b = -Double.MAX_VALUE;
        Exception exception = assertThrows(ArithmeticException.class, () -> calculator.add(a, b));
        assertTrue(exception.getMessage().contains("underflow"));
    }
} 