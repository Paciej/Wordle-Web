package pl.polsl.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InvalidGuessException class.
 * Tests verify that the message passed to the constructor
 * is correctly stored and returned by getMessage().
 *
 * @author Maciej Porebski
 * @version 1.0
 */
public class InvalidGuessExceptionTest {

    /**
     * Tests constructor of InvalidGuessException with various messages.
     * Verifies that getMessage() returns the same text as passed to constructor.
     *
     * @param message message passed to the exception constructor
     */
    @ParameterizedTest(name = "Exception message \"{0}\" should be stored correctly")
    @CsvSource({
        "'Simple message'",
        "'Message with spaces inside'",
        "'12345'",
        "'!@#$%^&*()'",
        "'',"
    })
    public void testConstructorStoresMessage(String message) {
        InvalidGuessException exception = new InvalidGuessException(message);

        assertEquals(message, exception.getMessage(),
                "Exception should return the same message that was passed to constructor");
    }
}