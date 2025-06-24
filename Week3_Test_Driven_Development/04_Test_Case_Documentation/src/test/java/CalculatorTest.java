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
            // Given: Two positive double numbers are provided for addition.
            Double firstNumber = 2.0;
            Double secondNumber = 3.0;
            Double expectedSum = 5.0;

            // When: The add method is called with these numbers.
            Double actualSum = calculator.add(firstNumber, secondNumber);

            // Then: The result should be the sum of the two numbers.
            assertEquals(expectedSum, actualSum, "Adding 2.0 + 3.0 should equal 5.0");
        }

        @Test
        @DisplayName("Should add two negative numbers successfully")
        void shouldAddTwoNegativeNumbers() {
            // Given: Two negative double numbers are provided for addition.
            Double firstNumber = -5.0;
            Double secondNumber = -3.0;
            Double expectedSum = -8.0;

            // When: The add method is called with these numbers.
            Double actualSum = calculator.add(firstNumber, secondNumber);

            // Then: The result should be the sum of the two negative numbers.
            assertEquals(expectedSum, actualSum, "Adding -5.0 + (-3.0) should equal -8.0");
        }

        @Test
        @DisplayName("Should add positive and negative numbers successfully")
        void shouldAddPositiveAndNegativeNumbers() {
            // Given: A positive and a negative double number are provided for addition.
            Double positiveNumber = 10.0;
            Double negativeNumber = -4.0;
            Double expectedSum = 6.0;

            // When: The add method is called with these numbers.
            Double actualSum = calculator.add(positiveNumber, negativeNumber);

            // Then: The result should be the sum, considering the signs.
            assertEquals(expectedSum, actualSum, "Adding 10.0 + (-4.0) should equal 6.0");
        }

        @Test
        @DisplayName("Should add decimal numbers successfully")
        void shouldAddDecimalNumbers() {
            // Given: Two decimal double numbers are provided for addition.
            Double firstDecimal = 3.5;
            Double secondDecimal = 2.7;
            Double expectedSum = 6.2;

            // When: The add method is called with these numbers.
            Double actualSum = calculator.add(firstDecimal, secondDecimal);

            // Then: The result should be the sum of the decimal numbers with a small delta for precision.
            assertEquals(expectedSum, actualSum, 0.001, "Adding 3.5 + 2.7 should equal 6.2");
        }

        @Test
        @DisplayName("Should add zero successfully")
        void shouldAddZero() {
            // Given: A double number and zero are provided for addition.
            Double nonZeroNumber = 5.0;
            Double zeroValue = 0.0;
            Double expectedSum = 5.0;

            // When: The add method is called with these numbers.
            Double actualSum = calculator.add(nonZeroNumber, zeroValue);

            // Then: The result should be the non-zero number.
            assertEquals(expectedSum, actualSum, "Adding 5.0 + 0.0 should equal 5.0");
        }

        @Test
        @DisplayName("Should add two zeros successfully")
        void shouldAddTwoZeros() {
            // Given: Two zero double numbers are provided for addition.
            Double firstZero = 0.0;
            Double secondZero = 0.0;
            Double expectedSum = 0.0;

            // When: The add method is called with two zeros.
            Double actualSum = calculator.add(firstZero, secondZero);

            // Then: The result should be zero.
            assertEquals(expectedSum, actualSum, "Adding 0.0 + 0.0 should equal 0.0");
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
        void shouldAddVariousNumberCombinations(Double firstInput, Double secondInput, Double expectedSum) {
            // Given: Parameters are provided by @CsvSource.

            // When: The add method is called with various double combinations.
            Double actualSum = calculator.add(firstInput, secondInput);

            // Then: The result should match the expected sum with a small delta for precision.
            assertEquals(expectedSum, actualSum, 0.001, 
                String.format("Adding %.3f + %.3f should equal %.3f", firstInput, secondInput, expectedSum));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when first parameter is null")
        void shouldThrowExceptionWhenFirstParameterIsNull() {
            // Given: The first number is null, and the second number is a valid double.
            Double nullFirstNumber = null;
            Double validSecondNumber = 5.0;

            // When & Then: Calling the add method with a null first parameter should throw an IllegalArgumentException.
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(nullFirstNumber, validSecondNumber), 
                "Should throw IllegalArgumentException when first parameter is null");
            
            // Business Logic: The calculator should not accept null inputs for parameters.
            assertEquals("First parameter cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when second parameter is null")
        void shouldThrowExceptionWhenSecondParameterIsNull() {
            // Given: The first number is a valid double, and the second number is null.
            Double validFirstNumber = 5.0;
            Double nullSecondNumber = null;

            // When & Then: Calling the add method with a null second parameter should throw an IllegalArgumentException.
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(validFirstNumber, nullSecondNumber), 
                "Should throw IllegalArgumentException when second parameter is null");
            
            // Business Logic: The calculator should not accept null inputs for parameters.
            assertEquals("Second parameter cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when first parameter is NaN")
        void shouldThrowExceptionWhenFirstParameterIsNaN() {
            // Given: The first number is Not-a-Number (NaN), and the second number is a valid double.
            Double nanFirstNumber = Double.NaN;
            Double validSecondNumber = 5.0;

            // When & Then: Calling the add method with a NaN first parameter should throw an IllegalArgumentException.
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(nanFirstNumber, validSecondNumber), 
                "Should throw IllegalArgumentException when first parameter is NaN");
            
            // Business Logic: The calculator should not perform operations with NaN inputs.
            assertEquals("First parameter cannot be NaN (Not a Number)", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when second parameter is NaN")
        void shouldThrowExceptionWhenSecondParameterIsNaN() {
            // Given: The first number is a valid double, and the second number is Not-a-Number (NaN).
            Double validFirstNumber = 5.0;
            Double nanSecondNumber = Double.NaN;

            // When & Then: Calling the add method with a NaN second parameter should throw an IllegalArgumentException.
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(validFirstNumber, nanSecondNumber), 
                "Should throw IllegalArgumentException when second parameter is NaN");
            
            // Business Logic: The calculator should not perform operations with NaN inputs.
            assertEquals("Second parameter cannot be NaN (Not a Number)", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when first parameter is positive infinity")
        void shouldThrowExceptionWhenFirstParameterIsPositiveInfinity() {
            // Given: The first number is positive infinity, and the second number is a valid double.
            Double positiveInfinityFirstNumber = Double.POSITIVE_INFINITY;
            Double validSecondNumber = 5.0;

            // When & Then: Calling the add method with a positive infinity first parameter should throw an IllegalArgumentException.
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(positiveInfinityFirstNumber, validSecondNumber), 
                "Should throw IllegalArgumentException when first parameter is positive infinity");
            
            // Business Logic: The calculator should not perform operations with infinite inputs.
            assertEquals("First parameter cannot be infinite", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when first parameter is negative infinity")
        void shouldThrowExceptionWhenFirstParameterIsNegativeInfinity() {
            // Given: The first number is negative infinity, and the second number is a valid double.
            Double negativeInfinityFirstNumber = Double.NEGATIVE_INFINITY;
            Double validSecondNumber = 5.0;

            // When & Then: Calling the add method with a negative infinity first parameter should throw an IllegalArgumentException.
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(negativeInfinityFirstNumber, validSecondNumber), 
                "Should throw IllegalArgumentException when first parameter is negative infinity");
            
            // Business Logic: The calculator should not perform operations with infinite inputs.
            assertEquals("First parameter cannot be infinite", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when second parameter is positive infinity")
        void shouldThrowExceptionWhenSecondParameterIsPositiveInfinity() {
            // Given: The first number is a valid double, and the second number is positive infinity.
            Double validFirstNumber = 5.0;
            Double positiveInfinitySecondNumber = Double.POSITIVE_INFINITY;

            // When & Then: Calling the add method with a positive infinity second parameter should throw an IllegalArgumentException.
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> calculator.add(validFirstNumber, positiveInfinitySecondNumber), 
                "Should throw IllegalArgumentException when second parameter is positive infinity");
            
            // Business Logic: The calculator should not perform operations with infinite inputs.
            assertEquals("Second parameter cannot be infinite", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw ArithmeticException when addition would cause overflow")
        void shouldThrowArithmeticExceptionWhenAdditionWouldCauseOverflow() {
            // Given: Two numbers whose sum exceeds Double.MAX_VALUE.
            Double maximumValue = Double.MAX_VALUE;
            Double valueToOverflow = Double.MAX_VALUE;

            // When & Then: Attempting to add these numbers should result in an ArithmeticException.
            ArithmeticException exception = assertThrows(ArithmeticException.class, 
                () -> calculator.add(maximumValue, valueToOverflow), 
                "Should throw ArithmeticException when addition would cause overflow");
            
            // Business Logic: The calculator should prevent overflow for double additions.
            assertTrue(exception.getMessage().contains("Addition would cause overflow"));
        }

        @Test
        @DisplayName("Should throw ArithmeticException when addition would cause underflow")
        void shouldThrowArithmeticExceptionWhenAdditionWouldCauseUnderflow() {
            // Given: Two numbers whose sum goes below -Double.MAX_VALUE.
            Double minimumValue = -Double.MAX_VALUE;
            Double valueToUnderflow = -Double.MAX_VALUE;

            // When & Then: Attempting to add these numbers should result in an ArithmeticException.
            ArithmeticException exception = assertThrows(ArithmeticException.class, 
                () -> calculator.add(minimumValue, valueToUnderflow), 
                "Should throw ArithmeticException when addition would cause underflow");
            
            // Business Logic: The calculator should prevent underflow for double additions.
            assertTrue(exception.getMessage().contains("Addition would cause underflow"));
        }
    }

    @Nested
    @DisplayName("Add Method Tests - Primitive Double Parameters")
    class AddPrimitiveDoubleTests {

        @Test
        @DisplayName("Should add two positive primitive doubles successfully")
        void shouldAddTwoPositivePrimitiveDoubles() {
            // Given: Two positive primitive double numbers are provided for addition.
            double firstNumber = 2.0;
            double secondNumber = 3.0;
            double expectedSum = 5.0;

            // When: The add method is called with these primitive doubles.
            double actualSum = calculator.add(firstNumber, secondNumber);

            // Then: The result should be the sum of the two numbers.
            assertEquals(expectedSum, actualSum, "Adding 2.0 + 3.0 should equal 5.0");
        }

        @Test
        @DisplayName("Should add two negative primitive doubles successfully")
        void shouldAddTwoNegativePrimitiveDoubles() {
            // Given: Two negative primitive double numbers are provided for addition.
            double firstNumber = -5.0;
            double secondNumber = -3.0;
            double expectedSum = -8.0;

            // When: The add method is called with these primitive doubles.
            double actualSum = calculator.add(firstNumber, secondNumber);

            // Then: The result should be the sum of the two negative numbers.
            assertEquals(expectedSum, actualSum, "Adding -5.0 + (-3.0) should equal -8.0");
        }

        @Test
        @DisplayName("Should add positive and negative primitive doubles successfully")
        void shouldAddPositiveAndNegativePrimitiveDoubles() {
            // Given: A positive and a negative primitive double number are provided for addition.
            double positiveNumber = 10.0;
            double negativeNumber = -4.0;
            double expectedSum = 6.0;

            // When: The add method is called with these primitive doubles.
            double actualSum = calculator.add(positiveNumber, negativeNumber);

            // Then: The result should be the sum, considering the signs.
            assertEquals(expectedSum, actualSum, "Adding 10.0 + (-4.0) should equal 6.0");
        }

        @Test
        @DisplayName("Should add decimal primitive doubles successfully")
        void shouldAddDecimalPrimitiveDoubles() {
            // Given: Two decimal primitive double numbers are provided for addition.
            double firstDecimal = 3.5;
            double secondDecimal = 2.7;
            double expectedSum = 6.2;

            // When: The add method is called with these primitive doubles.
            double actualSum = calculator.add(firstDecimal, secondDecimal);

            // Then: The result should be the sum of the decimal numbers with a small delta for precision.
            assertEquals(expectedSum, actualSum, 0.001, "Adding 3.5 + 2.7 should equal 6.2");
        }

        @Test
        @DisplayName("Should add zero primitive doubles successfully")
        void shouldAddZeroPrimitiveDoubles() {
            // Given: A primitive double number and zero are provided for addition.
            double nonZeroNumber = 5.0;
            double zeroValue = 0.0;
            double expectedSum = 5.0;

            // When: The add method is called with these primitive doubles.
            double actualSum = calculator.add(nonZeroNumber, zeroValue);

            // Then: The result should be the non-zero number.
            assertEquals(expectedSum, actualSum, "Adding 5.0 + 0.0 should equal 5.0");
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
        void shouldAddVariousPrimitiveDoubleCombinations(double firstInput, double secondInput, double expectedSum) {
            // Given: Parameters are provided by @CsvSource for primitive doubles.

            // When: The add method is called with various primitive double combinations.
            double actualSum = calculator.add(firstInput, secondInput);

            // Then: The result should match the expected sum with a small delta for precision.
            assertEquals(expectedSum, actualSum, 0.001, 
                String.format("Adding %.3f + %.3f should equal %.3f", firstInput, secondInput, expectedSum));
        }

        @Test
        @DisplayName("Should throw ArithmeticException when primitive addition would cause overflow")
        void shouldThrowArithmeticExceptionWhenPrimitiveAdditionWouldCauseOverflow() {
            // Given: Two primitive double numbers whose sum exceeds Double.MAX_VALUE.
            double maximumValue = Double.MAX_VALUE;
            double valueToOverflow = Double.MAX_VALUE;

            // When & Then: Attempting to add these primitive doubles should result in an ArithmeticException.
            ArithmeticException exception = assertThrows(ArithmeticException.class, 
                () -> calculator.add(maximumValue, valueToOverflow), 
                "Should throw ArithmeticException when primitive addition would cause overflow");
            
            // Business Logic: The calculator should prevent overflow for primitive double additions.
            assertTrue(exception.getMessage().contains("Addition would cause overflow"));
        }

        @Test
        @DisplayName("Should throw ArithmeticException when primitive addition would cause underflow")
        void shouldThrowArithmeticExceptionWhenPrimitiveAdditionWouldCauseUnderflow() {
            // Given: Two primitive double numbers whose sum goes below -Double.MAX_VALUE.
            double minimumValue = -Double.MAX_VALUE;
            double valueToUnderflow = -Double.MAX_VALUE;

            // When & Then: Attempting to add these primitive doubles should result in an ArithmeticException.
            ArithmeticException exception = assertThrows(ArithmeticException.class, 
                () -> calculator.add(minimumValue, valueToUnderflow), 
                "Should throw ArithmeticException when primitive addition would cause underflow");
            
            // Business Logic: The calculator should prevent underflow for primitive double additions.
            assertTrue(exception.getMessage().contains("Addition would cause underflow"));
        }
    }

    @Nested
    @DisplayName("Boundary Value Tests")
    class BoundaryValueTests {

        @Test
        @DisplayName("Should handle maximum double value minus small number")
        void shouldHandleMaximumDoubleValueMinusSmallNumber() {
            // Given: A double number close to MAX_VALUE and a small positive number.
            Double numberNearMax = Double.MAX_VALUE - 1.0;
            Double smallPositiveNumber = 1.0;
            Double expectedResult = Double.MAX_VALUE;

            // When: Adding the number near max with the small positive number.
            Double actualResult = calculator.add(numberNearMax, smallPositiveNumber);

            // Then: The result should be MAX_VALUE.
            assertEquals(expectedResult, actualResult, "Adding (MAX_VALUE-1) + 1 should equal MAX_VALUE");
        }

        @Test
        @DisplayName("Should handle minimum double value plus small number")
        void shouldHandleMinimumDoubleValuePlusSmallNumber() {
            // Given: A double number close to -MAX_VALUE and a small negative number.
            Double numberNearMin = -Double.MAX_VALUE + 1.0;
            Double smallNegativeNumber = -1.0;
            Double expectedResult = -Double.MAX_VALUE;

            // When: Adding the number near min with the small negative number.
            Double actualResult = calculator.add(numberNearMin, smallNegativeNumber);

            // Then: The result should be -MAX_VALUE.
            assertEquals(expectedResult, actualResult, "Adding (-MAX_VALUE+1) + (-1) should equal -MAX_VALUE");
        }

        @Test
        @DisplayName("Should handle very small positive numbers")
        void shouldHandleVerySmallPositiveNumbers() {
            // Given: Two very small positive double numbers.
            Double firstSmallPositive = Double.MIN_VALUE;
            Double secondSmallPositive = Double.MIN_VALUE;
            Double expectedSum = Double.MIN_VALUE * 2;

            // When: Adding these two very small positive numbers.
            Double actualSum = calculator.add(firstSmallPositive, secondSmallPositive);

            // Then: The result should be their sum.
            assertEquals(expectedSum, actualSum, "Adding MIN_VALUE + MIN_VALUE should equal 2*MIN_VALUE");
        }

        @Test
        @DisplayName("Should handle very small negative numbers")
        void shouldHandleVerySmallNegativeNumbers() {
            // Given: Two very small negative double numbers.
            Double firstSmallNegative = -Double.MIN_VALUE;
            Double secondSmallNegative = -Double.MIN_VALUE;
            Double expectedSum = -Double.MIN_VALUE * 2;

            // When: Adding these two very small negative numbers.
            Double actualSum = calculator.add(firstSmallNegative, secondSmallNegative);

            // Then: The result should be their sum.
            assertEquals(expectedSum, actualSum, "Adding (-MIN_VALUE) + (-MIN_VALUE) should equal -2*MIN_VALUE");
        }

        @Test
        @DisplayName("Should handle epsilon values")
        void shouldHandleEpsilonValues() {
            // Given: Two epsilon values, representing the smallest positive normal double.
            Double firstEpsilon = Double.MIN_NORMAL;
            Double secondEpsilon = Double.MIN_NORMAL;
            Double expectedSum = Double.MIN_NORMAL * 2;

            // When: Adding these two epsilon values.
            Double actualSum = calculator.add(firstEpsilon, secondEpsilon);

            // Then: The result should be their sum.
            assertEquals(expectedSum, actualSum, "Adding MIN_NORMAL + MIN_NORMAL should equal 2*MIN_NORMAL");
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
            // Given: A new instance of the Calculator class is created.
            Calculator actualCalculator = new Calculator();

            // When & Then: Assert that the calculator instance is not null and is of the correct type.
            assertNotNull(actualCalculator, "Calculator instance should not be null");
            assertTrue(actualCalculator instanceof Calculator, "Should be an instance of Calculator");
        }

        @Test
        @DisplayName("Should use mock calculator for testing")
        void shouldUseMockCalculatorForTesting() {
            // Given: The mock calculator is configured to return a specific value for certain inputs.
            Double inputOne = 2.0;
            Double inputTwo = 3.0;
            Double mockedResult = 5.0;
            when(mockCalculator.add(inputOne, inputTwo)).thenReturn(mockedResult);

            // When: The add method is called on the mock calculator.
            double actualResult = mockCalculator.add(inputOne, inputTwo);

            // Then: The result should match the mocked value, and the method call should be verified.
            assertEquals(mockedResult, actualResult, "Mock should return expected value");
            verify(mockCalculator, times(1)).add(inputOne, inputTwo);
        }

        @Test
        @DisplayName("Should verify mock behavior with different inputs")
        void shouldVerifyMockBehaviorWithDifferentInputs() {
            // Given: The mock calculator is configured for two different sets of inputs.
            Double firstInputSetVal1 = 1.0;
            Double firstInputSetVal2 = 2.0;
            Double firstMockedResult = 3.0;
            when(mockCalculator.add(firstInputSetVal1, firstInputSetVal2)).thenReturn(firstMockedResult);

            Double secondInputSetVal1 = 5.0;
            Double secondInputSetVal2 = 3.0;
            Double secondMockedResult = 8.0;
            when(mockCalculator.add(secondInputSetVal1, secondInputSetVal2)).thenReturn(secondMockedResult);

            // When: The add method is called twice with different inputs.
            double actualResult1 = mockCalculator.add(firstInputSetVal1, firstInputSetVal2);
            double actualResult2 = mockCalculator.add(secondInputSetVal1, secondInputSetVal2);

            // Then: Both results should match their respective mocked values, and both calls should be verified once.
            assertEquals(firstMockedResult, actualResult1, "First mock call should return 3.0");
            assertEquals(secondMockedResult, actualResult2, "Second mock call should return 8.0");
            verify(mockCalculator, times(1)).add(firstInputSetVal1, firstInputSetVal2);
            verify(mockCalculator, times(1)).add(secondInputSetVal1, secondInputSetVal2);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very large numbers that don't overflow")
        void shouldHandleVeryLargeNumbersThatDontOverflow() {
            // Given: Two very large numbers whose sum does not exceed Double.MAX_VALUE.
            Double halfMaxValue = Double.MAX_VALUE / 2;
            Double anotherHalfMaxValue = Double.MAX_VALUE / 2;
            Double expectedSum = Double.MAX_VALUE;

            // When: Adding these large numbers.
            Double actualSum = calculator.add(halfMaxValue, anotherHalfMaxValue);

            // Then: The result should be Double.MAX_VALUE without an overflow.
            assertEquals(expectedSum, actualSum, "Adding MAX_VALUE/2 + MAX_VALUE/2 should equal MAX_VALUE");
        }

        @Test
        @DisplayName("Should handle very small numbers that don't underflow")
        void shouldHandleVerySmallNumbersThatDontUnderflow() {
            // Given: Two very small numbers whose sum does not go below -Double.MAX_VALUE.
            Double halfMinNegativeValue = -Double.MAX_VALUE / 2;
            Double anotherHalfMinNegativeValue = -Double.MAX_VALUE / 2;
            Double expectedSum = -Double.MAX_VALUE;

            // When: Adding these small negative numbers.
            Double actualSum = calculator.add(halfMinNegativeValue, anotherHalfMinNegativeValue);

            // Then: The result should be -Double.MAX_VALUE without an underflow.
            assertEquals(expectedSum, actualSum, "Adding (-MAX_VALUE/2) + (-MAX_VALUE/2) should equal -MAX_VALUE");
        }

        @Test
        @DisplayName("Should handle precision loss with very small decimals")
        void shouldHandlePrecisionLossWithVerySmallDecimals() {
            // Given: Two small decimal numbers where precision loss might occur.
            Double firstDecimal = 0.1;
            Double secondDecimal = 0.2;
            Double expectedSum = 0.3;

            // When: Adding these decimal numbers.
            Double actualSum = calculator.add(firstDecimal, secondDecimal);

            // Then: The result should be approximately 0.3, accounting for floating-point precision.
            assertEquals(expectedSum, actualSum, 0.0000001, 
                "Adding 0.1 + 0.2 should equal 0.3 with high precision");
        }

        @Test
        @DisplayName("Should handle mixed precision numbers")
        void shouldHandleMixedPrecisionNumbers() {
            // Given: A whole number and a very small decimal number.
            Double wholeNumber = 1.0;
            Double verySmallDecimal = 0.0000000000000001;
            Double expectedSum = 1.0000000000000001;

            // When: Adding these mixed precision numbers.
            Double actualSum = calculator.add(wholeNumber, verySmallDecimal);

            // Then: The result should maintain the precision of the small decimal.
            assertEquals(expectedSum, actualSum, 0.0000000000000001, 
                "Adding 1.0 + very small number should maintain precision");
        }
    }

    @Nested
    @DisplayName("Performance and Stress Tests")
    class PerformanceAndStressTests {

        @Test
        @DisplayName("Should handle multiple rapid calculations")
        void shouldHandleMultipleRapidCalculations() {
            // Given: A large number of iterations for stress testing.
            int numberOfIterations = 1000;
            double cumulativeSum = 0.0;

            // When: Performing additions repeatedly in a loop.
            for (int i = 0; i < numberOfIterations; i++) {
                double currentResult = calculator.add((double)i, (double)i);
                cumulativeSum += currentResult;
            }

            // Then: The cumulative sum should be greater than zero, indicating successful calculations.
            assertTrue(cumulativeSum > 0, "Should complete multiple calculations successfully");
        }

        @Test
        @DisplayName("Should maintain consistency across multiple calls")
        void shouldMaintainConsistencyAcrossMultipleCalls() {
            // Given: Two input numbers and their expected sum.
            Double firstInput = 2.5;
            Double secondInput = 3.7;
            Double expectedSum = 6.2;

            // When: The add method is called multiple times with the same inputs.
            Double firstCallResult = calculator.add(firstInput, secondInput);
            Double secondCallResult = calculator.add(firstInput, secondInput);
            Double thirdCallResult = calculator.add(firstInput, secondInput);

            // Then: All results should be consistent and equal to the expected sum within a small delta.
            assertEquals(expectedSum, firstCallResult, 0.001, "First call should be correct");
            assertEquals(expectedSum, secondCallResult, 0.001, "Second call should be correct");
            assertEquals(expectedSum, thirdCallResult, 0.001, "Third call should be correct");
            assertEquals(firstCallResult, secondCallResult, "Results should be consistent");
            assertEquals(secondCallResult, thirdCallResult, "Results should be consistent");
        }
    }
} 