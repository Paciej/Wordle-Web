package pl.polsl.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the LetterStatus enum.
 * <p>
 * This test class verifies all public methods of LetterStatus using
 * parameterized tests covering correct, incorrect, and boundary cases.
 * </p>
 *
 * @author Maciej Porebski
 * @version 1.0
 */
class LetterStatusTest {
    
    /**
     * Provides test data for all enum values and their expected properties.
     * <p>
     * Test cases verify each LetterStatus constant with:
     * - Expected symbol
     * - Expected description
     * - Expected color code
     * </p>
     *
     * @return stream of arguments for parameterized tests
     */
    static Stream<Arguments> provideLetterStatusData() {
        return Stream.of(
            Arguments.of(LetterStatus.CORRECT, '+', "Correct position", "#4CAF50"),
            Arguments.of(LetterStatus.WRONG_POSITION, '?', "Wrong position", "#FF9800"),
            Arguments.of(LetterStatus.NOT_PRESENT, '-', "Not in word", "#9E9E9E")
        );
    }
    
    /**
     * Tests getSymbol method for all LetterStatus enum values.
     * <p>
     * Verifies that:
     * - Each enum constant returns correct symbol
     * - Symbol is a single character
     * - Symbol matches expected value
     * </p>
     *
     * @param status         the LetterStatus enum value
     * @param expectedSymbol the expected symbol character
     * @param description    the description (not used in this test but part of data)
     * @param colorCode      the color code (not used in this test but part of data)
     */
    @ParameterizedTest
    @MethodSource("provideLetterStatusData")
    void testGetSymbol(LetterStatus status, char expectedSymbol, 
                       String description, String colorCode) {
        // When
        char actualSymbol = status.getSymbol();
        
        // Then
        assertEquals(expectedSymbol, actualSymbol,
            "Symbol for " + status.name() + " should be '" + expectedSymbol + "'");
        assertNotEquals('\0', actualSymbol,
            "Symbol should not be null character");
    }
    
    /**
     * Tests getDescription method for all LetterStatus enum values.
     * <p>
     * Verifies that:
     * - Each enum constant returns correct description
     * - Description is not null or empty
     * - Description matches expected value
     * </p>
     *
     * @param status              the LetterStatus enum value
     * @param symbol              the symbol (not used in this test)
     * @param expectedDescription the expected description
     * @param colorCode           the color code (not used in this test)
     */
    @ParameterizedTest
    @MethodSource("provideLetterStatusData")
    void testGetDescription(LetterStatus status, char symbol,
                           String expectedDescription, String colorCode) {
        // When
        String actualDescription = status.getDescription();
        
        // Then
        assertEquals(expectedDescription, actualDescription,
            "Description for " + status.name() + " should be '" + expectedDescription + "'");
        assertNotNull(actualDescription,
            "Description should not be null");
        assertFalse(actualDescription.isEmpty(),
            "Description should not be empty");
    }
    
    /**
     * Tests getColorCode method for all LetterStatus enum values.
     * <p>
     * Verifies that:
     * - Each enum constant returns correct color code
     * - Color code is in valid hex format (#RRGGBB)
     * - Color code matches expected value
     * </p>
     *
     * @param status            the LetterStatus enum value
     * @param symbol            the symbol (not used in this test)
     * @param description       the description (not used in this test)
     * @param expectedColorCode the expected hex color code
     */
    @ParameterizedTest
    @MethodSource("provideLetterStatusData")
    void testGetColorCode(LetterStatus status, char symbol,
                         String description, String expectedColorCode) {
        // When
        String actualColorCode = status.getColorCode();
        
        // Then
        assertEquals(expectedColorCode, actualColorCode,
            "Color code for " + status.name() + " should be '" + expectedColorCode + "'");
        assertTrue(actualColorCode.matches("#[0-9A-Fa-f]{6}"),
            "Color code should be in hex format #RRGGBB");
        assertNotNull(actualColorCode,
            "Color code should not be null");
    }
    
    /**
     * Provides test data for fromSymbol method with valid symbols.
     * <p>
     * Tests correct cases where symbols should map to enum values.
     * </p>
     *
     * @return stream of symbol-status pairs
     */
    static Stream<Arguments> provideValidSymbols() {
        return Stream.of(
            Arguments.of('+', LetterStatus.CORRECT),
            Arguments.of('?', LetterStatus.WRONG_POSITION),
            Arguments.of('-', LetterStatus.NOT_PRESENT)
        );
    }
    
    /**
     * Tests fromSymbol method with valid symbol characters.
     * <p>
     * Verifies that:
     * - Valid symbols correctly map to enum constants
     * - Method returns exact enum value
     * - Enum instances are the same (singleton pattern)
     * </p>
     *
     * @param symbol         the input symbol character
     * @param expectedStatus the expected LetterStatus enum value
     */
    @ParameterizedTest
    @MethodSource("provideValidSymbols")
    void testFromSymbolValidCases(char symbol, LetterStatus expectedStatus) {
        // When
        LetterStatus actualStatus = LetterStatus.fromSymbol(symbol);
        
        // Then
        assertEquals(expectedStatus, actualStatus,
            "Symbol '" + symbol + "' should map to " + expectedStatus.name());
        assertSame(expectedStatus, actualStatus,
            "Should return the same enum instance");
    }
    
    /**
     * Provides test data for fromSymbol method with invalid symbols.
     * <p>
     * Tests incorrect/boundary cases that should throw exceptions:
     * - Invalid characters
     * - Digits
     * - Special characters not used by enum
     * - Whitespace
     * - Letters
     * </p>
     *
     * @return stream of invalid symbols
     */
    static Stream<Character> provideInvalidSymbols() {
        return Stream.of(
            'x',   // Invalid letter
            'A',   // Uppercase letter
            '0',   // Digit
            ' ',   // Space (boundary)
            '!',   // Invalid special char
            '@',   // Invalid special char
            '\n',  // Newline (boundary)
            '\0'   // Null character (boundary)
        );
    }
    
    /**
     * Tests fromSymbol method with invalid symbols that should throw exceptions.
     * <p>
     * Verifies that:
     * - Invalid symbols throw IllegalArgumentException
     * - Exception message is meaningful
     * - Method doesn't return null or wrong values
     * </p>
     *
     * @param invalidSymbol the invalid symbol character to test
     */
    @ParameterizedTest
    @MethodSource("provideInvalidSymbols")
    void testFromSymbolInvalidCases(char invalidSymbol) {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> LetterStatus.fromSymbol(invalidSymbol),
            "Should throw IllegalArgumentException for invalid symbol '" + invalidSymbol + "'"
        );
        
        // Verify exception message contains the invalid symbol
        assertTrue(exception.getMessage().contains(String.valueOf(invalidSymbol))
                || exception.getMessage().contains("Unknown symbol"),
            "Exception message should mention the invalid symbol or 'Unknown symbol'");
    }
    
    /**
     * Tests toString method for all LetterStatus enum values.
     * <p>
     * Verifies that:
     * - toString returns symbol as string
     * - Length is exactly 1
     * - Matches getSymbol result
     * </p>
     *
     * @param status      the LetterStatus enum value
     * @param symbol      the expected symbol
     * @param description the description (not used)
     * @param colorCode   the color code (not used)
     */
    @ParameterizedTest
    @MethodSource("provideLetterStatusData")
    void testToString(LetterStatus status, char symbol,
                     String description, String colorCode) {
        // When
        String result = status.toString();
        
        // Then
        assertNotNull(result,
            "toString should not return null");
        assertEquals(1, result.length(),
            "toString should return single character");
        assertEquals(String.valueOf(symbol), result,
            "toString should return symbol as string");
        assertEquals(String.valueOf(status.getSymbol()), result,
            "toString should match getSymbol");
    }
    
    /**
     * Tests that all enum values are accessible.
     * <p>
     * Verifies that:
     * - Enum has exactly 3 values
     * - All expected constants exist
     * - values() returns non-null array
     * </p>
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void testEnumValues(int index) {
        // Given
        LetterStatus[] values = LetterStatus.values();
        
        // Then
        assertNotNull(values,
            "values() should not return null");
        assertEquals(3, values.length,
            "Should have exactly 3 enum constants");
        assertNotNull(values[index],
            "Enum value at index " + index + " should not be null");
    }
    
    /**
     * Provides test data for valueOf method.
     *
     * @return stream of enum name strings
     */
    static Stream<String> provideValidEnumNames() {
        return Stream.of("CORRECT", "WRONG_POSITION", "NOT_PRESENT");
    }
    
    /**
     * Tests valueOf method with valid enum names.
     * <p>
     * Verifies that:
     * - Valid names return correct enum constants
     * - Case sensitivity is enforced
     * - Returned instances are correct
     * </p>
     *
     * @param enumName the name of the enum constant
     */
    @ParameterizedTest
    @MethodSource("provideValidEnumNames")
    void testValueOfValidNames(String enumName) {
        // When
        LetterStatus status = LetterStatus.valueOf(enumName);
        
        // Then
        assertNotNull(status,
            "valueOf should return non-null for valid name");
        assertEquals(enumName, status.name(),
            "Enum name should match input");
    }
    
    /**
     * Provides test data for valueOf with invalid names.
     *
     * @return stream of invalid enum names
     */
    static Stream<String> provideInvalidEnumNames() {
        return Stream.of(
            "correct",         // Lowercase (boundary)
            "INVALID",         // Non-existent
            "",               // Empty string (boundary)
            "CORRECT ",       // With space (boundary)
            "null",           // String "null" (boundary)
            "WRONG-POSITION"  // Wrong separator
        );
    }
    
    /**
     * Tests valueOf method with invalid enum names.
     * <p>
     * Verifies that:
     * - Invalid names throw IllegalArgumentException
     * - Case sensitivity is enforced
     * - Empty and malformed names are rejected
     * </p>
     *
     * @param invalidName the invalid enum name
     */
    @ParameterizedTest
    @MethodSource("provideInvalidEnumNames")
    void testValueOfInvalidNames(String invalidName) {
        // When & Then
        assertThrows(IllegalArgumentException.class,
            () -> LetterStatus.valueOf(invalidName),
            "valueOf should throw IllegalArgumentException for invalid name: " + invalidName);
    }
    
    /**
     * Tests fromSymbol with symbol comparison - boundary case.
     * <p>
     * Verifies that:
     * - Same symbols return same enum instance
     * - Different symbols return different enum instances
     * - Enum singleton pattern is preserved
     * </p>
     *
     * @param symbol1     first symbol to compare
     * @param symbol2     second symbol to compare
     * @param shouldMatch whether symbols should match
     */
    @ParameterizedTest
    @CsvSource({
        "+, +, true",   // Same symbol
        "?, ?, true",   // Same symbol
        "-, -, true",   // Same symbol
        "+, -, false",  // Different symbols
        "?, +, false"   // Different symbols
    })
    void testSymbolComparison(char symbol1, char symbol2, boolean shouldMatch) {
        // When
        LetterStatus status1 = LetterStatus.fromSymbol(symbol1);
        LetterStatus status2 = LetterStatus.fromSymbol(symbol2);
        
        // Then
        if (shouldMatch) {
            assertSame(status1, status2,
                "Same symbols should return same enum instance");
            assertEquals(status1.getSymbol(), status2.getSymbol(),
                "Symbols should match");
        } else {
            assertNotSame(status1, status2,
                "Different symbols should return different enum instances");
            assertNotEquals(status1.getSymbol(), status2.getSymbol(),
                "Symbols should be different");
        }
    }
    
    /**
     * Tests that fromSymbol correctly identifies all enum values.
     * <p>
     * Verifies complete coverage - every enum value can be retrieved via its symbol.
     * This is a boundary test ensuring no enum value is unreachable.
     * </p>
     *
     * @param status         the LetterStatus enum value
     * @param expectedSymbol the symbol for this status
     * @param description    description (unused)
     * @param colorCode      color code (unused)
     */
    @ParameterizedTest
    @MethodSource("provideLetterStatusData")
    void testFromSymbolCompleteness(LetterStatus status, char expectedSymbol,
                                    String description, String colorCode) {
        // When
        LetterStatus retrievedStatus = LetterStatus.fromSymbol(expectedSymbol);
        
        // Then
        assertSame(status, retrievedStatus,
            "fromSymbol should return the correct enum value for symbol '" + expectedSymbol + "'");
        assertEquals(expectedSymbol, retrievedStatus.getSymbol(),
            "Retrieved status should have matching symbol");
    }
    
    /**
     * Tests boundary case: verifying enum ordinal values.
     * <p>
     * Ensures enum constants maintain expected order and ordinal values.
     * This is important if code relies on ordinal positioning.
     * </p>
     */
    @ParameterizedTest
    @CsvSource({
        "CORRECT, 0",
        "WRONG_POSITION, 1",
        "NOT_PRESENT, 2"
    })
    void testEnumOrdinals(String enumName, int expectedOrdinal) {
        // When
        LetterStatus status = LetterStatus.valueOf(enumName);
        
        // Then
        assertEquals(expectedOrdinal, status.ordinal(),
            enumName + " should have ordinal " + expectedOrdinal);
    }
}
