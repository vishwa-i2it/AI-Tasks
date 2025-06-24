import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for the Calculator class.
 * 
 * These tests follow the GWT (Given, When, Then) pattern and cover:
 * - All public methods
 * - Success and failure scenarios
 * - Boundary value testing
 * - Edge cases and error conditions
 * 
 * @author TDD Calculator Project
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Calculator Tests")
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Nested
    @DisplayName("Add Method Tests - Double Parameters")
    class AddDoubleTests {

        @Test
        @DisplayName("Should add two positive integers successfully")
        void shouldAddTwoPositiveIntegers() {
            // Given
            Double a = 2.0;
            Double b = 3.0;
            Double expectedResult = 5.0;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding 2.0 + 3.0 should equal 5.0");
        }

        @Test
        @DisplayName("Should add two negative numbers successfully")
        void shouldAddTwoNegativeNumbers() {
            // Given
            Double a = -5.0;
            Double b = -3.0;
            Double expectedResult = -8.0;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding -5.0 + (-3.0) should equal -8.0");
        }

        @Test
        @DisplayName("Should add positive and negative numbers successfully")
        void shouldAddPositiveAndNegativeNumbers() {
            // Given
            Double a = 10.0;
            Double b = -4.0;
            Double expectedResult = 6.0;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding 10.0 + (-4.0) should equal 6.0");
        }

        @Test
        @DisplayName("Should add decimal numbers successfully")
        void shouldAddDecimalNumbers() {
            // Given
            Double a = 3.5;
            Double b = 2.7;
            Double expectedResult = 6.2;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, 0.001, "Adding 3.5 + 2.7 should equal 6.2");
        }

        @Test
        @DisplayName("Should add zero successfully")
        void shouldAddZero() {
            // Given
            Double a = 5.0;
            Double b = 0.0;
            Double expectedResult = 5.0;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding 5.0 + 0.0 should equal 5.0");
        }

        @Test
        @DisplayName("Should add two zeros successfully")
        void shouldAddTwoZeros() {
            // Given
            Double a = 0.0;
            Double b = 0.0;
            Double expectedResult = 0.0;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding 0.0 + 0.0 should equal 0.0");
        }

        @ParameterizedTest
        @CsvSource({
            "1.0, 1.0, 2.0",
            "0.1, 0.2, 0.3",
            "-1.0, -1.0, -2.0",
            "100.0, 200.0, 300.0",
            "0.001, 0.002, 0.003"
        })
        @DisplayName("Should add various number combinations successfully")
        void shouldAddVariousNumberCombinations(Double a, Double b, Double expected) {
            // Given - parameters provided by @CsvSource

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expected, result, 0.001, 
                String.format("Adding %.3f + %.3f should equal %.3f", a, b, expected));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when first parameter is null")
        void shouldThrowExceptionWhenFirstParameterIsNull() {
            // Given
            Double a = null;
            Double b = 5.0;

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(a, b), 
                "Should throw IllegalArgumentException when first parameter is null");
            
            assertEquals("First parameter cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when second parameter is null")
        void shouldThrowExceptionWhenSecondParameterIsNull() {
            // Given
            Double a = 5.0;
            Double b = null;

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(a, b), 
                "Should throw IllegalArgumentException when second parameter is null");
            
            assertEquals("Second parameter cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when first parameter is NaN")
        void shouldThrowExceptionWhenFirstParameterIsNaN() {
            // Given
            Double a = Double.NaN;
            Double b = 5.0;

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(a, b), 
                "Should throw IllegalArgumentException when first parameter is NaN");
            
            assertEquals("First parameter cannot be NaN (Not a Number)", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when second parameter is NaN")
        void shouldThrowExceptionWhenSecondParameterIsNaN() {
            // Given
            Double a = 5.0;
            Double b = Double.NaN;

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(a, b), 
                "Should throw IllegalArgumentException when second parameter is NaN");
            
            assertEquals("Second parameter cannot be NaN (Not a Number)", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when first parameter is positive infinity")
        void shouldThrowExceptionWhenFirstParameterIsPositiveInfinity() {
            // Given
            Double a = Double.POSITIVE_INFINITY;
            Double b = 5.0;

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(a, b), 
                "Should throw IllegalArgumentException when first parameter is positive infinity");
            
            assertEquals("First parameter cannot be infinite", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when first parameter is negative infinity")
        void shouldThrowExceptionWhenFirstParameterIsNegativeInfinity() {
            // Given
            Double a = Double.NEGATIVE_INFINITY;
            Double b = 5.0;

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(a, b), 
                "Should throw IllegalArgumentException when first parameter is negative infinity");
            
            assertEquals("First parameter cannot be infinite", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when second parameter is positive infinity")
        void shouldThrowExceptionWhenSecondParameterIsPositiveInfinity() {
            // Given
            Double a = 5.0;
            Double b = Double.POSITIVE_INFINITY;

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(a, b), 
                "Should throw IllegalArgumentException when second parameter is positive infinity");
            
            assertEquals("Second parameter cannot be infinite", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when second parameter is negative infinity")
        void shouldThrowExceptionWhenSecondParameterIsNegativeInfinity() {
            // Given
            Double a = 5.0;
            Double b = Double.NEGATIVE_INFINITY;

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(a, b), 
                "Should throw IllegalArgumentException when second parameter is negative infinity");
            
            assertEquals("Second parameter cannot be infinite", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw ArithmeticException when addition would cause overflow")
        void shouldThrowArithmeticExceptionWhenAdditionWouldCauseOverflow() {
            // Given
            Double a = Double.MAX_VALUE;
            Double b = Double.MAX_VALUE;

            // When & Then
            ArithmeticException exception = assertThrows(ArithmeticException.class, 
                () -> calculator.add(a, b), 
                "Should throw ArithmeticException when addition would cause overflow");
            
            assertTrue(exception.getMessage().contains("Addition would cause overflow"));
        }

        @Test
        @DisplayName("Should throw ArithmeticException when addition would cause underflow")
        void shouldThrowArithmeticExceptionWhenAdditionWouldCauseUnderflow() {
            // Given
            Double a = -Double.MAX_VALUE;
            Double b = -Double.MAX_VALUE;

            // When & Then
            ArithmeticException exception = assertThrows(ArithmeticException.class, 
                () -> calculator.add(a, b), 
                "Should throw ArithmeticException when addition would cause underflow");
            
            assertTrue(exception.getMessage().contains("Addition would cause underflow"));
        }
    }

    @Nested
    @DisplayName("Add Method Tests - Primitive Double Parameters")
    class AddPrimitiveDoubleTests {

        @Test
        @DisplayName("Should add two positive primitive doubles successfully")
        void shouldAddTwoPositivePrimitiveDoubles() {
            // Given
            double a = 2.0;
            double b = 3.0;
            double expectedResult = 5.0;

            // When
            double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding 2.0 + 3.0 should equal 5.0");
        }

        @Test
        @DisplayName("Should add two negative primitive doubles successfully")
        void shouldAddTwoNegativePrimitiveDoubles() {
            // Given
            double a = -5.0;
            double b = -3.0;
            double expectedResult = -8.0;

            // When
            double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding -5.0 + (-3.0) should equal -8.0");
        }

        @Test
        @DisplayName("Should add positive and negative primitive doubles successfully")
        void shouldAddPositiveAndNegativePrimitiveDoubles() {
            // Given
            double a = 10.0;
            double b = -4.0;
            double expectedResult = 6.0;

            // When
            double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding 10.0 + (-4.0) should equal 6.0");
        }

        @Test
        @DisplayName("Should add decimal primitive doubles successfully")
        void shouldAddDecimalPrimitiveDoubles() {
            // Given
            double a = 3.5;
            double b = 2.7;
            double expectedResult = 6.2;

            // When
            double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, 0.001, "Adding 3.5 + 2.7 should equal 6.2");
        }

        @Test
        @DisplayName("Should add zero primitive doubles successfully")
        void shouldAddZeroPrimitiveDoubles() {
            // Given
            double a = 5.0;
            double b = 0.0;
            double expectedResult = 5.0;

            // When
            double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding 5.0 + 0.0 should equal 5.0");
        }

        @ParameterizedTest
        @CsvSource({
            "1.0, 1.0, 2.0",
            "0.1, 0.2, 0.3",
            "-1.0, -1.0, -2.0",
            "100.0, 200.0, 300.0",
            "0.001, 0.002, 0.003"
        })
        @DisplayName("Should add various primitive double combinations successfully")
        void shouldAddVariousPrimitiveDoubleCombinations(double a, double b, double expected) {
            // Given - parameters provided by @CsvSource

            // When
            double result = calculator.add(a, b);

            // Then
            assertEquals(expected, result, 0.001, 
                String.format("Adding %.3f + %.3f should equal %.3f", a, b, expected));
        }

        @Test
        @DisplayName("Should throw ArithmeticException when primitive addition would cause overflow")
        void shouldThrowArithmeticExceptionWhenPrimitiveAdditionWouldCauseOverflow() {
            // Given
            double a = Double.MAX_VALUE;
            double b = Double.MAX_VALUE;

            // When & Then
            ArithmeticException exception = assertThrows(ArithmeticException.class, 
                () -> calculator.add(a, b), 
                "Should throw ArithmeticException when primitive addition would cause overflow");
            
            assertTrue(exception.getMessage().contains("Addition would cause overflow"));
        }

        @Test
        @DisplayName("Should throw ArithmeticException when primitive addition would cause underflow")
        void shouldThrowArithmeticExceptionWhenPrimitiveAdditionWouldCauseUnderflow() {
            // Given
            double a = -Double.MAX_VALUE;
            double b = -Double.MAX_VALUE;

            // When & Then
            ArithmeticException exception = assertThrows(ArithmeticException.class, 
                () -> calculator.add(a, b), 
                "Should throw ArithmeticException when primitive addition would cause underflow");
            
            assertTrue(exception.getMessage().contains("Addition would cause underflow"));
        }
    }

    @Nested
    @DisplayName("Boundary Value Tests")
    class BoundaryValueTests {

        @Test
        @DisplayName("Should handle maximum double value minus small number")
        void shouldHandleMaximumDoubleValueMinusSmallNumber() {
            // Given
            Double a = Double.MAX_VALUE - 1.0;
            Double b = 1.0;
            Double expectedResult = Double.MAX_VALUE;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding (MAX_VALUE-1) + 1 should equal MAX_VALUE");
        }

        @Test
        @DisplayName("Should handle minimum double value plus small number")
        void shouldHandleMinimumDoubleValuePlusSmallNumber() {
            // Given
            Double a = -Double.MAX_VALUE + 1.0;
            Double b = -1.0;
            Double expectedResult = -Double.MAX_VALUE;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding (-MAX_VALUE+1) + (-1) should equal -MAX_VALUE");
        }

        @Test
        @DisplayName("Should handle very small positive numbers")
        void shouldHandleVerySmallPositiveNumbers() {
            // Given
            Double a = Double.MIN_VALUE;
            Double b = Double.MIN_VALUE;
            Double expectedResult = Double.MIN_VALUE * 2;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding MIN_VALUE + MIN_VALUE should equal 2*MIN_VALUE");
        }

        @Test
        @DisplayName("Should handle very small negative numbers")
        void shouldHandleVerySmallNegativeNumbers() {
            // Given
            Double a = -Double.MIN_VALUE;
            Double b = -Double.MIN_VALUE;
            Double expectedResult = -Double.MIN_VALUE * 2;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding (-MIN_VALUE) + (-MIN_VALUE) should equal -2*MIN_VALUE");
        }

        @Test
        @DisplayName("Should handle epsilon values")
        void shouldHandleEpsilonValues() {
            // Given
            Double a = Double.MIN_NORMAL;
            Double b = Double.MIN_NORMAL;
            Double expectedResult = Double.MIN_NORMAL * 2;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding MIN_NORMAL + MIN_NORMAL should equal 2*MIN_NORMAL");
        }
    }

    @Nested
    @DisplayName("Mockito Integration Tests")
    class MockitoIntegrationTests {

        @Mock
        private Calculator mockCalculator;

        @Test
        @DisplayName("Should verify calculator instance creation")
        void shouldVerifyCalculatorInstanceCreation() {
            // Given
            Calculator calculator = new Calculator();

            // When & Then
            assertNotNull(calculator, "Calculator instance should not be null");
            assertTrue(calculator instanceof Calculator, "Should be an instance of Calculator");
        }

        @Test
        @DisplayName("Should use mock calculator for testing")
        void shouldUseMockCalculatorForTesting() {
            // Given
            when(mockCalculator.add(2.0, 3.0)).thenReturn(5.0);

            // When
            double result = mockCalculator.add(2.0, 3.0);

            // Then
            assertEquals(5.0, result, "Mock should return expected value");
            verify(mockCalculator, times(1)).add(2.0, 3.0);
        }

        @Test
        @DisplayName("Should verify mock behavior with different inputs")
        void shouldVerifyMockBehaviorWithDifferentInputs() {
            // Given
            when(mockCalculator.add(1.0, 2.0)).thenReturn(3.0);
            when(mockCalculator.add(5.0, 3.0)).thenReturn(8.0);

            // When
            double result1 = mockCalculator.add(1.0, 2.0);
            double result2 = mockCalculator.add(5.0, 3.0);

            // Then
            assertEquals(3.0, result1, "First mock call should return 3.0");
            assertEquals(8.0, result2, "Second mock call should return 8.0");
            verify(mockCalculator, times(1)).add(1.0, 2.0);
            verify(mockCalculator, times(1)).add(5.0, 3.0);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very large numbers that don't overflow")
        void shouldHandleVeryLargeNumbersThatDontOverflow() {
            // Given
            Double a = Double.MAX_VALUE / 2;
            Double b = Double.MAX_VALUE / 2;
            Double expectedResult = Double.MAX_VALUE;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding MAX_VALUE/2 + MAX_VALUE/2 should equal MAX_VALUE");
        }

        @Test
        @DisplayName("Should handle very small numbers that don't underflow")
        void shouldHandleVerySmallNumbersThatDontUnderflow() {
            // Given
            Double a = -Double.MAX_VALUE / 2;
            Double b = -Double.MAX_VALUE / 2;
            Double expectedResult = -Double.MAX_VALUE;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, "Adding (-MAX_VALUE/2) + (-MAX_VALUE/2) should equal -MAX_VALUE");
        }

        @Test
        @DisplayName("Should handle precision loss with very small decimals")
        void shouldHandlePrecisionLossWithVerySmallDecimals() {
            // Given
            Double a = 0.1;
            Double b = 0.2;
            Double expectedResult = 0.3;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, 0.0000001, 
                "Adding 0.1 + 0.2 should equal 0.3 with high precision");
        }

        @Test
        @DisplayName("Should handle mixed precision numbers")
        void shouldHandleMixedPrecisionNumbers() {
            // Given
            Double a = 1.0;
            Double b = 0.0000000000000001;
            Double expectedResult = 1.0000000000000001;

            // When
            Double result = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result, 0.0000000000000001, 
                "Adding 1.0 + very small number should maintain precision");
        }
    }

    @Nested
    @DisplayName("Performance and Stress Tests")
    class PerformanceAndStressTests {

        @Test
        @DisplayName("Should handle multiple rapid calculations")
        void shouldHandleMultipleRapidCalculations() {
            // Given
            int iterations = 1000;
            double expectedSum = 0.0;

            // When
            for (int i = 0; i < iterations; i++) {
                double result = calculator.add(i, i);
                expectedSum += result;
            }

            // Then
            assertTrue(expectedSum > 0, "Should complete multiple calculations successfully");
        }

        @Test
        @DisplayName("Should maintain consistency across multiple calls")
        void shouldMaintainConsistencyAcrossMultipleCalls() {
            // Given
            Double a = 2.5;
            Double b = 3.7;
            Double expectedResult = 6.2;

            // When
            Double result1 = calculator.add(a, b);
            Double result2 = calculator.add(a, b);
            Double result3 = calculator.add(a, b);

            // Then
            assertEquals(expectedResult, result1, 0.001, "First call should be correct");
            assertEquals(expectedResult, result2, 0.001, "Second call should be correct");
            assertEquals(expectedResult, result3, 0.001, "Third call should be correct");
            assertEquals(result1, result2, "Results should be consistent");
            assertEquals(result2, result3, "Results should be consistent");
        }
    }
} 